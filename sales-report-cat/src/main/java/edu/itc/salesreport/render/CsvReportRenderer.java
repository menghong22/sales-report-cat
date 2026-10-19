
package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;

import java.nio.charset.StandardCharsets;

public final class CsvReportRenderer
        implements ReportRenderer {

    @Override
    public String extension() {
        return "csv";
    }

    @Override
    public byte[] render(MonthlyReport report) {
        StringBuilder out = new StringBuilder();

        out.append(
                "month,branch,revenue,discounts,"
                + "receipts,average_basket\n");

        for (BranchSummary b : report.branches()) {
            appendBranch(out, report, b);
        }

        appendBranch(out, report, report.chain());

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendBranch(
            StringBuilder out,
            MonthlyReport report,
            BranchSummary b) {

        out.append(report.month()).append(',')
                .append(csv(b.branch())).append(',')
                .append(b.revenue().toPlainString()).append(',')
                .append(b.discounts().toPlainString()).append(',')
                .append(b.receipts()).append(',')
                .append(b.averageBasket().toPlainString())
                .append('\n');
    }

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
