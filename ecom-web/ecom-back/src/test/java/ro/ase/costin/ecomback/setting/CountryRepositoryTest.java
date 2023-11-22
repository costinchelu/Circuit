package ro.ase.costin.ecomback.setting;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomback.setting.repository.CountryRepository;
import ro.ase.costin.ecomcommon.entity.Country;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class CountryRepositoryTest {

    @Autowired private CountryRepository countryRepository;

    @Test
    void testCreateCountry() {
        Country country = countryRepository.save(new Country("China", "CN"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isPositive();
    }

    @Test
    void testListCountries() {
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);
        assertThat(listCountries).isNotEmpty();
    }

    @Test
    @Disabled
    void testUpdateCountry() {
        Integer id = 1;
        String name = "Republic of India";

        Country country = countryRepository.findById(id).get();
        country.setName(name);

        Country updatedCountry = countryRepository.save(country);
        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    @Disabled
    void testGetCountry() {
        Integer id = 3;
        Country country = countryRepository.findById(id).get();
        assertThat(country).isNotNull();
    }

    @Test
    @Disabled
    void testDeleteCountry() {
        Integer id = 5;
        countryRepository.deleteById(id);
        Optional<Country> findById = countryRepository.findById(id);
        assertThat(findById.isEmpty());
    }
}