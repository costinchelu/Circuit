package ro.ase.costin.ecomback.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.ase.costin.ecomback.order.OrderDetailRepository;
import ro.ase.costin.ecomback.utils.DelayedFormatter;
import ro.ase.costin.ecomcommon.data.ReportItem;
import ro.ase.costin.ecomcommon.data.ReportType;
import ro.ase.costin.ecomcommon.entity.OrderDetail;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailReportService extends AbstractReportService {

    @NonNull
    private OrderDetailRepository orderDetailRepository;

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
//        startDate =  removeTime(startDate);
//        endDate = setEndTime(endDate);

        List<OrderDetail> listOrderDetails = new ArrayList<>();

        if (reportType.equals(ReportType.CATEGORY)) {
            listOrderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startDate, endDate);
        } else if (reportType.equals(ReportType.PRODUCT)) {
            listOrderDetails = orderDetailRepository.findWithProductAndTimeBetween(startDate, endDate);
        }
//        printRawData(listOrderDetails);
        List<ReportItem> listReportItems = new ArrayList<>();

        for (OrderDetail detail : listOrderDetails) {
            String identifier = "";
            if (reportType.equals(ReportType.CATEGORY)) {
                identifier = detail.getProduct().getCategory().getName();
            } else if (reportType.equals(ReportType.PRODUCT)) {
                identifier = detail.getProduct().getShortName();
            }
            ReportItem reportItem = new ReportItem(identifier);

            float grossSales = detail.getSubtotal() + detail.getShippingCost();
            float netSales = detail.getSubtotal() - detail.getProductCost();

            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);
                reportItem.addGrossSales(grossSales);
                reportItem.addNetSales(netSales);
                reportItem.increaseProductsCount(detail.getQuantity());
            } else {
                listReportItems.add(new ReportItem(identifier, grossSales, netSales, detail.getQuantity()));
            }
        }
//        printReportData(listReportItems);
        return listReportItems;
    }

    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(item -> log.info("{}, {}, {}, {} \n",
                DelayedFormatter.format("%-20s", item.getIdentifier()),
                DelayedFormatter.format("%10.2f", item.getGrossSales()),
                DelayedFormatter.format("%10.2f", item.getNetSales()),
                DelayedFormatter.format("%.03f", item.getProductsCount())));
    }

    private void printRawData(List<OrderDetail> listOrderDetails) {
        listOrderDetails.forEach(detail -> log.info("{} | {} | {} | {} | {} \n",
                DelayedFormatter.format("%d", detail.getQuantity()),
                DelayedFormatter.format("%-20s", detail.getProduct().getShortName().substring(0, 20)),
                DelayedFormatter.format("%10.2f", detail.getSubtotal()),
                DelayedFormatter.format("%10.2f", detail.getProductCost()),
                DelayedFormatter.format("%10.2f", detail.getShippingCost())));
    }

}

