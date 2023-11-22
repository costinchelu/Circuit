package ro.ase.costin.ecomfront.customer;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class CustomerRestController {

    private CustomerService service;

    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(String email) {
        return service.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
