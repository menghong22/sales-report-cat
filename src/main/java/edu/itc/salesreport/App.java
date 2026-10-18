package edu.itc.salesreport;

import edu.itc.salesreport.ingest.ConsoleProgress;
import edu.itc.salesreport.ingest.CsvTransactionParser;
import edu.itc.salesreport.ingest.MonthLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.YearMonth;

public class App {

    public static void main(String[] args)
            throws IOException {

        YearMonth month = args.length >= 1
                ? YearMonth.parse(args[0])
                : YearMonth.of(2026, 9);

        Path dataDir = args.length >= 2
                ? Path.of(args[1])
                : Path.of("data");

        MonthLoader loader =
                new MonthLoader(new CsvTransactionParser());

        loader.subscribe(new ConsoleProgress());

        var transactions = loader.load(month, dataDir);

        System.out.printf(
                "Loaded %d valid transactions.%n",
                transactions.size());

        System.out.printf(
                "Rejected %d rows.%n",
                loader.rejectedRows().size());

        loader.rejectedRows().forEach(
                System.out::println);
    }
}
