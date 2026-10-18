
package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;

import java.nio.charset.StandardCharsets;

public final class TextReportRenderer
        implements ReportRenderer {

    @Override
    public String extension() {
        return "txt";
    }

    @Override
    public byte[] render(MonthlyReport report) {
        StringBuilder out = new StringBuilder();

        out.append("Angkor Mart Monthly Sales Report\n");
        out.append("Month: ")
                .append(report.month())
                .append("\n\n");

        out.append(String.format(
                "%-8s %12s %10s %14s%n",
                "Branch", "Revenue", "Receipts",
                "Average Basket"));

        for (BranchSummary b : report.branches()) {
            appendBranch(out, b);
        }

        out.append("-----------------------------------------------\n");
        appendBranch(out, report.chain());

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendBranch(
            StringBuilder out, BranchSummary b) {

        out.append(String.format(
                "%-8s %12s %10d %14s%n",
                b.branch(),
                b.revenue().toPlainString(),
                b.receipts(),
                b.averageBasket().toPlainString()));
    }
}
