package ro.ase.costin.ecomfront.order;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ro.ase.costin.ecomcommon.data.PaymentMethod;
import ro.ase.costin.ecomcommon.entity.Order;
import ro.ase.costin.ecomcommon.entity.OrderDetail;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderPdfExporter {

    private final DateFormat dfHyphenSeparated = new SimpleDateFormat("yyyy-MM-dd");

    private final DateFormat dfSlashSeparated = new SimpleDateFormat("dd/MM/yyyy");

    private final DateFormat dfShortDate = new SimpleDateFormat("yyMM");

    private final NumberFormat format = NumberFormat.getNumberInstance(new Locale("ro", "RO"));

    private final String[] invoiceTableHeader = new String[]{"Nr crt.", "Denumire produs", "Cantitate",
            "Preț unitar (fără TVA)", "Valoare", "Valoare TVA", "Total cu TVA"};

    private String invoiceNo;

    private final Font font9 = getFreeSansItalic(9, Color.BLACK);

    private final Font font9r = getFreeSansRegular(9, Color.BLACK);

    private final Font font10b = getFreeSansBold( 10, Color.BLACK);

    private final Font font11 = getFreeSansRegular( 11, Color.BLACK);

    private final Font font11w = getFreeSansRegular( 11, Color.WHITE);

    private final Font font11b = getFreeSansBold(11, Color.BLACK);

    private final Font font13b = getFreeSansBold(13, Color.BLACK);

    private final Font font14 = getFreeSansRegular( 14, Color.BLACK);

    private final Font font16b = getFreeSansBold( 16, Color.BLACK);

    private final Font font21b = getFreeSansBold( 21, Color.BLACK);

    private final Font font28b = getHelvetica(FontFactory.HELVETICA, 32, Color.BLACK, Font.BOLD);

    public static final String FREE_SANS = "font/FreeSans.ttf";

    public static final String FREE_SANS_BOLD = "font/FreeSansBold.ttf";

    public static final String FREE_SANS_ITALIC = "font/FreeSansOblique.ttf";

    public void setResponseHeader(HttpServletResponse httpResponse, String contentType, String extension, String prefix) {
        httpResponse.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + getFilename(extension, prefix);
        httpResponse.setHeader(headerKey, headerValue);
    }

    private String getFilename(String extension, String prefix) {
        return prefix + invoiceNo + extension;
    }

    private String getInvoiceNumber(Order order) {
        return order.getId() + dfShortDate.format(order.getOrderTime()) + "_" + dfHyphenSeparated.format(order.getOrderTime());
    }

    public void exportPdf(HttpServletResponse response, Order order) {
        invoiceNo = getInvoiceNumber(order);
        setResponseHeader(response, "application/pdf", ".pdf", "Factura_");

        try (Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Paragraph paragraph = new Paragraph("", font14);
            paragraph.setAlignment(Element.ALIGN_CENTER);

            PdfPTable table1 = getTable1();
            PdfPTable table2 = getTable2(order);
            PdfPTable table3 = getTable3(order);
            PdfPTable table4 = getTable4(order);
            PdfPTable table5 = getTable5();
            paragraph.add(table1);
            paragraph.add(table2);
            paragraph.add(table3);
            paragraph.add(table4);
            paragraph.add(table5);

            document.add(paragraph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PdfPTable getTable1() throws IOException {
        PdfPTable table1 = new PdfPTable(3);
        table1.setWidthPercentage(100f);
        table1.setSpacingBefore(1);
        table1.setWidths(new float[]{10f, 10f, 10f});

        PdfPCell cell11 = new PdfPCell();
        cell11.setBackgroundColor(Color.WHITE);
        cell11.setPadding(5);
        cell11.setBorder(Rectangle.NO_BORDER);
        com.lowagie.text.Image logo = Image.getInstance("site-logo/circuitbrain.png");
        logo.scaleAbsolute(68, 63);
        cell11.addElement(logo);
        cell11.setBorder(Rectangle.BOTTOM);
        cell11.setPaddingBottom(10);
        table1.addCell(cell11);

        PdfPCell cell12 = new PdfPCell();
        cell12.setBackgroundColor(Color.WHITE);
        cell12.setPadding(5);
        cell12.setBorder(Rectangle.NO_BORDER);
        cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell12.setVerticalAlignment(Element.ALIGN_CENTER);
        cell12.setPhrase(new Phrase("FACTURA", font28b));
        cell12.setBorder(Rectangle.BOTTOM);
        cell12.setPaddingBottom(10);
        table1.addCell(cell12);

        PdfPCell cell13 = new PdfPCell();
        cell13.setBackgroundColor(Color.WHITE);
        cell13.setPadding(5);
        cell13.setBorder(Rectangle.NO_BORDER);
        cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell13.setVerticalAlignment(Element.ALIGN_CENTER);
        cell13.setBorder(Rectangle.BOTTOM);
        cell13.setPaddingBottom(10);
        table1.addCell(cell13);

        return table1;
    }

    private PdfPTable getTable2(Order order) {
        PdfPTable table2 = new PdfPTable(3);
        table2.setWidthPercentage(100f);
        table2.setSpacingBefore(5);
        table2.setWidths(new float[]{10f, 10f, 10f});

        PdfPCell cell21 = new PdfPCell();
        cell21.setBackgroundColor(Color.WHITE);
        cell21.setPadding(5);
        cell21.setBorder(Rectangle.NO_BORDER);
        cell21.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell21.setVerticalAlignment(Element.ALIGN_CENTER);
        cell21.setBorder(Rectangle.BOTTOM);
        cell21.setPhrase(new Phrase(
                "Comanda numărul: " + order.getId() +
                        "\nCota TVA: 19%",
                font14));
        cell21.setPaddingBottom(10);
        table2.addCell(cell21);

        PdfPCell cell22 = new PdfPCell();
        cell22.setBackgroundColor(Color.WHITE);
        cell22.setPadding(5);
        cell22.setBorder(Rectangle.NO_BORDER);
        cell22.setBorder(Rectangle.BOTTOM);
        cell21.setPaddingBottom(10);
        table2.addCell(cell22);

        PdfPCell cell23 = new PdfPCell();
        cell23.setBackgroundColor(Color.WHITE);
        cell23.setPadding(5);
        cell23.setBorder(Rectangle.NO_BORDER);
        cell23.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell23.setVerticalAlignment(Element.ALIGN_CENTER);
        cell23.setBorder(Rectangle.BOTTOM);
        cell23.setPhrase(new Phrase(
                "Factura numărul: " + order.getId() + dfShortDate.format(order.getOrderTime()) +
                        "\nData: " + dfSlashSeparated.format(order.getOrderTime())
                , font14));
        cell21.setPaddingBottom(10);
        table2.addCell(cell23);

        return table2;
    }

    private PdfPTable getTable3(Order order) {
        String paymentMethod = formatPaymentMethod(order.getPaymentMethod());

        PdfPTable table3 = new PdfPTable(2);
        table3.setWidthPercentage(100f);
        table3.setSpacingBefore(5);

        PdfPCell cell31 = new PdfPCell();
        cell31.setBackgroundColor(Color.WHITE);
        cell31.setPadding(5);
        cell31.setBorder(Rectangle.NO_BORDER);
        Phrase p1 = new Phrase();
        p1.add(new Phrase("Circuit Store", font21b));
        p1.add(new Phrase("\n\n", font11b));
        p1.add(new Phrase("• Capital social: ", font11));
        p1.add(new Phrase("5000 lei", font11b));
        p1.add(new Phrase("\n• Nr. de înregistrare: ", font11));
        p1.add(new Phrase("J00/000/2022", font11b));
        p1.add(new Phrase("\n• C.U.I.: ", font11));
        p1.add(new Phrase("RO 10000000", font11b));
        p1.add(new Phrase("\n• Adresa: ", font11));
        p1.add(new Phrase("București, Str. Electronicii, nr. 10", font11b));
        p1.add(new Phrase("\n• Email: ", font11));
        p1.add(new Phrase("circuit.ecom@gmail.com", font11b));
        p1.add(new Phrase("\n• Cont: ", font11));
        p1.add(new Phrase("RO82INGB0000999900000000", font11b));
        p1.add(new Phrase("\n• Banca: ", font11));
        p1.add(new Phrase("ING Bank", font11b));
        cell31.setPhrase(p1);
        table3.addCell(cell31);

        PdfPCell cell32 = new PdfPCell();
        cell32.setBackgroundColor(Color.WHITE);
        cell32.setPadding(5);
        cell32.setBorder(Rectangle.NO_BORDER);
        Phrase p2 = new Phrase();
        p2.add(new Phrase("• Cumpărător: ", font11));
        p2.add(new Phrase(order.getCustomer().getFullName(), font11b));
        p2.add(new Phrase("\n• Telefon: ", font11));
        p2.add(new Phrase(order.getCustomer().getPhoneNumber(), font11b));
        p2.add(new Phrase("\n• Email: ", font11));
        p2.add(new Phrase(order.getCustomer().getEmail(), font11b));
        p2.add(new Phrase("\n• Adresa: ", font11));
        p2.add(new Phrase(order.getCustomer().getAddressLine1() + "\n" + order.getCustomer().getAddressLine2(), font11b));
        p2.add(new Phrase("\n• Cod poștal: ", font11));
        p2.add(new Phrase(order.getCustomer().getPostalCode(), font11b));
        p2.add(new Phrase("\n• Oraș: ", font11));
        p2.add(new Phrase(order.getCustomer().getCity(), font11b));
        p2.add(new Phrase("\n• Regiune administrativă: ", font11));
        p2.add(new Phrase(order.getCustomer().getState(), font11b));
        p2.add(new Phrase("\n• Țara: ", font11));
        p2.add(new Phrase(order.getCustomer().getCountry().getName(), font11b));
        p2.add(new Phrase("\n• Modalitate plată: ", font11));
        p2.add(new Phrase(paymentMethod, font11b));
        p2.add(new Phrase("\n• Id comandă: ", font11));
        p2.add(new Phrase(order.getId().toString(), font11b));
        cell32.setPhrase(p2);
        table3.addCell(cell32);

        return table3;
    }

    private PdfPTable getTable4(Order order) {
        PdfPTable table4 = new PdfPTable(7);
        table4.setWidthPercentage(100f);
        table4.setSpacingBefore(15);
        table4.setWidths(new float[]{12f, 104f, 26f, 32f, 25f, 23f, 24f});
        writeTableHeader(table4);
        writeTableData(table4, order);
        return table4;
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.GRAY);
        cell.setPadding(5);
        cell.setPaddingBottom(10);

        for (String s : invoiceTableHeader) {
            cell.setPhrase(new Phrase(s, font11w));
            table.addCell(cell);
        }
    }

    private void writeTableData(PdfPTable table, Order order){
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setMaximumFractionDigits(2);
        int counter = 1;

        double shippingNoVat = order.getShippingCost() / 1.19;
        double subtotalValue = order.getTotal() / 1.19;
        double subtotalVat = order.getTotal() - subtotalValue;

        for (OrderDetail detail : order.getOrderDetails()) {
            int quantity = detail.getQuantity();
            double unitPrice = detail.getUnitPrice() / 1.19;
            double subtotal = quantity * unitPrice;
            double vat = subtotal * 0.19;

            PdfPCell counterCell = new PdfPCell(new Phrase(String.valueOf(counter++), font11));
            counterCell.setPaddingBottom(10);
            counterCell.setPaddingTop(10);
            counterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(counterCell);

            PdfPCell nameCell = new PdfPCell(new Phrase(detail.getProduct().getName(), font11));
            nameCell.setPaddingBottom(10);
            nameCell.setPaddingTop(10);
            table.addCell(nameCell);

            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(quantity), font11));
            quantityCell.setPaddingBottom(10);
            quantityCell.setPaddingTop(10);
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(quantityCell);

            PdfPCell unitPriceCell = new PdfPCell(new Phrase(String.valueOf(format.format(unitPrice)), font11));
            unitPriceCell.setPaddingBottom(10);
            unitPriceCell.setPaddingTop(10);
            unitPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(unitPriceCell);

            PdfPCell totalPriceCell = new PdfPCell(new Phrase(String.valueOf(format.format(subtotal)), font11));
            totalPriceCell.setPaddingBottom(10);
            totalPriceCell.setPaddingTop(10);
            totalPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(totalPriceCell);

            PdfPCell vatCell = new PdfPCell(new Phrase(String.valueOf(format.format(vat)), font11));
            vatCell.setPaddingBottom(10);
            vatCell.setPaddingTop(10);
            vatCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(vatCell);

            PdfPCell vatIncludedCell = new PdfPCell(new Phrase(String.valueOf(format.format(detail.getSubtotal())), font11));
            vatIncludedCell.setPaddingBottom(10);
            vatIncludedCell.setPaddingTop(10);
            vatIncludedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(vatIncludedCell);
        }

        PdfPCell shippingC1 = new PdfPCell(new Phrase(String.valueOf(counter), font11));
        shippingC1.setPaddingBottom(10);
        shippingC1.setPaddingTop(10);
        shippingC1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC1);

        PdfPCell shippingC2 = new PdfPCell(new Phrase("Serviciu transport", font11));
        shippingC2.setPaddingBottom(10);
        shippingC2.setPaddingTop(10);
        shippingC2.setRowspan(3);
        table.addCell(shippingC2);

        PdfPCell shippingC3 = new PdfPCell(new Phrase("1", font11));
        shippingC3.setPaddingBottom(10);
        shippingC3.setPaddingTop(10);
        shippingC3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC3);

        PdfPCell shippingC4 = new PdfPCell(new Phrase(String.valueOf(format.format(shippingNoVat)), font11));
        shippingC4.setPaddingBottom(10);
        shippingC4.setPaddingTop(10);
        shippingC4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC4);

        PdfPCell shippingC5 = new PdfPCell(new Phrase(String.valueOf(format.format(shippingNoVat)), font11));
        shippingC5.setPaddingBottom(10);
        shippingC5.setPaddingTop(10);
        shippingC5.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC5);

        PdfPCell shippingC6 = new PdfPCell(new Phrase(String.valueOf(format.format(shippingNoVat * 0.19)), font11));
        shippingC6.setPaddingBottom(10);
        shippingC6.setPaddingTop(10);
        shippingC6.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC6);

        PdfPCell shippingC7 = new PdfPCell(new Phrase(String.valueOf(format.format(order.getShippingCost())), font11));
        shippingC7.setPaddingBottom(15);
        shippingC7.setPaddingTop(10);
        shippingC7.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(shippingC7);

        PdfPCell c8 = new PdfPCell(new Phrase("Subtotal:", font13b));
        c8.setPaddingBottom(10);
        c8.setPaddingTop(10);
        c8.setPaddingRight(20);
        c8.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c8.setColspan(4);
        table.addCell(c8);

        PdfPCell c9 = new PdfPCell(new Phrase(format.format(subtotalValue), font11));
        c9.setPaddingBottom(10);
        c9.setPaddingTop(10);
        c9.setHorizontalAlignment(Element.ALIGN_CENTER);
        c9.setColspan(1);
        table.addCell(c9);

        PdfPCell c10 = new PdfPCell(new Phrase(format.format(subtotalVat), font11));
        c10.setPaddingBottom(10);
        c10.setPaddingTop(10);
        c10.setHorizontalAlignment(Element.ALIGN_CENTER);
        c10.setColspan(1);
        table.addCell(c10);

        PdfPCell c11 = new PdfPCell(new Phrase(format.format(order.getTotal()), font11));
        c11.setPaddingBottom(10);
        c11.setPaddingTop(10);
        c11.setHorizontalAlignment(Element.ALIGN_CENTER);
        c11.setColspan(1);
        table.addCell(c11);

        String total = "Total de plată: " + format.format(order.getTotal()) + " €";
        PdfPCell c12 = new PdfPCell(new Phrase(total, font16b));
        c12.setPaddingBottom(10);
        c12.setPaddingTop(10);
        c12.setPaddingRight(20);
        c12.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c12.setColspan(7);
        table.addCell(c12);
    }

    private PdfPTable getTable5() {
        PdfPTable table5 = new PdfPTable(3);
        table5.setWidthPercentage(100f);
        table5.setSpacingBefore(35);

        Phrase p1 = new Phrase();
        p1.add(new Phrase("Semnătura și ștampila furnizorului", font10b));
        p1.add(new Phrase("\n\nConform Codului Fiscal art. 319 aliniatul 29, semnarea și ștampilarea facturilor nu sunt obligatorii.", font9));
        PdfPCell c1 = new PdfPCell(p1);
        c1.setPaddingRight(10);
        c1.setPaddingLeft(10);
        c1.setBorder(Rectangle.NO_BORDER);
        table5.addCell(c1);

        Phrase p2 = new Phrase();
        p2.add(new Phrase("Semnătura de primire", font10b));
        PdfPCell c2 = new PdfPCell(p2);
        c2.setPaddingRight(10);
        c2.setPaddingLeft(10);
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.LEFT);
        table5.addCell(c2);

        Phrase p3 = new Phrase();
        p3.add(new Phrase("Date privind expediția:", font10b));
        p3.add(new Phrase("\n\nNumele delegatului:", font9r));
        p3.add(new Phrase("\nC.I. serie:        număr:", font9r));
        p3.add(new Phrase("\nCNP:", font9r));

        PdfPCell c3 = new PdfPCell(p3);
        c3.setPaddingLeft(10);
        c3.setBorder(Rectangle.NO_BORDER);
        c3.setBorder(Rectangle.LEFT);
        table5.addCell(c3);

        return table5;
    }

    private Font getHelvetica(String fontName, int fontSize, Color fontColor, int style) {
        Font f = new Font(Font.HELVETICA, fontSize, style);
        f.setColor(fontColor);
        f.setFamily(fontName);
        return f;
    }

    private Font getFreeSansBold(int fontSize, Color fontColor) {
        Font f = FontFactory.getFont(FREE_SANS_BOLD, BaseFont.IDENTITY_H, true);
        f.setColor(fontColor);
        f.setSize(fontSize);
        return f;
    }
    private Font getFreeSansRegular(int fontSize, Color fontColor) {
        Font f = FontFactory.getFont(FREE_SANS, BaseFont.IDENTITY_H, true);
        f.setColor(fontColor);
        f.setSize(fontSize);
        return f;
    }

    private Font getFreeSansItalic(int fontSize, Color fontColor) {
        Font f = FontFactory.getFont(FREE_SANS_ITALIC, BaseFont.IDENTITY_H, true);
        f.setColor(fontColor);
        f.setSize(fontSize);
        return f;
    }

    private String formatPaymentMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case PAYPAL: return "PayPal";
            case CREDIT_CARD: return "card bancar";
            default: return "numerar";
        }
    }
}
