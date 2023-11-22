package ro.ase.costin.ecomfront.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.ShippingRate;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Disabled("Integration tests")
class ShippingRateRepositoryTest {

    @Autowired private ShippingRateRepository shippingRateRepository;

    @Test
    void testFindByCountryAndState() {
        Country usa = new Country(234);
        String state = "New York";
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(usa, state);
        assertThat(shippingRate).isNotNull();
    }
}
