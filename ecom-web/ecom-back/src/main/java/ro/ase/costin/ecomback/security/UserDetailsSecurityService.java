package ro.ase.costin.ecomback.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ro.ase.costin.ecomback.user.UserRepository;
import ro.ase.costin.ecomcommon.entity.User;

public class UserDetailsSecurityService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(userEmail);
        if (user != null) {
            return new UserDetailsSecurity(user);
        }
        throw  new UsernameNotFoundException("Utilizatorul (e-mail: " + userEmail + ") nu a fost găsit.");
    }
}
