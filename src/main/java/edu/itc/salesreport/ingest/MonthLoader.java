package edu.itc.salesreport.ingest;

import edu.itc.salesreport.model.SaleTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public final class MonthLoader {

    private final TransactionParser parser;

    private final List<LoadListener> listeners =
            new CopyOnWriteArrayList<>();

    private final List<String> rejectedRows =
            new ArrayList<>();

    public MonthLoader(TransactionParser parser) {
        this.parser = parser;
    }

    public Runnable subscribe(LoadListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public List<String> rejectedRows() {
        return List.copyOf(rejectedRows);
    }

    private void publish(LoadEvent event) {
        for (LoadListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public List<SaleTransaction> load(
            YearMonth month, Path dataDir)
            throws IOException {

        long start = System.nanoTime();

        rejectedRows.clear();

        List<SaleTransaction> transactions =
                new ArrayList<>();

        Path monthDir = dataDir.resolve(month.toString());

        List<Path> files;

        try (Stream<Path> paths = Files.list(monthDir)) {
            files = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString()
                            .endsWith(".csv"))
                    .sorted(Comparator.comparing(
                            p -> p.getFileName().toString()))
                    .toList();
        }

        int acceptedTotal = 0;
        int rejectedTotal = 0;
        int filesDone = 0;

        for (Path file : files) {
            int accepted = 0;
            int rejected = 0;
            int lineNumber = 0;

            try (BufferedReader reader =
                         Files.newBufferedReader(file)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;

                    if (lineNumber == 1) {
                        continue; // Skip CSV header
                    }

                    try {
                        SaleTransaction transaction =
                                parser.parse(line);

                        if (!YearMonth.from(transaction.date())
                                .equals(month)) {
                            throw new InvalidRowException(
                                    "date does not match report month");
                        }

                        String filename =
                                file.getFileName().toString();

                        String expectedBranch =
                                filename.substring(0, 3);

                        if (!transaction.branch()
                                .equals(expectedBranch)) {
                            throw new InvalidRowException(
                                    "branch does not match filename");
                        }

                        transactions.add(transaction);
                        accepted++;
                    } catch (InvalidRowException e) {
                        rejected++;

                        rejectedRows.add(
                                file.getFileName()
                                        + ":" + lineNumber
                                        + " " + e.getMessage());
                    }
                }
            }

            acceptedTotal += accepted;
            rejectedTotal += rejected;
            filesDone++;

            publish(new LoadEvent.FileLoaded(
                    file,
                    accepted,
                    rejected,
                    filesDone,
                    files.size()
            ));
        }

        publish(new LoadEvent.LoadFinished(
                acceptedTotal,
                rejectedTotal,
                Duration.ofNanos(
                        System.nanoTime() - start)
        ));

        return List.copyOf(transactions);
    }
}
