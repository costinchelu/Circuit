package ro.ase.costin.ecomfront.address;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.entity.Address;
import ro.ase.costin.ecomcommon.entity.Customer;

import javax.transaction.Transactional;


@Service
@Transactional
@AllArgsConstructor
public class AddressService {

    private AddressRepository addressRepository;

    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findByCustomer(customer);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer addressId, Integer customerId) {
        return addressRepository.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Integer addressId, Integer customerId) {
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }
        addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findDefaultByCustomer(customer.getId());
    }
}
