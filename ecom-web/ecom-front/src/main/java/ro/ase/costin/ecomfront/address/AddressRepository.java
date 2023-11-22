package ro.ase.costin.ecomfront.address;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.Customer;

public interface AddressRepository extends CrudRepository<Address, Integer> {

    List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    Address findByIdAndCustomer(Integer addressId, Integer customerId);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    void deleteByIdAndCustomer(Integer addressId, Integer customerId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
    void setDefaultAddress(Integer id);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != ?1 AND a.customer.id = ?2")
    void setNonDefaultForOthers(Integer defaultAddressId, Integer customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultForShipping = true")
    Address findDefaultByCustomer(Integer customerId);
}
