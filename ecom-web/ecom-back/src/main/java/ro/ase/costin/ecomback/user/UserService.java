package ro.ase.costin.ecomback.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.exception.UserNotFoundException;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.user.RoleRepository;
import ro.ase.costin.ecomback.user.UserRepository;
import ro.ase.costin.ecomcommon.entity.Role;
import ro.ase.costin.ecomcommon.entity.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 5;

    @NonNull
    private UserRepository userRepository;

    @NonNull
    private RoleRepository roleRepository;

    @NonNull
    private PasswordEncoder passwordEncoder;

    public List<User> listAllUsers() {
        return (List<User>) userRepository.findAll(Sort.by("lastName").ascending());
    }

    public void listAllByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, USERS_PER_PAGE, userRepository);
    }

    public List<Role> listAllRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            User existingUserFromDb = userRepository.findById(user.getId()).get();

            if (user.getPassword().isBlank()) {
                user.setPassword(existingUserFromDb.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public User updateUserAccount(User userFromForm) {
        User userInDB = userRepository.findById(userFromForm.getId()).get();

        if (!userFromForm.getPassword().isEmpty()) {
            userInDB.setPassword(userFromForm.getPassword());
            encodePassword(userInDB);
        }

        if (userFromForm.getPhoto() != null) {
            userInDB.setPhoto(userFromForm.getPhoto());
        }

        userInDB.setFirstName(userFromForm.getFirstName());
        userInDB.setLastName(userFromForm.getLastName());

        return userRepository.save(userInDB);
    }

    private void encodePassword(User user) {
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);
    }

    public boolean isEmailUnique(Integer userId, String email) {
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail == null) {
            return true;
        } else return Objects.equals(userByEmail.getId(), userId);
    }

    public User get(Integer userId) throws UserNotFoundException {
        try {
            return userRepository.findById(userId).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new UserNotFoundException("Utilizatorul (ID: " + userId + ") nu a fost găsit.");
        }
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) throws UserNotFoundException {
        try {
            userRepository.updateEnabledStatus(id, enabled);
        } catch (Exception ex) {
            throw new UserNotFoundException("Could not find any user with id = " + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserNotFoundException("This user cannot be deleted");
        }
    }
}
