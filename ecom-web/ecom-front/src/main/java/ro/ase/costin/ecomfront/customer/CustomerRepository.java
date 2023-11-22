package ro.ase.costin.ecomfront.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.data.AuthenticationType;
import ro.ase.costin.ecomcommon.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
    Customer findByVerificationCode(String code);

    @Modifying
    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    void enable(Integer id);

    @Modifying
    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    void updateAuthenticationType(Integer customerId, AuthenticationType type);

    Customer findByResetPasswordToken(String token);
}
