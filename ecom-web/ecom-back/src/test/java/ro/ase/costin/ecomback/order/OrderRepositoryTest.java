package ro.ase.costin.ecomback.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.data.PaymentMethod;
import ro.ase.costin.ecomcommon.entity.Customer;
import ro.ase.costin.ecomcommon.entity.Order;
import ro.ase.costin.ecomcommon.entity.OrderDetail;
import ro.ase.costin.ecomcommon.entity.OrderTrack;
import ro.ase.costin.ecomcommon.entity.Product;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@Disabled("Integration tests")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void testCreateNewOrderWithSingleProduct() {
        Customer customer = em.find(Customer.class, 1);
        Product product = em.find(Product.class, 1);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setStatus(OrderStatus.NOUA);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(1);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getId()).isPositive();
    }

    @Test
    void testCreateNewOrderWithMultipleProducts() {
        Customer customer = em.find(Customer.class, 2);
        Product product1 = em.find(Product.class, 3);
        Product product2 = em.find(Product.class, 5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(20);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice() * orderDetail2.getQuantity());
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subtotal = product1.getPrice() * orderDetail1.getQuantity() + product2.getPrice() * orderDetail2.getQuantity();
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + mainOrder.getShippingCost());

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.PROCESARE);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(3);

        Order savedOrder = orderRepository.save(mainOrder);
        assertThat(savedOrder.getId()).isPositive();
    }

    @Test
    void testListOrders() {
        Iterable<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSizeGreaterThan(0);
        orders.forEach(System.out::println);
    }

    @Test
    void testUpdateOrder() {
        Integer orderId = 2;
        Order order = orderRepository.findById(orderId).get();
        order.setStatus(OrderStatus.LIVRARE);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliverDays(2);
        Order updatedOrder = orderRepository.save(order);
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.LIVRARE);
    }

    @Test
    void testGetOrder() {
        Integer orderId = 2;
        Order order = orderRepository.findById(orderId).get();
        assertThat(order).isNotNull();
        System.out.println(order);
    }

    @Test
    void testDeleteOrder() {
        Integer orderId = 3;
        orderRepository.deleteById(orderId);
        Optional<Order> result = orderRepository.findById(orderId);
        assertThat(result).isNotPresent();
    }

    @Test
    void testUpdateOrderTracks() {
        Integer orderId = 17;
        Order order = orderRepository.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NOUA);
        newTrack.setNotes(OrderStatus.NOUA.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PROCESARE);
        processingTrack.setNotes(OrderStatus.PROCESARE.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);
        Order updatedOrder = orderRepository.save(order);
        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }

    @Test
    void testAddTrackWithStatusNewToOrder() {
        Integer orderId = 5;
        Order order = orderRepository.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NOUA);
        newTrack.setNotes(OrderStatus.NOUA.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);

        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }

    @Test
    void testFindByOrderTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        assertThat(listOrders).isNotEmpty();

        for (Order order : listOrders) {
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n",
                    order.getId(), order.getOrderTime(), order.getProductCost(),
                    order.getSubtotal(), order.getTotal());
        }
    }
}