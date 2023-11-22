package ro.ase.costin.ecomback.review;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomback.paging.SearchRepository;
import ro.ase.costin.ecomcommon.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends SearchRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.headline LIKE %?1% OR "
            + "r.comment LIKE %?1% OR r.product.name LIKE %?1% OR "
            + "CONCAT(r.customer.firstName, ' ', r.customer.lastName) LIKE %?1%")
    Page<Review> findAll(String keyword, Pageable pageable);

    @NonNull
    List<Review> findAll();
}
