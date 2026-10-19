# ADR-0001: Architecture Style for Release 1

**Status:** Accepted
**Date:** 2026-10-19
**Deciders:** Angkor Mart Sales Report Development Team

## Context

Angkor Mart has three branches: Phnom Penh (PNH), Siem Reap (REP), and Battambang (BTB). The system must read daily CSV sales files, validate transactions, calculate monthly revenue, and generate reports.

The architecture must support reliable processing, good performance, easy testing, and future changes to report formats.

## Decision

We choose a **modular monolith with batch processing** for Release 1.

The application will have separate modules or packages for data ingestion, domain models, aggregation, and report rendering. These components run together in one Java application.

## Alternatives Considered

1. **Pipeline with independent workers:** Allows parallel processing but increases coordination, communication, and debugging complexity.
2. **Event-driven microservices:** Supports independent deployment and scaling but requires messaging infrastructure, monitoring, and distributed failure handling.

## Consequences

**Advantages:**

* Simple to develop, test, and deploy with Java and Maven.
* Low infrastructure and operational costs.
* Clear module boundaries support maintainability.
* Batch processing fits monthly reporting requirements.

**Disadvantages:**

* Modules cannot be deployed independently.
* Scaling is limited compared with distributed architectures.
* Large datasets may eventually require parallel processing.

## Revisit Trigger

Reconsider this decision if monthly data volume exceeds the system's performance target, reporting requires near-real-time updates, or teams need independent deployment of components.
