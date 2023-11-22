package ro.ase.costin.ecomfront.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomfront.customer.CustomerService;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class ControllerHelper {

    private CustomerService customerService;

    public Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
