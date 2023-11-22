package ro.ase.costin.ecomfront.review.vote;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.data.VoteResult;
import ro.ase.costin.ecomcommon.data.VoteType;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomfront.utils.ControllerHelper;


@RestController
@AllArgsConstructor
public class VoteReviewRestController {

    private ReviewVoteService reviewVoteService;

    private ControllerHelper controllerHelper;

    @PostMapping("/vote_review/{id}/{type}")
    public VoteResult voteReview(@PathVariable(name = "id") Integer reviewId,
                                 @PathVariable(name = "type") String type,
                                 HttpServletRequest request) {

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        if (customer == null) {
            return VoteResult.fail("Trebuie să fi autentificat pentru a putea vota recenzia.");
        }
        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        return reviewVoteService.doVote(reviewId, customer, voteType);
//        TODO: a customer cannot vote its own review
    }
}
