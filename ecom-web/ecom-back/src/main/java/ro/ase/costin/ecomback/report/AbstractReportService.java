package ro.ase.costin.ecomback.report;

import lombok.extern.slf4j.Slf4j;
import ro.ase.costin.ecomcommon.data.ReportItem;
import ro.ase.costin.ecomcommon.data.ReportType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class AbstractReportService {

    protected DateFormat dateFormatter;

    public List<ReportItem> getReportDataLast7Days(ReportType reportType) {
        return getReportDataLastXDays(7, reportType);
    }

    public List<ReportItem> getReportDataLast28Days(ReportType reportType) {
        return getReportDataLastXDays(28, reportType);
    }

    public List<ReportItem> getReportDataLast6Months(ReportType reportType) {
        return getReportDataLastXMonths(6, reportType);
    }

    public List<ReportItem> getReportDataLastYear(ReportType reportType) {
        return getReportDataLastXMonths(12, reportType);
    }

    public List<ReportItem> getReportDataByDateRange(Date startTime, Date endTime, ReportType reportType) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    protected List<ReportItem> getReportDataLastXDays(int days, ReportType reportType) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date endTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = cal.getTime();
//        logPeriod(endTime, startTime);
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    protected List<ReportItem> getReportDataLastXMonths(int months, ReportType reportType) {
        dateFormatter = new SimpleDateFormat("yyyy-MM");
        Date endTime = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -(months - 1));
        Date startTime = cal.getTime();
//        logPeriod(endTime, startTime);
        return getReportDataByDateRangeInternal(startTime, endTime, reportType);
    }

    private List<ReportItem> getReportData(int period, ReportType reportType) {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        if (ReportType.DAY.equals(reportType)) {
            calendar.add(Calendar.DAY_OF_MONTH, -(period - 1));
            dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        } else if (ReportType.MONTH.equals(reportType)) {
            calendar.add(Calendar.MONTH, -(period - 1));
            dateFormatter = new SimpleDateFormat("yyyy-MM");
        }
        Date startTime = calendar.getTime();
//        logPeriod(endTime, startTime);
        return getReportDataByDateRange(startTime, endTime, reportType);
    }

    private void logPeriod(Date endTime, Date startTime) {
        log.info("De la: " + startTime);
        log.info("Până la: " + endTime);
    }

    protected static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    protected static Date setEndTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    protected abstract List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType);
}
