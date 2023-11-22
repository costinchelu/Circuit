package ro.ase.costin.ecomback.brand;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.data.BrandDTO;
import ro.ase.costin.ecomcommon.entity.Brand;
import ro.ase.costin.ecomcommon.entity.Category;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BrandsCsvExporter extends AbstractExporter {

    private final String[] brandFieldMapping = {"id", "name", "categories"};

    public void exportCsv(List<Brand> brands, HttpServletResponse httpResponse) {
        super.setResponseHeader(httpResponse, "text/csv", ".csv", "marci_");
        try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvBeanWriter.writeHeader(brandCsvHeader);
            List<BrandDTO> enrichedBrands = getEnrichedBrands(brands);
            for (BrandDTO brand : enrichedBrands) {
                csvBeanWriter.write(brand, brandFieldMapping);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private List<BrandDTO> getEnrichedBrands(List<Brand> brands) {
        return brands.stream().map(this::mapBrandDto).collect(Collectors.toList());
    }

    private BrandDTO mapBrandDto(Brand brand) {
        return BrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName().toUpperCase())
                .categories(StringUtils.stripAccents(StringUtils.join(
                                brand.getCategories()
                                        .stream()
                                        .sorted(Comparator.comparing(Category::getName))
                                        .collect(Collectors.toList()),
                        "+")))
                .build();
    }
}
