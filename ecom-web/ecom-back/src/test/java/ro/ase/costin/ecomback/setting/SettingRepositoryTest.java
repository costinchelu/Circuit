package ro.ase.costin.ecomback.setting;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomback.setting.repository.SettingRepository;
import ro.ase.costin.ecomcommon.entity.Setting;
import ro.ase.costin.ecomcommon.data.SettingCategory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
@Disabled("Integration tests")
class SettingRepositoryTest {

    @Autowired
    SettingRepository settingRepository;

    @Test
    void testCreateGeneralSettings() {
//        Setting siteName = new Setting("SITE_NAME", "Circuit", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "shop_500px.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "(c) 2022 Circuit E-Store", SettingCategory.GENERAL);

        settingRepository.saveAll(List.of(siteLogo, copyright));
        Iterable<Setting> result = settingRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType));
    }

    @Test
    void testListSettingsByCategory() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.GENERAL);
        settings.forEach(System.out::println);
    }
}