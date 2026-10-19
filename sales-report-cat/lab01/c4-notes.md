# C4 Diagram Notes — Angkor Mart

## C4 Level 1: Context Diagram

The context diagram shows the people and external systems interacting with the Angkor Mart Monthly Sales Report System. It deliberately excludes internal applications, databases, Java classes, and implementation details. An open question for the client is whether the finance director should receive only an e-mail notification or also a report attachment.

## C4 Level 2: Container Diagram

The container diagram shows the major applications and data stores planned for release 1. It deliberately excludes internal Java classes, interfaces, algorithms, and package-level details.

The **Report Batch** supports **QA-1 (Performance)** by providing a centralized batch process for approximately 3 million sales rows. The **Ingest Job and Landing Zone** support **QA-3 (Availability)** by separating file arrival from report processing, allowing delayed files to be handled without losing available data. The **Web Report Page** must also support **QA-5 (Security)** by restricting branch managers to their own branch reports.

An open question for the client is whether head office may publish a partial chain report when one branch's CSV file arrives late.

## Release 1 assumptions

- Branches upload daily CSV files using SFTP.
- Head office uses a modular monolith batch application for processing.
- Report files are stored in a head-office file system.
- JavaFX and web interfaces are planned for later labs.
- Authentication and authorization will be implemented before branch managers receive access.
