package ro.ase.costin.ecomfront.shipping;

import org.springframework.data.repository.CrudRepository;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.ShippingRate;


public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {

    ShippingRate findByCountryAndState(Country country, String state);
}
