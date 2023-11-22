package ro.ase.costin.ecomback.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.exception.OrderNotFoundException;

@RestController
@AllArgsConstructor
public class OrderRestController {

    private OrderService orderService;

    @PostMapping("/orders_shipper/update/{id}/{status}")
    public Response updateOrderStatus(@PathVariable("id") Integer orderId, @PathVariable("status") String status) throws OrderNotFoundException {
        orderService.updateStatus(orderId, status);
        return new Response(orderId, status);
    }
}

@AllArgsConstructor
@Data
class Response {

    private Integer orderId;

    private String status;
}
