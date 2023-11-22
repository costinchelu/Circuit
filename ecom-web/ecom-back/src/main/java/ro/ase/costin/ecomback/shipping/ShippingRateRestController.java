package ro.ase.costin.ecomback.shipping;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomback.exception.ShippingRateNotFoundException;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;

@RestController
@AllArgsConstructor
@Slf4j
public class ShippingRateRestController {

    private ShippingRateService shippingRateService;

    @PostMapping("/get_shipping_cost")
    public String getShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException, ProductNotFoundException {
        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);
        return String.valueOf(shippingCost);
    }
}
