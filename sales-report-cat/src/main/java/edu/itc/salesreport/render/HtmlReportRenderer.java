
package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;

import java.nio.charset.StandardCharsets;

public final class HtmlReportRenderer
        implements ReportRenderer {

    @Override
    public String extension() {
        return "html";
    }

    @Override
    public byte[] render(MonthlyReport report) {
        StringBuilder out = new StringBuilder();

        out.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <title>Angkor Mart Monthly Sales</title>
                </head>
                <body>
                <h1>Angkor Mart Monthly Sales Report</h1>
                <p>Month: """)
                .append(escape(report.month().toString()))
                .append("""
                </p>
                <table border="1">
                <thead><tr>
                <th>Branch</th><th>Revenue</th>
                <th>Receipts</th><th>Average Basket</th>
                </tr></thead><tbody>
                """);

        for (BranchSummary b : report.branches()) {
            appendBranch(out, b);
        }

        appendBranch(out, report.chain());

        out.append("""
                </tbody></table>
                </body></html>
                """);

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendBranch(
            StringBuilder out, BranchSummary b) {

        out.append("<tr><td>")
                .append(escape(b.branch()))
                .append("</td><td>")
                .append(escape(b.revenue().toPlainString()))
                .append("</td><td>")
                .append(b.receipts())
                .append("</td><td>")
                .append(escape(
                        b.averageBasket().toPlainString()))
                .append("</td></tr>\n");
    }

    private static String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
