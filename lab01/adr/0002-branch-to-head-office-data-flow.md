# ADR-0002: Branch-to-Head-Office Data Flow

**Status:** Proposed
**Date:** 2026-10-19
**Deciders:** Angkor Mart Sales Report Development Team

## Context

Angkor Mart's three branches generate daily CSV sales files. Head office needs these files to prepare monthly reports.

The data transfer process must handle network failures, missing files, duplicate uploads, and late submissions without losing sales information.

## Decision

We propose **daily CSV uploads from branches to head office using SFTP or authenticated HTTPS**, followed by scheduled batch processing.

Each branch uploads its daily file to a central location. Head office validates incoming files before including them in the monthly report.

## Alternatives Considered

1. **Head-office pull:** Head office downloads files directly from branch systems. This reduces branch upload responsibilities but depends on branch availability and network access.
2. **Message broker per transaction:** Branches send each receipt as an event. This enables faster updates but requires additional infrastructure and reliable message delivery.

## Failure and Recovery Policy

* **Missing files:** Wait until the agreed reporting cutoff. Generate a report marked *incomplete* if required files remain missing, and notify the operator.
* **Duplicate uploads:** Use the branch, filename, and SHA-256 checksum to identify repeated uploads and prevent double counting.
* **Late files:** Validate and merge late files into a new version of the monthly report, preserving the previous version for audit purposes.
* **Transfer failures:** Retry uploads and log failures for investigation.
* **Invalid rows:** Reject invalid transactions, record their file and line number, and continue processing valid transactions.

## Consequences

**Advantages:**

* Simple integration with the existing CSV-based system.
* Reliable retry and recovery are possible.
* Central validation improves data quality.
* Lower infrastructure cost than a message broker.

**Disadvantages:**

* Reports are not updated in real time.
* Missing uploads may delay complete reports.
* Secure transfer and file tracking must be implemented.

## Revisit Trigger

Reconsider this approach if Angkor Mart requires real-time sales dashboards, increases its branch count substantially, or experiences frequent delays and failures in daily file transfers.
