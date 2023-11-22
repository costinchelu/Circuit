package ro.ase.costin.ecomback.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.entity.OrderDetail;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-09-01");
        Date endTime = dateFormatter.parse("2021-09-30");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails).isNotEmpty();

        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%-30s | %d | %10.2f| %10.2f | %10.2f \n",
                    detail.getProduct().getCategory().getName(),
                    detail.getQuantity(), detail.getProductCost(),
                    detail.getShippingCost(), detail.getSubtotal());
        }
    }


    @Test
    void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-10-01");
        Date endTime = dateFormatter.parse("2021-10-30");

        List<OrderDetail> listOrderDetails = orderDetailRepository.findWithProductAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails).isNotEmpty();

        for (OrderDetail detail : listOrderDetails) {
            System.out.printf("%-70s | %d | %10.2f| %10.2f | %10.2f \n",
                    detail.getProduct().getShortName(),
                    detail.getQuantity(), detail.getProductCost(),
                    detail.getShippingCost(), detail.getSubtotal());
        }
    }
}