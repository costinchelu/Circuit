package ro.ase.costin.ecomfront.checkout;

import java.util.List;

import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.CartItem;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.ShippingRate;

@Service
public class CheckoutService {

    private static final int DIM_DIVISOR = 5000;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        CheckoutInfo checkoutInfo = new CheckoutInfo();
        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());
        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = Math.max(product.getWeight(), dimWeight);
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();
            item.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }
        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }
        return cost;
    }
}
