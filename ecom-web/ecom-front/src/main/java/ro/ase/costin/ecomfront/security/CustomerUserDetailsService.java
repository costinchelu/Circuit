package ro.ase.costin.ecomfront.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomfront.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException("Nu s-a găsit clientul cu email: " + email);
        }
        return new CustomerUserDetails(customer);
    }
}
