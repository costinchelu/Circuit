package ro.ase.costin.ecomfront.setting;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.data.CurrencySettingBag;
import ro.ase.costin.ecomcommon.data.EmailSettingBag;
import ro.ase.costin.ecomcommon.data.PaymentSettingBag;
import ro.ase.costin.ecomcommon.entity.Currency;
import ro.ase.costin.ecomcommon.entity.Setting;
import ro.ase.costin.ecomcommon.data.SettingCategory;

import java.util.List;

@Service
@AllArgsConstructor
public class SettingService {

    private SettingRepository settingRepository;

    private CurrencyRepository currencyRepository;

    public List<Setting> getGeneralSettings() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));
        return new EmailSettingBag(settings);
    }
    public CurrencySettingBag getCurrencySettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(settings);
    }

    public PaymentSettingBag getPaymentSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(settings);
    }

    public String getCurrencyCode() {
        Setting setting = settingRepository.findByKey("CURRENCY_ID");
        Integer currencyId = Integer.parseInt(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();
        return currency.getCode();
    }
}
