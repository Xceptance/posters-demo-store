## ADDED Requirements

### Requirement: Customer Search Indexing

The system SHALL maintain a Lucene search index for customers, updating it asynchronously when customers are created or modified.

#### Scenario: Seeded customer indexing
- **WHEN** the 10 test customers are seeded via XML import on startup
- **THEN** the async indexer is triggered to make them immediately searchable
