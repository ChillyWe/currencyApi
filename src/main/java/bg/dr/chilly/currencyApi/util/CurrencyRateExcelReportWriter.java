package bg.dr.chilly.currencyApi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import bg.dr.chilly.currencyApi.db.projection.CurrencyRateView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class CurrencyRateExcelReportWriter {

    private CurrencyRateExcelReportWriter() { }

    public static ByteArrayInputStream writeExcelReport(List<CurrencyRateView> currencyRates) throws IOException {
        String[] COLUMNs = {"Id", "CreatedOn", "UpdatedOn", "Base", "Quote", "Name", "Rate", "ReverseRate", "Source",
            "SourceCreatedOn"};
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Currency Rate Export");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Row for Header
            Row headerRow = sheet.createRow(0);

            // Header
            for (int col = 0; col < COLUMNs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(COLUMNs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            for (CurrencyRateView view : currencyRates) {
                Row row = sheet.createRow(rowIdx++);

                CreationHelper createHelper = workbook.getCreationHelper();
                CellStyle dateStyle = workbook.createCellStyle();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm:ss"));

                row.createCell(0).setCellValue(view.getId());

                Cell cell = row.createCell(1);
                cell.setCellValue((map(view.getCreatedOn())));
                cell.setCellStyle(dateStyle);

                cell = row.createCell(2);
                cell.setCellValue((map(view.getUpdatedOn())));
                cell.setCellStyle(dateStyle);

                row.createCell(3).setCellValue(view.getBase());
                row.createCell(4).setCellValue(view.getQuote().getId());
                row.createCell(5).setCellValue(view.getQuote().getName());
                row.createCell(6).setCellValue(view.getRate().doubleValue());
                row.createCell(7).setCellValue(view.getReverseRate().doubleValue());
                row.createCell(8).setCellValue(view.getSource());

                cell = row.createCell(9);
                cell.setCellValue((map(view.getSourceCreatedOn())));
                cell.setCellStyle(dateStyle);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private static LocalDateTime map(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

}
