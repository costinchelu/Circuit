package ro.ase.costin.ecomback.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.order.OrderRepository;
import ro.ase.costin.ecomback.utils.DelayedFormatter;
import ro.ase.costin.ecomcommon.data.ReportItem;
import ro.ase.costin.ecomcommon.data.ReportType;
import ro.ase.costin.ecomcommon.entity.Order;


@Service
@Slf4j
@RequiredArgsConstructor
public class MasterOrderReportService extends AbstractReportService {

    @NonNull
    private OrderRepository orderRepository;

    protected List<ReportItem> getReportDataByDateRangeInternal(Date startTime, Date endTime, ReportType reportType) {
        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);
//        printRawData(listOrders);
        List<ReportItem> listReportItems = createReportData(startTime, endTime, reportType);
        calculateSalesForReportData(listOrders, listReportItems);
//        printReportData(listReportItems);
        return listReportItems;
    }

    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for (Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());
            ReportItem reportItem = new ReportItem(orderDateString);
            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);
                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        }
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime, ReportType reportType) {
        List<ReportItem> listReportItems = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);
        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);

        listReportItems.add(new ReportItem(dateString));
        do {
            if (reportType.equals(ReportType.DAY)) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (reportType.equals(ReportType.MONTH)) {
                startDate.add(Calendar.MONTH, 1);
            }

            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);
            listReportItems.add(new ReportItem(dateString));

        } while (startDate.before(endDate));

        return listReportItems;
    }

    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(item -> log.info("{}, {}, {}, {} \n",
                item.getIdentifier(),
                DelayedFormatter.format("%.03f", item.getGrossSales()),
                DelayedFormatter.format("%.03f", item.getNetSales()),
                item.getOrdersCount()));
    }

    private void printRawData(List<Order> listOrders) {
        listOrders.forEach(order -> log.info("{} | {} | {} | {} \n", DelayedFormatter.format("%-3d", order.getId()),
                order.getOrderTime(), DelayedFormatter.format("%10.2f", order.getTotal()),
                DelayedFormatter.format("%10.2f", order.getProductCost())));
    }
}