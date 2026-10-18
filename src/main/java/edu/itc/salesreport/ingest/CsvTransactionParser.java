package edu.itc.salesreport.ingest;

import edu.itc.salesreport.model.PaymentMethod;
import edu.itc.salesreport.model.SaleTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.function.Function;

public final class CsvTransactionParser
        implements TransactionParser {

    public static final String HEADER =
            "branch,date,receipt_no,sku,product_name,"
            + "category,quantity,unit_price,discount,"
            + "payment_method";

    private static final Set<String> BRANCHES =
            Set.of("PNH", "REP", "BTB");

    private static final Set<String> CATEGORIES =
            Set.of(
                    "Grocery",
                    "Beverages",
                    "Household",
                    "Personal Care",
                    "Electronics",
                    "Stationery"
            );

    @Override
    public SaleTransaction parse(String line)
            throws InvalidRowException {

        if (line == null || line.isBlank()) {
            throw new InvalidRowException("empty line");
        }

        String[] f = line.split(",", -1);

        if (f.length != 10) {
            throw new InvalidRowException(
                    "expected 10 fields but found "
                    + f.length);
        }

        String branch = required("branch", f[0]);

        if (!BRANCHES.contains(branch)) {
            throw new InvalidRowException(
                    "bad branch '" + branch + "'");
        }

        LocalDate date =
                field("date", f[1], LocalDate::parse);

        String receiptNo =
                required("receipt_no", f[2]);

        String sku = required("sku", f[3]);

        String productName =
                required("product_name", f[4]);

        String category =
                required("category", f[5]);

        if (!CATEGORIES.contains(category)) {
            throw new InvalidRowException(
                    "bad category '" + category + "'");
        }

        int quantity =
                field("quantity", f[6],
                        Integer::parseInt);

        BigDecimal unitPrice =
                field("unit_price", f[7],
                        BigDecimal::new);

        BigDecimal discount =
                field("discount", f[8],
                        BigDecimal::new);

        PaymentMethod paymentMethod =
                field("payment_method", f[9],
                        PaymentMethod::valueOf);

        try {
            return new SaleTransaction(
                    branch,
                    date,
                    receiptNo,
                    sku,
                    productName,
                    category,
                    quantity,
                    unitPrice,
                    discount,
                    paymentMethod
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidRowException(
                    e.getMessage(), e);
        }
    }

    private static String required(
            String name, String value)
            throws InvalidRowException {

        if (value == null || value.isBlank()) {
            throw new InvalidRowException(
                    "bad " + name + ": empty value");
        }

        return value.strip();
    }

    private static <T> T field(
            String name,
            String raw,
            Function<String, T> convert)
            throws InvalidRowException {

        try {
            return convert.apply(raw.strip());
        } catch (RuntimeException e) {
            throw new InvalidRowException(
                    "bad " + name + " '" + raw + "'",
                    e);
        }
    }
}
