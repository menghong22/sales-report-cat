
package edu.itc.salesreport.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SaleTransactionTest {

    @Test
    void revenueUsesHalfUpRounding() {

        var transaction = new SaleTransaction(
                "PNH",
                LocalDate.of(2026, 9, 1),
                "PNH-001",
                "SKU-001",
                "Test Product",
                "Grocery",
                3,
                new BigDecimal("0.335"),
                new BigDecimal("0.00"),
                PaymentMethod.CASH
        );

        assertEquals(
                new BigDecimal("1.01"),
                transaction.revenue());
    }

    @Test
    void rejectsDiscountAboveLineTotal() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new SaleTransaction(
                        "PNH",
                        LocalDate.of(2026, 9, 1),
                        "PNH-001",
                        "SKU-001",
                        "Test Product",
                        "Grocery",
                        2,
                        new BigDecimal("5.00"),
                        new BigDecimal("15.00"),
                        PaymentMethod.CASH
                ));
    }

    @Test
    void topProductsIsUnmodifiable() {

        var products = new ArrayList<ProductTotal>();

        products.add(new ProductTotal(
                "SKU-001",
                "Test Product",
                new BigDecimal("100.00"),
                10
        ));

        var summary = new BranchSummary(
                "PNH",
                new BigDecimal("100.00"),
                new BigDecimal("0.00"),
                10,
                Map.of("Grocery",
                        new BigDecimal("100.00")),
                products,
                Map.of(PaymentMethod.CASH,
                        new BigDecimal("100.00"))
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> summary.topProducts().clear());

        products.clear();

        assertEquals(1, summary.topProducts().size());
    }
}
