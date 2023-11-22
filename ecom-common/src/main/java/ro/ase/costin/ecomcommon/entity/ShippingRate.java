package ro.ase.costin.ecomcommon.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shipping_rates")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class ShippingRate extends IdBasedEntity {

    private float rate;

    private int days;

    // cash on delivery
    @Column(name = "cod_supported")
    private boolean codSupported;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, length = 45)
    private String state;

    @Override
    public String toString() {
        return "Livrare [id=" + id + ", preț=" + rate + ", zile=" + days + ", plata la livrare=" + codSupported
                + ", țară=" + country.getName() + ", regiune=" + state + "]";
    }
}
