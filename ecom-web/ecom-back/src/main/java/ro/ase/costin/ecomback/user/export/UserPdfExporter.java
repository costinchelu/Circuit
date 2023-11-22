package ro.ase.costin.ecomback.user.export;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    public void exportPdf(List<User> users, HttpServletResponse response) {
        super.setResponseHeader(response, "application/pdf", ".pdf", "Utilizatori_");

        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            Font font = getFont(FontFactory.HELVETICA_BOLD, 18, Color.RED);

            Paragraph paragraph = new Paragraph("Lista utilizatorilor", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(20);
            table.setWidths(new float[] {12f, 73f, 40f, 40f, 52f, 22f, 28f});

            writeTableHeader(table);
            writeTableData(table, users);
            document.add(table);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTableData(PdfPTable table, List<User> users) throws IOException {
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoleNames());
            table.addCell(user.isEnabled() ? "DA" : "NU");
            Image userPicture = Image.getInstance("user-photos/" + user.getId() + "/" + user.getPhoto());
            table.addCell(userPicture);
        }
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPadding(5);
        Font font = getFont(FontFactory.HELVETICA, 13, Color.WHITE);

        for (String s : userHeader) {
            cell.setPhrase(new Phrase(s, font));
            table.addCell(cell);
        }
        cell.setPhrase(new Phrase("Poza utilizator", font));
        table.addCell(cell);
    }

    private Font getFont(String fontName, int fontSize, Color fontColor) {
        Font font = FontFactory.getFont(fontName);
        font.setSize(fontSize);
        font.setColor(fontColor);
        return font;
    }
}
