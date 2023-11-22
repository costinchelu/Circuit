package ro.ase.costin.ecomcommon.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class AddressWCountryBasedEntity extends AddressBasedEntity {

    @ManyToOne
    @JoinColumn(name = "country_id")
    protected Country country;

    @Override
    public String toString() {
        StringBuilder address = new StringBuilder(firstName);
        if (StringUtils.isNotBlank(lastName))  address.append(" ").append(lastName);
        if (StringUtils.isNotBlank(addressLine1))  address.append(", ").append(addressLine1);
        if (StringUtils.isNotBlank(addressLine2))  address.append(", ").append(addressLine2);
        if (StringUtils.isNotBlank(city))  address.append(", ").append(city);
        if (StringUtils.isNotBlank(state))  address.append(", ").append(state);
        address.append(", ").append(country.getName());
        if (StringUtils.isNotBlank(postalCode)) address.append(". Cod poștal: ").append(postalCode);
        if (StringUtils.isNotBlank(phoneNumber)) address.append(". Telefon: ").append(phoneNumber);
        return address.toString();
    }
}

