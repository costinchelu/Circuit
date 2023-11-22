package ro.ase.costin.ecomback.export;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractExporter {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    protected final String[] userHeader = {"ID", "E-mail", "Prenume", "Nume", "Permisiuni", "Activat"};

    protected final String[] productPdfHeader = {"ID", "Imagine", "Denumire", "Marca", "Categorie", "Cost", "Pret", "% discount"};

    protected final String[] categoryCsvHeader = {"ID categorie", "Nume categorie"};

    protected final String[] brandCsvHeader = {"ID marca", "Nume", "Categorii"};

    protected final String[] productCsvHeader = {"ID", "Denumire", "Brand", "Categorie", "Cost", "Pret", "% discount"};

    protected final String[] customerCsvHeader = {"ID", "Nume", "Prenume", "Email", "Telefon", "Adresa r1", "Adresa r2",
            "Cod postal", "Oras", "Regiune", "Tara", "Autentificare"};

    public void setResponseHeader(HttpServletResponse httpResponse, String contentType, String extension, String prefix) {
        httpResponse.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + getFilename(extension, prefix);
        httpResponse.setHeader(headerKey, headerValue);
    }

    private String getFilename(String extension, String prefix) {
        String timestamp = dateFormat.format(new Date());
        return prefix + timestamp + extension;
    }
}