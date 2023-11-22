var data;
var chartOptions;

$(document).ready(function() {
    setupButtonEventHandlers("_category", loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period) {
    if (period === "custom") {
        startDate = $("#startDate_category").val();
        endDate = $("#endDate_category").val();

        requestURL = contextPath + "reports/category/" + startDate + "/" + endDate;
    } else {
        requestURL = contextPath + "reports/category/" + period;
    }

    $.get(requestURL, function(responseJSON) {
        prepareChartDataForSalesReportByCategory(responseJSON);
        customizeChartForSalesReportByCategory();
        formatChartData(data, 1, 2);
        drawChartForSalesReportByCategory(period);
        setSalesAmount(period, '_category', "Total produse");
    });
}

function prepareChartDataForSalesReportByCategory(responseJSON) {
    data = new google.visualization.DataTable();
    data.addColumn('string', 'Categorie produs');
    data.addColumn('number', 'Val. brută vânzări');
    data.addColumn('number', 'Val. netă vânzări');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    });
}

function customizeChartForSalesReportByCategory() {
    chartOptions = {
        width: '100%',
        height: '100%',
        legend: {position: 'right'},
        chartArea: {
            left: "3%",
            top: "3%",
            height: "94%",
            width: "94%"
        }
    };
}

function drawChartForSalesReportByCategory() {
    var salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
    salesChart.draw(data, chartOptions);
}