# Quality-attribute scenarios: Angkor Mart monthly sales report

## Stakeholders
- **Head-office analyst:** Views, analyzes, and exports chain-wide and per-branch monthly sales reports.
- **Branch manager (PNH, REP, BTB):** Accesses and monitors sales reports for their assigned branch only.
- **Finance director:** Receives email notifications when monthly reports are generated and finalized.
- **System operator:** Manages, schedules, and monitors batch ingestion and report generation jobs.
- **Development team:** Maintains, tests, and extends system capabilities (e.g., adding export formats).

## Scenarios
| ID | Attribute | Source | Stimulus | Artifact | Environment | Response | Response measure | Rank |
|----|-----------|--------|----------|----------|-------------|----------|------------------|------|
| **QA-1** | Performance | Scheduler | 06:00 start on 2nd day of M+1 for ~3M rows | Report engine | Normal operation (8-core server) | Process all branch files and produce output reports | $\le 60\text{ s}$ wall-clock time (median of 5 runs after 2 warm-ups) | (H,H) |
| **QA-2** | Scalability | System Operator | Adds a 4th branch with ~1M monthly rows | Ingest & Report engine | Peak monthly processing load | Ingests and generates combined chain report | Run time increases by $\le 35\%$ over 3-branch baseline | (M,M) |
| **QA-3** | Availability | Network link | REP link fails for 2h; file delayed past 23:00 | Ingest job | Degraded branch connectivity | Generates partial report for available branches by 07:00 | Partial report published by 07:00; full report updated $\le 15\text{ min}$ after REP arrival | (H,M) |
| **QA-4** | Modifiability | Developer | Request to add new export renderer (e.g., JSON) | Render module | Development & testing | Implement new `ReportRenderer` interface | $\le 2$ person-days of effort; $\le 100$ lines of new code | (H,L) |
| **QA-5** | Security | Branch Manager | Manager attempts to view data from another branch | Web report UI | Normal operation | System denies access and logs unauthorized attempt | 100% of unauthorized requests blocked; log entry written instantly | (H,M) |
| **QA-6** | Testability | Developer | Runs test suite on core calculation engine | Test suite / CI | Build & execution environment | Executes full suite including monetary rounding checks | Test execution time $\le 30\text{ s}$; 100% pass rate | (M,L) |

## Rank Justifications
- **QA-1 (H,H):** High business importance as morning report deadlines are strict; high architectural difficulty to parse and calculate ~3M records with exact `BigDecimal` rounding within 60 seconds.
- **QA-2 (M,M):** Moderate business importance since store expansion occurs gradually; moderate architectural effort to ensure parallel stream/batch scaling without bottlenecking.
- **QA-3 (H,M):** High business importance so network issues do not stall head-office operations; moderate difficulty handling partial aggregations and state updates cleanly.
- **QA-4 (H,L):** High business importance for future report format flexibility; low difficulty if the Strategy design pattern is properly abstracted.
- **QA-5 (H,M):** High business importance to enforce branch data privacy; moderate difficulty setting up row-level or branch-filtered authorization rules.
- **QA-6 (M,L):** Moderate business importance to maintain code stability; low difficulty utilizing standard JUnit 6 unit testing frameworks.

## Assumptions
- **A1:** The production environment is equipped with an 8-core CPU, 16GB RAM, and SSD storage.
- **A2:** Monthly data volume spans across 3 branches totaling approximately 3,000,000 transaction rows.
- **A3:** CSV input files follow UTF-8 encoding without field quotes.

## Utility Tree
```mermaid
flowchart LR
  U((Utility)) --> P[Performance]
  U --> SC[Scalability]
  U --> A[Availability]
  U --> M[Modifiability]
  U --> S[Security]
  U --> T[Testability]

  P --> QA1["QA-1 (H,H): 3M rows in <= 60 s"]
  SC --> QA2["QA-2 (M,M): 4th branch adds <= 35% time"]
  A --> QA3["QA-3 (H,M): Partial report out by 07:00 AM"]
  M --> QA4["QA-4 (H,L): New renderer in <= 2 days"]
  S --> QA5["QA-5 (H,M): Manager sees own branch only"]
  T --> QA6["QA-6 (M,L): Core tests run in <= 30 s"]