package ro.ase.costin.ecomcommon.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem extends IdBasedEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Transient
    private float shippingCost;

    @Transient
    public float getSubtotal() {
        return product.getDiscountPrice() * quantity;
    }

    @Override
    public String toString() {
        return "Obiect comandă [id=" + id + ", client=" + customer.getFullName() + ", produs=" + product.getShortName()
                + ", cantitate=" + quantity + "]";
    }
}
