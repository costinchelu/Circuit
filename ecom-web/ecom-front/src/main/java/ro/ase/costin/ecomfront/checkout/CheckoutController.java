package ro.ase.costin.ecomfront.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ro.ase.costin.ecomcommon.data.EmailSettingBag;
import ro.ase.costin.ecomcommon.data.PaymentMethod;
import ro.ase.costin.ecomcommon.data.PaymentSettingBag;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.CartItem;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Order;
import ro.ase.costin.ecomcommon.entity.ShippingRate;
import ro.ase.costin.ecomfront.address.AddressService;
import ro.ase.costin.ecomfront.checkout.paypal.PayPalApiException;
import ro.ase.costin.ecomfront.checkout.paypal.PayPalService;
import ro.ase.costin.ecomfront.order.OrderService;
import ro.ase.costin.ecomcommon.data.CurrencySettingBag;
import ro.ase.costin.ecomfront.setting.SettingService;
import ro.ase.costin.ecomfront.shipping.ShippingRateService;
import ro.ase.costin.ecomfront.shoppingcart.ShoppingCartService;
import ro.ase.costin.ecomfront.utils.ControllerHelper;
import ro.ase.costin.ecomfront.utils.Utility;

@Controller
@AllArgsConstructor
public class CheckoutController {

    private CheckoutService checkoutService;

    private AddressService addressService;

    private ShippingRateService shippingRateService;

    private ShoppingCartService shoppingCartService;

    private OrderService orderService;

    private SettingService settingService;

    private PayPalService paypalService;

    private ControllerHelper controllerHelper;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String paypalClientId = paymentSettings.getClientID();

        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("customer", customer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Address defaultAddress = addressService.getDefaultAddress(customer);

        ShippingRate shippingRate;
        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, createdOrder);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws UnsupportedEncodingException, MessagingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getShippingAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);
        mailSender.send(message);
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model) throws UnsupportedEncodingException, MessagingException {
        String orderId = request.getParameter("orderId");
        String pageTitle = "Eroare checkout";
        String message;

        try {
            if (paypalService.validateOrder(orderId)) {
                return placeOrder(request);
            } else {
                pageTitle = "Eroare checkout";
                message = "EROARE: Tranzacția nu a putut fi finalizată (detaliile comenzii sunt invalide)";
            }
        } catch (PayPalApiException e) {
            message = "EROARE: Tranzacția a eșuat datorită unei erori: " + e.getMessage();
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("title", pageTitle);
        model.addAttribute("message", message);
        return "message";
    }
    // TODO: Card payments should create a CREDIT_CARD OrderType
}
