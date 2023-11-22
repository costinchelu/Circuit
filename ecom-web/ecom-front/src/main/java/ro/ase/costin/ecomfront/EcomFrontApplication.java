package ro.ase.costin.ecomfront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"ro.ase.costin.ecomcommon.entity", "ro.ase.costin.ecomback.user"})
public class EcomFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomFrontApplication.class, args);
    }

}
