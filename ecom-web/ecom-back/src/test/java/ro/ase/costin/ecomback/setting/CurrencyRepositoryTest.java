package ro.ase.costin.ecomback.setting;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomback.setting.repository.CurrencyRepository;
import ro.ase.costin.ecomcommon.entity.Currency;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@Disabled("Integration tests")
class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    void testCreateCurrencies() {
        List<Currency> listCurrencies = Arrays.asList(
                new Currency("Leu", "RL", "RON"),
                new Currency("Euro", "€", "EUR"),
                new Currency("United States Dollar", "$", "USD"),
                new Currency("British Pound", "£", "GPB"),
                new Currency("Moldavian Leu", "ML", "MLE"),
                new Currency("Bulgarian Leva", "BL", "LEV"),
                new Currency("Hungarian Forint", "F", "HUN"),
                new Currency("Swedish Crown", "SC", "SWE"),
                new Currency("Japanese Yen", "¥", "JPY"),
                new Currency("Chinese Yuan", "¥", "CNY")
        );
        currencyRepository.saveAll(listCurrencies);
        Iterable<Currency> iterable = currencyRepository.findAll();
        assertThat(iterable).size().isNotNull();
    }

    @Test
    void testListAllOrderByNameAsc() {
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();
        currencies.forEach(System.out::println);
        assertThat(currencies).isNotEmpty();
    }
}