package ro.ase.costin.ecomfront.review.vote;

import java.util.List;
import java.util.NoSuchElementException;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.data.VoteResult;
import ro.ase.costin.ecomcommon.data.VoteType;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomcommon.entity.ReviewVote;
import ro.ase.costin.ecomfront.review.ReviewRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ReviewVoteService {

    private ReviewRepository reviewRepository;

    private ReviewVoteRepository reviewVoteRepository;

    public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
        reviewVoteRepository.delete(vote);
        reviewRepository.updateVoteCount(reviewId);
        Integer voteCount = reviewRepository.getVoteCount(reviewId);
        String myVote = voteType.equals(VoteType.UP) ? "👍" : "👎";
        return VoteResult.success("Ai anulat votul tău " + myVote + " al recenziei.", voteCount);
    }

    public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) {
        Review review;
        try {
            review = reviewRepository.findById(reviewId).orElseThrow();
        } catch (NoSuchElementException ex) {
            return VoteResult.fail("Recenzia (ID: " + reviewId + ") nu există.");
        }

        ReviewVote reviewVote = reviewVoteRepository.findByReviewAndCustomer(reviewId, customer.getId());
        if (reviewVote != null) {
            if (reviewVote.isUpvoted() && voteType.equals(VoteType.UP) ||
                    reviewVote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
                return undoVote(reviewVote, reviewId, voteType);
            } else if (reviewVote.isUpvoted() && voteType.equals(VoteType.DOWN)) {
                reviewVote.voteDown();
            } else if (reviewVote.isDownvoted() && voteType.equals(VoteType.UP)) {
                reviewVote.voteUp();
            }
        } else {
            reviewVote = new ReviewVote();
            reviewVote.setCustomer(customer);
            reviewVote.setReview(review);

            if (voteType.equals(VoteType.UP)) {
                reviewVote.voteUp();
            } else {
                reviewVote.voteDown();
            }
        }

        reviewVoteRepository.save(reviewVote);
        reviewRepository.updateVoteCount(reviewId);
        Integer voteCount = reviewRepository.getVoteCount(reviewId);
        String vote = voteType.equals(VoteType.UP) ? "👍" : "👎";
        return VoteResult.success("Ai votat " + vote + ") recenzia.", voteCount);
    }

    public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
        List<ReviewVote> listVotes = reviewVoteRepository.findByProductAndCustomer(productId, customerId);
        for (ReviewVote vote : listVotes) {
            Review votedReview = vote.getReview();
            if (listReviews.contains(votedReview)) {
                int index = listReviews.indexOf(votedReview);
                Review review = listReviews.get(index);
                if (vote.isUpvoted()) {
                    review.setUpvotedByCurrentCustomer(true);
                } else if (vote.isDownvoted()) {
                    review.setDownvotedByCurrentCustomer(true);
                }
            }
        }
    }
}
