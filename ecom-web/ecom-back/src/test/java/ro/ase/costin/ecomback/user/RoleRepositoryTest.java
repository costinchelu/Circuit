package ro.ase.costin.ecomback.user;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ro.ase.costin.ecomcommon.entity.Role;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Disabled("Integration tests")
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void testCreateARole() {
        Role roleAdmin = new Role("SomeRole", "description of the role");
        Role savedRole = roleRepository.save(roleAdmin);
        assertThat(savedRole.getId()).isPositive();
    }

    @Test
    void testUpdateRole() {
        String description = "DESCRIPTION";
        Optional<Role> roleAdmin = roleRepository.findById(2);
        roleAdmin.get().setDescription(description);
        roleRepository.save(roleAdmin.get());
        assertThat(roleRepository.findById(2).get().getDescription()).isEqualTo(description);
    }

    @Test
    void testCreateAdminRole() {
        Role roleAdmin = new Role("Admin", "complete access");
        Role savedRole = roleRepository.save(roleAdmin);
        assertThat(savedRole.getId()).isPositive();
    }

    @Test
    void testCreateAssistRole() {
        Role roleAssist = new Role("Assistant", "access forum (reviews, questions)");
        Role savedRole = roleRepository.save(roleAssist);
        assertThat(savedRole.getId()).isPositive();
    }

    @Test
    void testCreateEditorRole() {
        Role roleEditor = new Role("Editor", " access categories, brands, products, " +
                "articles, menus");
        Role savedRole = roleRepository.save(roleEditor);
        assertThat(savedRole.getId()).isPositive();
    }
}
