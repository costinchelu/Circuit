package ro.ase.costin.ecomfront.review;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Product;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomcommon.exception.ReviewNotFoundException;
import ro.ase.costin.ecomfront.order.OrderDetailRepository;
import ro.ase.costin.ecomfront.product.ProductRepository;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    public static final int REVIEWS_PER_PAGE = 5;

    @NonNull
    private ReviewRepository reviewRepository;

    @NonNull
    private OrderDetailRepository orderDetailRepository;

    @NonNull
    private ProductRepository productRepository;

    public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
        if (keyword != null) {
            return reviewRepository.findByCustomer(customer.getId(), keyword, pageable);
        }
        return reviewRepository.findByCustomer(customer.getId(), pageable);
    }

    public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
        Review review = reviewRepository.findByCustomerAndId(customer.getId(), reviewId);
        if (review == null) {
            throw new ReviewNotFoundException("Clientul nu a creat recenzia cu ID " + reviewId);
        }
        return review;
    }

    public Page<Review> list3MostVotedReviewsByProduct(Product product) {
        Sort sort = Sort.by("votes").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        return reviewRepository.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
        return reviewRepository.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = reviewRepository.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = orderDetailRepository
                .countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.LIVRATA);
        return count > 0;
    }

    public Review saveReview(Review review) {
        review.setReviewTime(new Date());
        Review savedReview = reviewRepository.save(review);
        Integer productId = savedReview.getProduct().getId();
        productRepository.updateReviewCountAndAverageRating(productId);
        return savedReview;
    }
}
