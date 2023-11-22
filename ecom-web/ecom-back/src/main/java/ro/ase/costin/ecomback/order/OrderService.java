package ro.ase.costin.ecomback.order;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.paging.PagingAndSortingHelper;
import ro.ase.costin.ecomback.setting.repository.CountryRepository;
import ro.ase.costin.ecomcommon.data.OrderStatus;
import ro.ase.costin.ecomcommon.entity.Country;
import ro.ase.costin.ecomcommon.entity.Order;
import ro.ase.costin.ecomcommon.entity.OrderTrack;
import ro.ase.costin.ecomcommon.exception.OrderNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int ORDERS_PER_PAGE = 10;

    @NonNull
    private OrderRepository orderRepository;

    @NonNull
    private CountryRepository countryRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();
        Sort sort;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        Page<Order> page;
        if (keyword != null) {
            page = orderRepository.findAll(keyword, pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public Order get(Integer id) throws OrderNotFoundException {
        try {
            return orderRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            throw new OrderNotFoundException("Comanda (ID: " + id + ") nu a fost găsită.");
        }
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Long count = orderRepository.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Comanda (ID: " + id + ") nu a fost găsită.");
        }
        orderRepository.deleteById(id);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) throws OrderNotFoundException {
        Order orderInDB = orderRepository.findById(orderInForm.getId())
                .orElseThrow(() -> new OrderNotFoundException("Comanda (ID: " + orderInForm.getId() + ") nu a fost găsită."));
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());
        orderRepository.save(orderInForm);
    }

    public void updateStatus(Integer orderId, String status) throws OrderNotFoundException {
        Order orderInDB = orderRepository.findById(orderId).
                orElseThrow(() -> new OrderNotFoundException("Comanda (ID: " + orderId + ") nu a fost găsită."));
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);
        if (!orderInDB.hasStatus(statusToUpdate)) {
            List<OrderTrack> orderTracks = orderInDB.getOrderTracks();
            OrderTrack track = new OrderTrack();
            track.setOrder(orderInDB);
            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);
            orderInDB.setStatus(statusToUpdate);
            orderRepository.save(orderInDB);
            // TODO ideally orders should be updated in the right sequence
        }
    }
}
