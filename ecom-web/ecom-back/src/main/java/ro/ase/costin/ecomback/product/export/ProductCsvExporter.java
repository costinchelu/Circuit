package ro.ase.costin.ecomback.product.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.data.ProductExportDto;
import ro.ase.costin.ecomcommon.entity.Product;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProductCsvExporter extends AbstractExporter {

    private final String[] productFieldMapping = {"id", "name", "brand", "category", "cost", "price", "discount"};

    public void exportCsv(List<Product> products, HttpServletResponse httpResponse) {
        super.setResponseHeader(httpResponse, "text/csv", ".csv", "produse_");

        try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(httpResponse.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvBeanWriter.writeHeader(productCsvHeader);
            List<ProductExportDto> productExportDtos = getCustomizedProducts(products);
            for (ProductExportDto p : productExportDtos) {
                csvBeanWriter.write(p, productFieldMapping);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public List<ProductExportDto> getCustomizedProducts(List<Product> products) {
        return products.stream().map(this::mapToProductExportDto).collect(Collectors.toList());
    }

    private ProductExportDto mapToProductExportDto(Product product) {
        return ProductExportDto.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand().getName())
                .category(StringUtils.stripAccents(product.getCategory().getName()))
                .cost(product.getCost())
                .price(product.getPrice())
                .discount(product.getDiscountPercent())
                .image(product.getMainImage())
                .build();
    }
}
