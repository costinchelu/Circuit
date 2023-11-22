package ro.ase.costin.ecomback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"ro.ase.costin.ecomcommon.entity", "ro.ase.costin.ecomback.user"})
public class EcomBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomBackApplication.class, args);
    }

}
