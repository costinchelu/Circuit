package ro.ase.costin.ecomback.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomback.brand.BrandRepository;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;

import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
@Disabled("Integration tests")

class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void testCreateBrand1() {
        Category laptops = new Category(6);
        Brand acer = new Brand("Acer");
        acer.getCategories().add(laptops);

        Brand savedBrand = brandRepository.save(acer);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testCreateBrand2() {
        Category cellphones = new Category(4);
        Category tablets = new Category(7);

        Brand apple = new Brand("Apple");
        apple.getCategories().add(cellphones);
        apple.getCategories().add(tablets);

        Brand savedBrand = brandRepository.save(apple);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testCreateBrand3() {
        Brand samsung = new Brand("Samsung");

        samsung.getCategories().add(new Category(29));	// category memory
        samsung.getCategories().add(new Category(24));	// category internal hard drive

        Brand savedBrand = brandRepository.save(samsung);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isPositive();
    }

    @Test
    void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    void testGetById() {
        Brand brand = brandRepository.findById(1).get();

        assertThat(brand.getName()).isEqualTo("Acer");
    }

    @Test
    void testUpdateName() {
        String newName = "Samsung Electronics";
        Brand samsung = brandRepository.findById(3).get();
        samsung.setName(newName);

        Brand savedBrand = brandRepository.save(samsung);
        assertThat(savedBrand.getName()).isEqualTo(newName);
    }

    @Test
    @Disabled
    void testDelete() {
        Integer id = 2;
        brandRepository.deleteById(id);

        Optional<Brand> result = brandRepository.findById(id);

        assertTrue(result.isEmpty());
    }
}
