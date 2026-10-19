
package edu.itc.salesreport.ingest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonthLoaderTest {

    @TempDir
    Path tempDir;

    private static final String HEADER =
            CsvTransactionParser.HEADER;

    private static final String VALID =
            "PNH,2026-09-01,PNH-1,SKU-1,Rice,"
            + "Grocery,2,6.50,0.00,CASH";

    @Test
    void loadsFilesAndPublishesEvents()
            throws Exception {

        Path monthDir =
                Files.createDirectories(
                        tempDir.resolve("2026-09"));

        Files.writeString(
                monthDir.resolve("PNH-2026-09-01.csv"),
                HEADER + "\n" + VALID + "\n");

        Files.writeString(
                monthDir.resolve("PNH-2026-09-02.csv"),
                HEADER + "\n" + VALID + "\n"
                        + "invalid,row\n");

        MonthLoader loader =
                new MonthLoader(new CsvTransactionParser());

        List<LoadEvent> events = new ArrayList<>();

        loader.subscribe(events::add);

        var transactions = loader.load(
                YearMonth.of(2026, 9), tempDir);

        assertEquals(2, transactions.size());
        assertEquals(1, loader.rejectedRows().size());
        assertEquals(3, events.size());

        assertInstanceOf(
                LoadEvent.FileLoaded.class, events.get(0));

        assertInstanceOf(
                LoadEvent.FileLoaded.class, events.get(1));

        LoadEvent.LoadFinished finished =
                (LoadEvent.LoadFinished) events.get(2);

        assertEquals(2, finished.accepted());
        assertEquals(1, finished.rejected());
    }

    @Test
    void unsubscribeStopsListenerNotifications()
            throws Exception {

        Path monthDir =
                Files.createDirectories(
                        tempDir.resolve("2026-09"));

        Files.writeString(
                monthDir.resolve("PNH-2026-09-01.csv"),
                HEADER + "\n" + VALID + "\n");

        MonthLoader loader =
                new MonthLoader(new CsvTransactionParser());

        List<LoadEvent> events = new ArrayList<>();

        Runnable unsubscribe =
                loader.subscribe(events::add);

        unsubscribe.run();

        loader.load(YearMonth.of(2026, 9), tempDir);

        assertTrue(events.isEmpty());
    }
}
