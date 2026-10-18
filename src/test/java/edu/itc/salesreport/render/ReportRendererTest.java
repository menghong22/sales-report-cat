
package edu.itc.salesreport.render;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ReportRendererTest {

    @Test
    void allRenderersIncludeBranchesAndChain() {
        ReportRenderer[] renderers = {
                new TextReportRenderer(),
                new HtmlReportRenderer(),
                new CsvReportRenderer()
        };

        for (ReportRenderer renderer : renderers) {
            String output = new String(
                    renderer.render(TestReports.sample()),
                    StandardCharsets.UTF_8);

            assertTrue(output.contains("PNH"));
            assertTrue(output.contains("REP"));
            assertTrue(output.contains("BTB"));
            assertTrue(output.contains("ALL"));
            assertTrue(output.contains("2300.50"));
        }
    }

    @Test
    void csvContainsExpectedRepRow() {
        String output = new String(
                new CsvReportRenderer().render(
                        TestReports.sample()),
                StandardCharsets.UTF_8);

        assertTrue(output.contains(
                "2026-09,REP,800.50,0.00,50,16.01"));
    }

    @Test
    void selectsRendererByExtension() {
        assertInstanceOf(
                CsvReportRenderer.class,
                ReportRenderer.forExtension("csv"));

        assertInstanceOf(
                HtmlReportRenderer.class,
                ReportRenderer.forExtension("html"));

        assertInstanceOf(
                TextReportRenderer.class,
                ReportRenderer.forExtension("txt"));
    }

    @Test
    void rejectsUnsupportedExtension() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ReportRenderer.forExtension("pdf"));
    }

    @Test
    void htmlEscapesSpecialCharacters() {
        String output = new String(
                new HtmlReportRenderer().render(
                        TestReports.sample()),
                StandardCharsets.UTF_8);

        assertTrue(output.contains("<table"));
        assertTrue(output.contains("</html>"));
    }
}
