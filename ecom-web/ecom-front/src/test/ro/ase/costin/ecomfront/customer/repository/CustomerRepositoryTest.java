package ro.ase.costin.ecomfront.customer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.data.AuthenticationType;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomfront.customer.CustomerRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Disabled("Customer already created")
    void testCreateCustomer1() {
        Integer countryId = 234; // USA
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("David");
        customer.setLastName("Fountaine");
        customer.setPassword("password123");
        customer.setEmail("david.s.fountaine@gmail.com");
        customer.setPhoneNumber("312-462-7518");
        customer.setAddressLine1("1927  West Drive");
        customer.setCity("Sacramento");
        customer.setState("California");
        customer.setPostalCode("95867");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isPositive();
    }

    @Test
    @Disabled("Skip creating this customer")
    void testCreateCustomer2() {
        Integer countryId = 106; // India
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Sanya");
        customer.setLastName("Lad");
        customer.setPassword("password456");
        customer.setEmail("sanya.lad2020@gmail.com");
        customer.setPhoneNumber("02224928052");
        customer.setAddressLine1("173 , A-, Shah & Nahar Indl.estate, Sunmill Road");
        customer.setAddressLine2("Dhanraj Mill Compound, Lower Parel (west)");
        customer.setCity("Mumbai");
        customer.setState("Maharashtra");
        customer.setPostalCode("400013");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isPositive();
    }

    @Test
    void testListCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out::println);
        assertThat(customers).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void testUpdateCustomer() {
        Integer customerId = 1;
        String lastName = "Stanfield";
        Customer customer = customerRepository.findById(customerId).orElse(new Customer());
        customer.setLastName(lastName);
        customer.setEnabled(true);
        Customer updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    void testGetCustomer() {
        Integer customerId = 1;
        Optional<Customer> findById = customerRepository.findById(customerId);

        assertThat(findById).isPresent();

        Customer customer = findById.get();
        System.out.println(customer);
    }

    @Test
    @Disabled("No delete method to run")
    void testDeleteCustomer() {
        Integer customerId = 2;
        customerRepository.deleteById(customerId);
        Optional<Customer> findById = customerRepository.findById(customerId);
        assertThat(findById).isNotPresent();
    }

    @Test
    void testFindByEmail() {
        String email = "david.s.fountaine@gmail.com";
        Customer customer = customerRepository.findByEmail(email);
        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    @Disabled
    void testFindByVerificationCode() {
        String code = "code_123";
        Customer customer = customerRepository.findByVerificationCode(code);
        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    void testEnableCustomer() {
        Integer customerId = 1;
        customerRepository.enable(customerId);
        Customer customer = customerRepository.findById(customerId).orElse(new Customer());
        assertThat(customer.isEnabled()).isTrue();
    }

    @Test
    void testUpdateAuthenticationType() {
        Integer id = 1;
        customerRepository.updateAuthenticationType(id, AuthenticationType.DATABASE);
        Customer customer = customerRepository.findById(id).orElseGet(Customer::new);
        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
    }
}