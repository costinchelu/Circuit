package ro.ase.costin.ecomback.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class PasswordEncoderTest {

    @Test
    void testEncodePassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "Password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("Encoded password (test): " + encodedPassword);

        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
