package ro.ase.costin.ecomfront.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.CartItem;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.ShippingRate;
import ro.ase.costin.ecomfront.address.AddressService;
import ro.ase.costin.ecomfront.shipping.ShippingRateService;
import ro.ase.costin.ecomfront.utils.ControllerHelper;


@Controller
@AllArgsConstructor
public class ShoppingCartController {

    private ShoppingCartService cartService;

    private AddressService addressService;

    private ShippingRateService shippingRateService;

    private ControllerHelper controllerHelper;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        float estimatedTotal = cartItems.stream().map(CartItem::getSubtotal).reduce(0.0f, Float::sum);
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate;

        boolean usePrimaryAddressAsDefault = false;
        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }
}
