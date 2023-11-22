package ro.ase.costin.ecomfront.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.data.OrderReturnRequest;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.data.PaymentMethod;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.CartItem;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Order;
import ro.ase.costin.ecomcommon.entity.OrderDetail;
import ro.ase.costin.ecomcommon.entity.OrderTrack;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.exception.OrderNotFoundException;
import ro.ase.costin.ecomfront.checkout.CheckoutInfo;


@Service
@RequiredArgsConstructor
public class OrderService {

    public static final int ORDERS_PER_PAGE = 8;

    @NonNull
    private OrderRepository orderRepository;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            newOrder.setStatus(OrderStatus.PLATITA);
        } else {
            newOrder.setStatus(OrderStatus.NOUA);
        }
        newOrder.setStatus(OrderStatus.NOUA);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        cartItems.forEach(cartItem -> {
            Product product = cartItem.getProduct();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        });

        OrderTrack track = new OrderTrack();
        track.setOrder(newOrder);
        track.setStatus(OrderStatus.NOUA);
        track.setNotes(OrderStatus.NOUA.defaultDescription());
        track.setUpdatedTime(new Date());
        newOrder.getOrderTracks().add(track);

        return orderRepository.save(newOrder);
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        if (keyword != null) {
            return orderRepository.findAll(keyword, customer.getId(), pageable);
        }
        return orderRepository.findAll(customer.getId(), pageable);
    }

    public Order getOrder(Integer id, Customer customer) throws OrderNotFoundException {
        return orderRepository.findByIdAndCustomer(id, customer)
                .orElseThrow(() -> new OrderNotFoundException("Comanda (ID: " + id + ") nu a fost găsită"));
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = orderRepository.findByIdAndCustomer(request.getOrderId(), customer)
                .orElseThrow(() -> new OrderNotFoundException("Comanda (ID: " + request.getOrderId() + ") nu a fost găsită"));

        if (order.isReturnRequested()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETUR_SOLICITAT);

        String notes = "Reason: " + request.getReason();
        if (StringUtils.isNotBlank(request.getNote())) {
            notes += ". " + request.getNote();
        }
        track.setNotes(notes);
        order.getOrderTracks().add(track);
        order.setStatus(OrderStatus.RETUR_SOLICITAT);
        orderRepository.save(order);
        // TODO order should not be returned if older than one month
        // TODO an email to the company should be triggered as a request to return
    }
}
