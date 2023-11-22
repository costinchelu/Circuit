package ro.ase.costin.ecomfront.setting;

import org.springframework.data.repository.CrudRepository;
import ro.ase.costin.ecomcommon.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {

}
