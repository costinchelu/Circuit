package ro.ase.costin.ecomback.user.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import ro.ase.costin.ecomback.export.AbstractExporter;
import ro.ase.costin.ecomcommon.entity.User;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class UserExcelExporter extends AbstractExporter {

    private final XSSFWorkbook workbook;

    private XSSFSheet sheet;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    public void exportXlsx(List<User> users, HttpServletResponse httpResponse) throws IOException {
        super.setResponseHeader(httpResponse, "application/octet-stream", ".xlsx", "utilizatori_");

        try (ServletOutputStream outputStream = httpResponse.getOutputStream()) {

            writeHeaderLine();
            writeDataLines(users);
            workbook.write(outputStream);

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            workbook.close();
        }
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Utilizatori");
        XSSFRow row = sheet.createRow(0);
        XSSFCellStyle cellStyle = getCellStyle(true, 16);

        for (int i = 0; i < userHeader.length; i++) {
            createCell(row, i, userHeader[i], cellStyle);
        }
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(cellStyle);
    }

    private void writeDataLines(List<User> users) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = getCellStyle(false, 14);

        for (User user : users) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getEmail(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getRoleNames(), cellStyle);
            createCell(row, columnIndex, user.isEnabled() ? "DA" : "NU", cellStyle);
        }
    }

    private XSSFCellStyle getCellStyle(boolean isBold, int fontSize) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(isBold);
        font.setFontHeight(fontSize);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
