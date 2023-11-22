package ro.ase.costin.ecomback.setting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.State;

import java.util.List;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {

    List<State> findByCountryOrderByNameAsc(Country country);
}
