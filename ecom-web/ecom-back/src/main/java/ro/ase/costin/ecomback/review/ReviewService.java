package ro.ase.costin.ecomback.review;

import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.product.ProductRepository;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomcommon.exception.ReviewNotFoundException;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    public static final int REVIEWS_PER_PAGE = 10;

    @NonNull
    private ReviewRepository reviewRepository;

    @NonNull
    private ProductRepository productRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, REVIEWS_PER_PAGE, reviewRepository);
    }

    public Review get(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new ReviewNotFoundException("Recenzia nu există - ID: " + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDB = reviewRepository.findById(reviewInForm.getId()).orElseThrow();
        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInForm.getComment());
        reviewRepository.save(reviewInDB);
        productRepository.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Recenzia nu există - ID: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
