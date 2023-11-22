package ro.ase.costin.ecomfront.review.vote;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomcommon.entity.ReviewVote;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class ReviewVoteRepositoryTest {

    @Autowired private ReviewVoteRepository repo;

    @Test
    void testSaveVote() {
        Integer customerId = 3;
        Integer reviewId = 5;
        ReviewVote vote = new ReviewVote();
        vote.setCustomer(new Customer(customerId));
        vote.setReview(new Review(reviewId));
        vote.isUpvoted();
        ReviewVote savedVote = repo.save(vote);
        assertThat(savedVote.getId()).isPositive();
    }

    @Test
    void testFindByReviewAndCustomer() {
        Integer customerId = 1;
        Integer reviewId = 4;
        ReviewVote vote = repo.findByReviewAndCustomer(reviewId, customerId);
        assertThat(vote).isNotNull();
        System.out.println(vote);
    }

    @Test
    void testFindByProductAndCustomer() {
        Integer customerId = 1;
        Integer productId = 1;
        List<ReviewVote> listVotes = repo.findByProductAndCustomer(productId, customerId);
        assertThat(listVotes.size()).isPositive();
        listVotes.forEach(System.out::println);
    }
}