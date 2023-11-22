package ro.ase.costin.ecomback.user;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ro.ase.costin.ecomcommon.entity.Role;
import ro.ase.costin.ecomcommon.entity.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Disabled("Integration tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Tag("integration_test")
    void testCreateUser() {
        Role roleAdmin = entityManager.find(Role.class, 2);
        User userCostin = new User("someUser@yah.com", "12345", "Costin", "Chelu");
        userCostin.addRole(roleAdmin);
        User savedUser = userRepository.save(userCostin);
        assertThat(savedUser.getId()).isPositive();
    }

    @Test
    @Tag("integration_test")
    void testCreateUserWith2Roles() {
        Role roleEdit = entityManager.find(Role.class, 10);
        Role roleAssist = entityManager.find(Role.class, 3);
        User userMihai = new User("someUser2@gmail.ro", "12345", "Mihai", "Ionescu");
        userMihai.addRole(roleEdit);
        userMihai.addRole(roleAssist);
        User savedUser = userRepository.save(userMihai);
        assertThat(savedUser.getId()).isPositive();
    }

    @Test
    @Tag("integration_test")
    void testListAllUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        allUsers.forEach(System.out::println);
        assertThat(allUsers).isNotNull();
    }

    @Test
    void testFindById() {
        Optional<User> user = userRepository.findById(1);
        System.out.println(user.orElse(new User()));
        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Tag("integration_test")
    void  testGetUserByEmail() {
        String email = "abcdefghijk@lmnopqrs.com";
        User userByEmail = userRepository.getUserByEmail(email);
        assertThat(userByEmail).isNull();

//        User validUser = userRepository.getUserByEmail("costin@yah.com");
//        assertThat(validUser).isNotNull();
    }

    @Test
    void testCountById() {
        Integer id = 1;
        Long count = userRepository.countById(id);
        assertThat(count).isNotNull().isPositive();
    }

    @Test
    void testDisableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, false);
        assertThat(userRepository.findById(1).get().isEnabled()).isFalse();
    }

    @Test
    void testEnableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, true);
        assertThat(userRepository.findById(1).get().isEnabled()).isTrue();
    }

    @Test
    void testListFirstPage() {
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> users = page.getContent();
        assertThat(users).size().isEqualTo(pageSize);
    }

    @Test
    @Tag("integration_test")
    void testSearchUsers() {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(keyword, pageable);
        List<User> users = page.getContent();
        assertThat(users.size()).isPositive();
    }

}
