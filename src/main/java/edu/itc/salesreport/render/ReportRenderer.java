
package edu.itc.salesreport.render;

import edu.itc.salesreport.model.MonthlyReport;

public sealed interface ReportRenderer
        permits TextReportRenderer,
                HtmlReportRenderer,
                CsvReportRenderer {

    String extension();

    byte[] render(MonthlyReport report);

    static ReportRenderer forExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "txt" -> new TextReportRenderer();
            case "html" -> new HtmlReportRenderer();
            case "csv" -> new CsvReportRenderer();
            default -> throw new IllegalArgumentException(
                    "Unsupported extension: " + extension);
        };
    }
}
