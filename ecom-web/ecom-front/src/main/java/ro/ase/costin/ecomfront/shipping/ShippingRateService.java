package ro.ase.costin.ecomfront.shipping;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.ShippingRate;

@Service
@AllArgsConstructor
public class ShippingRateService {

    private ShippingRateRepository shippingRateRepository;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (StringUtils.isBlank(state)) {
            state = customer.getCity();
        }
        return shippingRateRepository.findByCountryAndState(customer.getCountry(), state);
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (StringUtils.isBlank(state)) {
            state = address.getCity();
        }
        return shippingRateRepository.findByCountryAndState(address.getCountry(), state);
    }
}
