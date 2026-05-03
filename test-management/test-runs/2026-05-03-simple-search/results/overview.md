# Search — Test Case Overview

This directory contains the manual functional test cases for the **Search** domain of the Posters Demo Store. These tests cover the storefront full-text search functionality powered by Apache Lucene, including single-term and multi-word queries, localization, prefix matching, result correctness, and boundary/stress scenarios.

> [!NOTE]
> Search Suggestions (type-ahead dropdown) are tracked separately in the [Test Backlog](../TEST-BACKLOG.md) and will be authored as their own test domain.

| Test ID | Title | Priority | Suite | Objective |
| :--- | :--- | :--- | :--- | :--- |
| [TC_SRC_001](./TC_SRC_001.md) | Simple Search — Single Term (Happy Path) | 🔴 Critical | 🚀 Smoke, 🔄 Regression, 🧪 Full | Verify single-term search returns relevant, correctly localized results across all locales. |
| [TC_SRC_002](./TC_SRC_002.md) | Search — No Results | 🟠 High | 🔄 Regression, 🧪 Full | Verify the "No products found" empty state for unmatched queries. |
| [TC_SRC_003](./TC_SRC_003.md) | Search — Whitespace & Empty Query Handling | 🟡 Medium | 🔄 Regression, 🧪 Full | Verify graceful handling of empty, whitespace-only, and atypical whitespace input. |
| [TC_SRC_004](./TC_SRC_004.md) | Multi-Word Search (AND Behavior) | 🔴 Critical | 🔄 Regression, 🧪 Full | Verify AND default operator, reversed terms, 3+ terms, and full description paste. |
| [TC_SRC_005](./TC_SRC_005.md) | Partial / Prefix Search | 🟠 High | 🔄 Regression, 🧪 Full | Verify prefix/wildcard matching returns relevant products for incomplete words. |
| [TC_SRC_006](./TC_SRC_006.md) | Case-Insensitive Search | 🟠 High | 🔄 Regression, 🧪 Full | Verify identical results regardless of letter case, including diacritics. |
| [TC_SRC_007](./TC_SRC_007.md) | Search Result Correctness & Navigation | 🟠 High | 🚀 Smoke, 🔄 Regression, 🧪 Full | Verify result tiles are genuine matches with valid data and PDP navigation is consistent. |
| [TC_SRC_008](./TC_SRC_008.md) | Search Result Count & Heading | 🟡 Medium | 🔄 Regression, 🧪 Full | Verify heading displays correct search term, count, and singular/plural grammar. |
| [TC_SRC_009](./TC_SRC_009.md) | Search — Input Boundary & Stress | 🟡 Medium | 🧪 Full | Verify graceful handling of long inputs, emoji, and injection attempts. |
