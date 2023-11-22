package ro.ase.costin.ecomback.user.export;

import lombok.extern.slf4j.Slf4j;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class UserCsvExporter extends AbstractExporter {

    private final String[] userFieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};

    public void exportCsv(List<User> users, HttpServletResponse httpResponse) {
        super.setResponseHeader(httpResponse, "text/csv", ".csv", "utilizatori_");

        try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvBeanWriter.writeHeader(userHeader);
            for (User user : users) {
                csvBeanWriter.write(user, userFieldMapping);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
