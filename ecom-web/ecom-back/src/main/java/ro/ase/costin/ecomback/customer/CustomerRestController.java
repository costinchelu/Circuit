package ro.ase.costin.ecomback.customer;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CustomerRestController {

    private CustomerService customerService;

    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        if (customerService.isEmailUnique(id, email)) {
            return "OK";
        } else {
            return "Duplicated";
        }
    }
}
