package ro.ase.costin.ecomback.shipping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.ase.costin.ecomback.exception.ShippingRateNotFoundException;
import ro.ase.costin.ecomback.product.ProductRepository;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.ShippingRate;
import ro.ase.costin.ecomcommon.exception.ProductNotFoundException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class ShippingRateServiceTest {

    @MockBean
    private ShippingRateRepository shippingRateRepository;

    @MockBean
    private ProductRepository productRepository;

    @InjectMocks
    private ShippingRateService shippingRateService;

    @Test
    void testCalculateShippingCost_NoRateFound() {
        Integer productId = 1;
        Integer countryId = 189;
        String state = "NO STATE";
        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(null);

        assertThrows(ShippingRateNotFoundException.class,
                () -> shippingRateService.calculateShippingCost(productId, countryId, state));
    }

    @Test
    void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException, ProductNotFoundException {
        Integer productId = 1;
        Integer countryId = 189;
        String state = "Bucharest";

        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(10);
        Mockito.when(shippingRateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);

        Product product = new Product();
        product.setWeight(5);
        product.setWidth(4);
        product.setHeight(3);
        product.setLength(8);
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        float shippingCost = shippingRateService.calculateShippingCost(productId, countryId, state);

        assertEquals(50, shippingCost);
    }
}
