package ro.ase.costin.ecomfront.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT COUNT(d) FROM OrderDetail d JOIN OrderTrack t ON d.order.id = t.order.id"
            + " WHERE d.product.id = ?1 AND d.order.customer.id = ?2 AND t.status = ?3")
    Long countByProductAndCustomerAndOrderStatus(Integer productId, Integer customerId, OrderStatus status);
}
