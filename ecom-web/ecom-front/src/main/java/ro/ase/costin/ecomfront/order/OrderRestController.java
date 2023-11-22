package ro.ase.costin.ecomfront.order;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.data.OrderReturnRequest;
import ro.ase.costin.ecomcommon.data.OrderReturnResponse;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.exception.CustomerNotFoundException;
import ro.ase.costin.ecomcommon.exception.OrderNotFoundException;
import ro.ase.costin.ecomfront.customer.CustomerService;
import ro.ase.costin.ecomfront.utils.Utility;


@RestController
@AllArgsConstructor
public class OrderRestController {

    private OrderService orderService;

    private CustomerService customerService;

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest servletRequest) {

        Customer customer;
        try {
            customer = getAuthenticatedCustomer(servletRequest);
        } catch (CustomerNotFoundException ex) {
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("Clientul nu este autentificat");
        }
        return customerService.getCustomerByEmail(email);
    }
}
