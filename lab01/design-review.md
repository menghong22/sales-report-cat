# Design Review: Angkor Mart Sales Report

## 1. Package Responsibilities and Cohesion

| Package  | Responsibility                                                                                                          | Cohesion                                                            |
| -------- | ----------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| `model`  | Defines the canonical sales transaction and report data types, including validation and money calculations.             | High: types represent the domain concepts.                          |
| `ingest` | Reads daily CSV files, validates rows through the parser, records rejected rows, and publishes loading progress events. | High: all classes support data ingestion.                           |
| `render` | Converts a monthly report into text, HTML, or CSV output.                                                               | High: each renderer implements the same output contract.            |
| `ui`     | Provides user-facing interfaces for viewing reports and progress.                                                       | Intended responsibility; implementation may be added in later labs. |

## 2. Package Coupling

The following counts use direct imports between project packages in `src/main/java`. Java standard-library and third-party imports are excluded.

* **Ce (efferent coupling):** Number of distinct other project packages this package imports.
* **Ca (afferent coupling):** Number of other project packages that import this package.
* **Instability:** \(I = Ce / (Ca + Ce)\). If both values are zero, I is undefined and is reported as N/A.

Assuming the current implementation follows the intended dependency direction:

| Package  | Ce | Ca | Instability calculation |    I |
| -------- | -: | -: | ----------------------- | ---: |
| `model`  |  0 |  3 | 0 / (3 + 0)             | 0.00 |
| `ingest` |  1 |  1 | 1 / (1 + 1)             | 0.50 |
| `render` |  1 |  0 | 1 / (0 + 1)             | 1.00 |
| `ui`     |  2 |  0 | 2 / (0 + 2)             | 1.00 |

**Important:** These are provisional counts based on the intended architecture. Verify them against the actual Java imports before submission. An empty or not-yet-implemented `ui` package must not be counted as if it already contained classes.

## 3. Dependency Direction

The intended dependency direction is:

* `model` depends only on Java types and has no dependencies on other project packages.
* `ingest` depends on `model` to create validated transactions.
* `render` depends on `model` to read monthly reports.
* `ui` may coordinate ingestion and rendering, but domain types should remain independent of the UI.

This arrangement keeps domain rules independent of input and output mechanisms. The dependency rules test should fail if `model` imports another project package or `ingest` imports `render`.

## 4. Accepted Coupling

The dependency from `ingest` to `model` is intentional. The CSV parser must construct `SaleTransaction` objects, so removing this dependency would require an unnecessary abstraction or duplicate data model.

The renderer dependency on `model` is also appropriate: renderers need access to `MonthlyReport` and `BranchSummary`, but the model must not depend on rendering formats.

## 5. Sealed Renderer Hierarchy and QA-4

The current `ReportRenderer` is a sealed interface permitting `TextReportRenderer`, `HtmlReportRenderer`, and `CsvReportRenderer`.

This is appropriate for the initial release because it makes the supported renderer set explicit and enables exhaustive switches. However, adding a new renderer requires modifying the permits list and rebuilding the application.

For QA-4 (modifiability), the sealed hierarchy is a reasonable choice while the supported formats are controlled by the development team. If third parties must add renderers independently, an open interface discovered through registration or dependency injection would be more suitable.

## 6. Conclusion

The package boundaries support high cohesion and a clear dependency direction. The main design risk is that future UI or reporting requirements could introduce unwanted dependencies. Keep the model independent, enforce dependency rules with tests, and recalculate coupling after adding new packages.
