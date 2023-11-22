package ro.ase.costin.ecomfront.review;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.Review;

import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Disabled("Slow tests")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void testFindByCustomerNoKeyword() {
        Integer customerId = 5;
        Pageable pageable = PageRequest.of(1, 5);
        Page<Review> page = reviewRepository.findByCustomer(customerId, pageable);
        long totalElements = page.getTotalElements();
        assertThat(totalElements).isGreaterThan(1);
    }

    @Test
    void testFindByCustomerWithKeyword() {
        Integer customerId = 5;
        String keyword = "charger";
        Pageable pageable = PageRequest.of(1, 5);
        Page<Review> page = reviewRepository.findByCustomer(customerId, keyword, pageable);
        long totalElements = page.getTotalElements();
        assertThat(totalElements).isPositive();
    }

    @Test
    void testFindByCustomerAndId() {
        Integer customerId = 5;
        Integer reviewId = 4;
        Review review = reviewRepository.findByCustomerAndId(customerId, reviewId);
        assertThat(review).isNotNull();
    }

    @Test
    void testFindByProduct() {
        Product product = new Product(23);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Review> page = reviewRepository.findByProduct(product, pageable);
        assertThat(page.getTotalElements()).isGreaterThan(1);
        List<Review> content = page.getContent();
        content.forEach(System.out::println);
    }

    @Test
    void testUpdateVoteCount() {
        Integer reviewId = 5;
        reviewRepository.updateVoteCount(reviewId);
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(review.getVotes()).isEqualTo(2);
    }
}