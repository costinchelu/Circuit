package ro.ase.costin.ecomback.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.ShippingRate;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class ShippingRateRepositoryTest {

    @Autowired private ShippingRateRepository shippingRateRepository;

    @Autowired private TestEntityManager em;

    @Test
    void testCreateNew() {
        Country romania = new Country(190);
        ShippingRate newRate = new ShippingRate();
        newRate.setCountry(romania);
        newRate.setState("Vlașca");
        newRate.setRate(8.25f);
        newRate.setDays(3);
        newRate.setCodSupported(true);
        ShippingRate savedRate = shippingRateRepository.save(newRate);
        assertThat(savedRate).isNotNull();
        assertThat(savedRate.getId()).isPositive();
    }

    @Test
    void testUpdate() {
        Integer rateId = 3000;
        ShippingRate rate = em.find(ShippingRate.class, rateId);
        rate.setRate(9.15f);
        rate.setDays(2);
        ShippingRate updatedRate = shippingRateRepository.save(rate);
        assertThat(updatedRate.getRate()).isEqualTo(9.15f);
        assertThat(updatedRate.getDays()).isEqualTo(2);
    }

    @Test
    void testFindAll() {
        List<ShippingRate> rates = (List<ShippingRate>) shippingRateRepository.findAll();
        assertThat(rates).isNotEmpty();
        rates.forEach(System.out::println);
    }

    @Test
    void testFindByCountryAndState() {
        Integer countryId = 190;
        String state = "Vlașca";
        ShippingRate rate = shippingRateRepository.findByCountryAndState(countryId, state);
        assertThat(rate).isNotNull();
        System.out.println(rate);
    }

    @Test
    void testUpdateCODSupport() {
        Integer rateId = 3000;
        shippingRateRepository.updateCODSupport(rateId, false);
        ShippingRate rate = em.find(ShippingRate.class, rateId);
        assertThat(rate.isCodSupported()).isFalse();
    }

    @Test
    void testDelete() {
        Integer rateId = 3000;
        shippingRateRepository.deleteById(rateId);
        ShippingRate rate = em.find(ShippingRate.class, rateId);
        assertThat(rate).isNull();
    }
}