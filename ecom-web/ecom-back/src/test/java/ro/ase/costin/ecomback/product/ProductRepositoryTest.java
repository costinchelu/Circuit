package ro.ase.costin.ecomback.product;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;
import ro.ase.costin.ecomcommon.entity.Product;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
@Disabled("Integration tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void createProductTest() {
        Brand brand = em.find(Brand.class, 37);
        Category category = em.find(Category.class, 5);
        Product product = new Product();
        product.setBrand(brand);
        product.setCategory(category);
        product.setName("Acer Aspire Desktop");
        product.setAlias("aspire-desktop");
        product.setShortDescription("GG aspire");
        product.setFullDescription("full description for Acer Aspire Desktop");
        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isPositive();
    }

    @Test
    void listAllProductsTest() {
        Iterable<Product> itProducts = productRepository.findAll();
        assertThat(itProducts).isNotNull();
    }

    @Test
    void getOneProductTest() {
        Optional<Product> productById = productRepository.findById(2);
        assertThat(productById).isNotNull();
        assertThat(productById.get()).isInstanceOf(Product.class);
    }

    @Test
    void updateProductTest() {
        Product product = productRepository.findById(1).get();
        product.setPrice(499);
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    void deleteProductByIdTest() {
        productRepository.deleteById(3);
        assertThat(productRepository.findById(3)).isNotPresent();
    }

    @Test
    void saveProductWithImagesTest() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.png");
        product.addExtraImage("extra_image_2.png");
        product.addExtraImage("extra-image3.png");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }

    @Test
    void testSaveProductWithDetails() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.addDetail("Device Memory", "128 GB");
        product.addDetail("CPU Model", "MediaTek");
        product.addDetail("OS", "Android 10");

        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getDetails()).isNotEmpty();
    }
}