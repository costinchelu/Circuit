package ro.ase.costin.ecomback.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserRestController {

    private UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(Integer id, String email) {
        return userService.isEmailUnique(id, email) ? "OK" : "INVALID";
    }
}
