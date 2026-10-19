
package edu.itc.salesreport;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DependencyRulesTest {

    private static final Path ROOT =
            Path.of("src/main/java/edu/itc/salesreport");

    private static boolean containsForbiddenImport(
            Path folder, String forbidden)
            throws Exception {

        try (Stream<Path> paths = Files.walk(folder)) {
            return paths
                    .filter(p -> p.toString().endsWith(".java"))
                    .anyMatch(p -> {
                        try {
                            return Files.readString(p)
                                    .lines()
                                    .anyMatch(line ->
                                            line.strip()
                                                    .startsWith("import ")
                                                    && line.contains(
                                                            forbidden));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    void modelDoesNotImportApplicationPackages()
            throws Exception {

        assertFalse(
                containsForbiddenImport(
                        ROOT.resolve("model"),
                        "edu.itc.salesreport."));
    }

    @Test
    void ingestDoesNotImportRenderPackage()
            throws Exception {

        assertFalse(
                containsForbiddenImport(
                        ROOT.resolve("ingest"),
                        "edu.itc.salesreport.render."));
    }
}
