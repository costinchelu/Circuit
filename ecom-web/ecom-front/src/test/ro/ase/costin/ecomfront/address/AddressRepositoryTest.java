package ro.ase.costin.ecomfront.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Customer;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class AddressRepositoryTest {

    @Autowired private AddressRepository addressRepository;

    @Test
    void testAddNew() {
        Integer customerId = 5;
        Integer countryId = 234; // USA
        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("John");
        newAddress.setLastName("Doe");
        newAddress.setPhoneNumber("646-232-3902");
        newAddress.setAddressLine1("204 Morningview Lane");
        newAddress.setCity("New York");
        newAddress.setState("New York");
        newAddress.setPostalCode("10013");
        Address savedAddress = addressRepository.save(newAddress);
        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isPositive();
    }

    @Test
    void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> listAddresses = addressRepository.findByCustomer(new Customer(customerId));
        assertThat(listAddresses).isNotEmpty();
        listAddresses.forEach(System.out::println);
    }

    @Test
    void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;
        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    void testUpdate() {
        Integer addressId = 1;
        String phoneNumber = "646-232-3932";
        Address address = addressRepository.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);
        Address updatedAddress = addressRepository.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    void testSetDefault() {
        Integer addressId = 8;
        addressRepository.setDefaultAddress(addressId);

        Address address = addressRepository.findById(addressId).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    void testSetNonDefaultAddresses() {
        Integer addressId = 8;
        Integer customerId = 5;
        addressRepository.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    void testGetDefault() {
        Integer customerId = 5;
        Address address = addressRepository.findDefaultByCustomer(customerId);
        assertThat(address).isNotNull();
    }
}
