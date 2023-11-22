package ro.ase.costin.ecomback.customer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.entity.Customer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomerCsvExporter extends AbstractExporter {

    private final String[] customerFieldMapping = {"id", "lastName", "firstName", "email", "phoneNumber",
            "addressLine1", "addressLine2",  "postalCode", "city", "state", "verificationCode", "authenticationType"};

    public void exportCsv(List<Customer> customers, HttpServletResponse httpResponse) {
        super.setResponseHeader(httpResponse, "text/csv", ".csv", "clienti_");

        try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvBeanWriter.writeHeader(customerCsvHeader);
            prepareCustomers(customers);
            for (Customer customer : customers) {
                csvBeanWriter.write(customer, customerFieldMapping);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void prepareCustomers(List<Customer> customers) {
        customers.forEach(this::prepareCustomer);
    }

    private void prepareCustomer(Customer customer) {
        customer.setVerificationCode(customer.getCountry().getName());
        customer.setFirstName(StringUtils.stripAccents(customer.getFirstName()));
        customer.setLastName(StringUtils.stripAccents(customer.getLastName()));
        customer.setAddressLine1(StringUtils.stripAccents(customer.getAddressLine1()));
        customer.setAddressLine2(StringUtils.stripAccents(customer.getAddressLine2()));
        customer.setCity(StringUtils.stripAccents(customer.getCity()));
        customer.setState(StringUtils.stripAccents(customer.getState()));
    }
}