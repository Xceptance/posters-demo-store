# Posters Demo Store - Performance Testing Backlog

This file serves as the holding ground for performance testing concepts and scenarios. These items require dedicated tooling (e.g., XLT, JMeter, Gatling) or automated frameworks and are out of scope for the manual functional test suite.

## 🔎 Search Performance

- **Search under concurrent load:** Verify Lucene full-text search response times remain acceptable (< 200ms p95) under concurrent user load across all locales.
- **Search index rebuild performance:** Measure time to rebuild the Lucene index from a full catalog re-import. Validate that index rebuild does not block search queries.
- **Prefix query performance on large catalogs:** Evaluate the impact of the appended wildcard (`*`) on query performance as the catalog grows.

## 🛒 Checkout Performance

- *(Empty — add scenarios as needed)*

## 📦 Catalog Browsing Performance

- *(Empty — add scenarios as needed)*

## 🌐 Localization Performance

- **Multi-locale switching latency:** Measure response time overhead when switching between locales, particularly for catalog pages with heavy text resolution via `LocalizedTextService`.
