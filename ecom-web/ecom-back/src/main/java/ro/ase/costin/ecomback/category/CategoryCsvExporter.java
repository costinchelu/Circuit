package ro.ase.costin.ecomback.category;

import lombok.extern.slf4j.Slf4j;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.entity.Category;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CategoryCsvExporter extends AbstractExporter {

    private final String[] fieldMapping = {"id", "name"};

    public void exportCsv(List<Category> listCategories, HttpServletResponse response) {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvBeanWriter.writeHeader(categoryCsvHeader);
            for (Category category : listCategories) {
                category.setName(category.getName().replace("-", "  "));
                csvBeanWriter.write(category, fieldMapping);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
