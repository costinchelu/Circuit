package ro.ase.costin.ecomback.customer;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.exception.CustomerNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.setting.repository.CountryRepository;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Customer;


@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    public static final int CUSTOMERS_PER_PAGE = 10;

    public static final String EXCEPTION_MESSAGE = "Nu a fost găsit clientul cu ID ";

    @NonNull
    private CustomerRepository customerRepository;

    @NonNull
    private CountryRepository countryRepository;

    @NonNull
    private PasswordEncoder passwordEncoder;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepository);
    }

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
        customerRepository.updateEnabledStatus(id, enabled);
    }

    public Customer get(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id)
                    .orElseThrow(() -> new CustomerNotFoundException(EXCEPTION_MESSAGE + id));
        } catch (NoSuchElementException ex) {
            throw new CustomerNotFoundException(EXCEPTION_MESSAGE + id);
        }
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer existCustomer = customerRepository.findByEmail(email);
        return existCustomer == null || Objects.equals(existCustomer.getId(), id);
    }

    public void save(Customer customerInForm) throws CustomerNotFoundException {
        Customer customerInDB = customerRepository.findById(customerInForm.getId())
                .orElseThrow(() -> new CustomerNotFoundException(EXCEPTION_MESSAGE + customerInForm.getId()));

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
        customerRepository.save(customerInForm);
    }

    public void delete(Integer id) throws CustomerNotFoundException {
        Long count = customerRepository.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException(EXCEPTION_MESSAGE + id);
        }
        customerRepository.deleteById(id);
    }

    public List<Customer> listAllCustomers() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Customer::getFullName))
                .collect(Collectors.toList());
    }
}

