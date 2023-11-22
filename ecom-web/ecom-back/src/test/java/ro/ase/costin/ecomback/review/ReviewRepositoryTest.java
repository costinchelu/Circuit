package ro.ase.costin.ecomback.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.Review;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
@Disabled("Integration test")
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;

    @Test
    void testCreateReview() {
        Integer productId = 10;
        Product product = new Product(productId);

        Integer customerId = 9;
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewTime(new Date());
        review.setHeadline("Test headline");
        review.setComment("Test comment for product id " + productId);
        review.setRating(4);

        Review savedReview = reviewRepository.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isPositive();
    }

    @Test
    void testListReviews() {
        List<Review> listReviews = reviewRepository.findAll();
        assertThat(listReviews).isNotEmpty();
        listReviews.forEach(System.out::println);
    }

    @Test
    void testGetReview() {
        Integer id = 3;
        Review review = reviewRepository.findById(id).get();
        assertThat(review).isNotNull();
        System.out.println(review);
    }

    @Test
    void testUpdateReview() {
        Integer id = 3;
        String headline = "An awesome product at an awesome price";
        String comment = "Overall great product and highly versatile...";

        Review review = reviewRepository.findById(id).get();
        review.setHeadline(headline);
        review.setComment(comment);

        Review updatedReview = reviewRepository.save(review);

        assertThat(updatedReview.getHeadline()).isEqualTo(headline);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    void testDeleteReview() {
        Integer id = 3;
        reviewRepository.deleteById(id);
        Optional<Review> findById = reviewRepository.findById(id);
        assertThat(findById).isNotPresent();
    }
}