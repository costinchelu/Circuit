package ro.ase.costin.ecomback.product.export;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.data.ProductExportDto;
import ro.ase.costin.ecomcommon.entity.Product;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ProductPdfExporter extends AbstractExporter {

    public void exportPdf(List<Product> products, HttpServletResponse response) {
        super.setResponseHeader(response, "application/pdf", ".pdf", "Produse_");

        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Font font = getFont(FontFactory.HELVETICA_BOLD, 18, Color.RED);

            Paragraph paragraph = new Paragraph("Lista produselor", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(20);
            table.setWidths(new float[] {11f, 28f, 71f, 38f, 38f, 12f, 12f, 18f});

            writeTableHeader(table);
            writeTableData(table, products);
            document.add(table);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTableData(PdfPTable table, List<Product> products) throws IOException {
        ProductCsvExporter pce = new ProductCsvExporter();
        List<ProductExportDto> customizedProducts = pce.getCustomizedProducts(products);
        for (ProductExportDto product : customizedProducts) {
            com.lowagie.text.Image productPicture = Image.getInstance("product-images/" + product.getId() + "/" + product.getImage());
            table.addCell(String.valueOf(product.getId()));
            table.addCell(productPicture);
            table.addCell(product.getName());
            table.addCell(product.getBrand());
            table.addCell(product.getCategory());
            table.addCell(String.valueOf(product.getCost()));
            table.addCell(String.valueOf(product.getPrice()));
            table.addCell(String.valueOf(product.getDiscount()));
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPadding(5);
        Font font = getFont(FontFactory.HELVETICA, 13, Color.WHITE);

        for (String s : productPdfHeader) {
            cell.setPhrase(new Phrase(s, font));
            table.addCell(cell);
        }
    }

    private Font getFont(String fontName, int fontSize, Color fontColor) {
        Font font = FontFactory.getFont(fontName);
        font.setSize(fontSize);
        font.setColor(fontColor);
        return font;
    }
}
