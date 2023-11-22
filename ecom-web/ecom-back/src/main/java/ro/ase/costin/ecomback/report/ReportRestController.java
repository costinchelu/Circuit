package ro.ase.costin.ecomback.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.costin.ecomcommon.data.ReportItem;
import ro.ase.costin.ecomcommon.data.ReportType;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportRestController {

    @NonNull
    private MasterOrderReportService masterOrderReportService;

    @NonNull
    private OrderDetailReportService orderDetailReportService;

    @GetMapping("/reports/sales_by_date/{period}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period) {
        switch (period) {
            case "last_28_days":
                return masterOrderReportService.getReportDataLast28Days(ReportType.DAY);
            case "last_6_months":
                return masterOrderReportService.getReportDataLast6Months(ReportType.MONTH);
            case "last_year":
                return masterOrderReportService.getReportDataLastYear(ReportType.MONTH);
            default:
                return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
        }
    }

    @GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate,
                                                      @PathVariable("endDate") String endDate) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);
        return masterOrderReportService.getReportDataByDateRange(startTime, endTime, ReportType.DAY);
    }

    @GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
    public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") String groupBy,
                                                                      @PathVariable("startDate") String startDate,
                                                                      @PathVariable("endDate") String endDate) throws ParseException {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse(startDate);
        Date endTime = dateFormatter.parse(endDate);
        return orderDetailReportService.getReportDataByDateRange(startTime, endTime, reportType);
    }

    @GetMapping("/reports/{groupBy}/{period}")
    public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable("groupBy") String groupBy, @PathVariable("period") String period) {
        ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
        switch (period) {
            case "last_28_days":
                return orderDetailReportService.getReportDataLast28Days(reportType);
            case "last_6_months":
                return orderDetailReportService.getReportDataLast6Months(reportType);
            case "last_year":
                return orderDetailReportService.getReportDataLastYear(reportType);
            default:
                return orderDetailReportService.getReportDataLast7Days(reportType);
        }
    }
}