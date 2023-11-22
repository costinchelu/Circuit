package ro.ase.costin.ecomcommon.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.data.PaymentMethod;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends AddressBasedEntity {

    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;

    private float shippingCost;

    private float productCost;

    private float subtotal;

    private float tax;

    private float total;

    private int deliverDays;

    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setCountry(customer.getCountry().getName());
        setPostalCode(customer.getPostalCode());
        setState(customer.getState());
    }

    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setCountry(address.getCountry().getName());
        setPostalCode(address.getPostalCode());
        setState(address.getState());
    }

    @Transient
    public String getDestination() {
        String destination = "";
        if (StringUtils.isNotBlank(state)) {
            destination += state + ", ";
        } else {
            destination =  city + ", ";
        }
        destination += country;
        return destination;
    }

    @Transient
    public String getShippingAddress() {
        String address = firstName;
        if (StringUtils.isNotBlank(lastName)) address += " " + lastName;
        if (StringUtils.isNotBlank(addressLine1)) address += ", " + addressLine1;
        if (StringUtils.isNotBlank(addressLine2)) address += ", " + addressLine2;
        if (StringUtils.isNotBlank(city)) address += ", " + city;
        if (StringUtils.isNotBlank(state)) address += ", " + state;
        address += ", " + country;
        if (StringUtils.isNotBlank(postalCode)) address += ". Cod poștal: " + postalCode;
        if (StringUtils.isNotBlank(phoneNumber)) address += ". Telefon: " + phoneNumber;
        return address;
    }

    @Transient
    public String getDeliverDateOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(this.deliverDate);
    }

    public void setDeliverDateOnForm(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.deliverDate = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Transient
    public String getRecipientName() {
        String name = firstName;
        if (StringUtils.isNotBlank(lastName)) name += " " + lastName;
        return name;
    }

    @Transient
    public String getRecipientAddress() {
        String address = addressLine1;
        if (StringUtils.isNotBlank(addressLine2)) address += ", " + addressLine2;
        if (StringUtils.isNotBlank(city)) address += ", " + city;
        if (StringUtils.isNotBlank(state)) address += ", " + state;
        address += ", " + country;
        if (StringUtils.isNotBlank(postalCode)) address += ". " + postalCode;
        return address;
    }

    @Transient
    public boolean isCOD() {
        return paymentMethod.equals(PaymentMethod.COD);
    }

    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESARE);
    }

    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.RIDICATA);
    }

    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.LIVRARE);
    }

    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.LIVRATA);
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNATA);
    }

    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETUR_SOLICITAT);
    }

    @Transient
    public String getProductNames() {
        StringBuilder productNames = new StringBuilder("<ul>");
        for (OrderDetail detail : orderDetails) {
            productNames.append("<li>").append(detail.getProduct().getShortName()).append("</li>");
        }
        productNames.append("</ul>");
        return productNames.toString();
    }

    @Transient
    public Date deliveredPlusOneMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(deliverDate);
        c.add(Calendar.MONTH, +1);
        return c.getTime();
    }

    public boolean hasStatus(OrderStatus status) {
        for (OrderTrack aTrack : orderTracks) {
            if (aTrack.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Comanda [id=" + id + ", subtotal=" + subtotal + ", metoda de plată=" + paymentMethod + ", stare=" + status
                + ", client=" + customer.getFullName() + "]";
    }
}
