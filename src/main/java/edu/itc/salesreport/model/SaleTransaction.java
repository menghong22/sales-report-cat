package edu.itc.salesreport.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public record SaleTransaction(
        String branch,
        LocalDate date,
        String receiptNo,
        String sku,
        String productName,
        String category,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal discount,
        PaymentMethod paymentMethod
) {

    public SaleTransaction {
        Objects.requireNonNull(branch, "branch");
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(receiptNo, "receiptNo");
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(productName, "productName");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(discount, "discount");
        Objects.requireNonNull(paymentMethod, "paymentMethod");

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "quantity must be > 0");
        }

        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "unitPrice cannot be negative");
        }

        if (discount.signum() < 0) {
            throw new IllegalArgumentException(
                    "discount cannot be negative");
        }

        BigDecimal lineTotal = unitPrice.multiply(
                BigDecimal.valueOf(quantity));

        if (discount.compareTo(lineTotal) > 0) {
            throw new IllegalArgumentException(
                    "discount exceeds line total");
        }
    }

    public BigDecimal revenue() {
        return unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
