
package edu.itc.salesreport.render;

import edu.itc.salesreport.model.BranchSummary;
import edu.itc.salesreport.model.MonthlyReport;
import edu.itc.salesreport.model.PaymentMethod;
import edu.itc.salesreport.model.ProductTotal;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

final class TestReports {

    private TestReports() {}

    static MonthlyReport sample() {
        BranchSummary pnh = branch(
                "PNH", "1200.00", 100);

        BranchSummary rep = branch(
                "REP", "800.50", 50);

        BranchSummary btb = branch(
                "BTB", "300.00", 40);

        BranchSummary chain = branch(
                "ALL", "2300.50", 190);

        return new MonthlyReport(
                YearMonth.of(2026, 9),
                List.of(pnh, rep, btb),
                chain,
                List.of()
        );
    }

    private static BranchSummary branch(
            String name, String revenue, long receipts) {

        BigDecimal amount = new BigDecimal(revenue);

        return new BranchSummary(
                name,
                amount,
                new BigDecimal("0.00"),
                receipts,
                Map.of("Grocery", amount),
                List.of(new ProductTotal(
                        "SKU-001",
                        "Test Product",
                        amount,
                        10)),
                Map.of(PaymentMethod.CASH, amount)
        );
    }
}
