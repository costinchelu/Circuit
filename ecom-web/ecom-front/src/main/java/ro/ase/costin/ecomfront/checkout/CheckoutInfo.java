package ro.ase.costin.ecomfront.checkout;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class CheckoutInfo {

    private float productCost;

    private float productTotal;

    private float shippingCostTotal;

    private float paymentTotal;

    private int deliverDays;

    private boolean codSupported;

    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    public String getPaymentTotal4PayPal() {
        DecimalFormat formatter = new DecimalFormat("##.##");
        return formatter.format(paymentTotal);
    }
}
