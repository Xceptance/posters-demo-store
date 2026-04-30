# Japanese Locale Support (ja-JP)

The `ja-JP` locale was integrated into the Posters Demo Store to demonstrate internationalization (i18n) for a non-Western language, enabling testing capabilities involving complex character encodings, dynamic fonts, and automated indexing.

## Features Implemented

1. **UI Message Bundles**: Fully translated the global UI chrome across all ~393 properties in `messages_ja_JP.properties`.
2. **Catalog Translation**: Injected localized Japanese `<name>` and `<description>` tags to the monolithic `catalog-import.xml` data source, fully mapping 125 internal products, 4 root categories, 13 sub-categories, and multiple shipping tiers.
3. **Typography Injection**: The **Noto Sans JP** variable web font (`woff2`) is self-hosted under `static/fonts/noto-sans-jp/`. To preserve page load latency, this font payload is dynamically imported via `<style>` blocks in `layout/default.html` exclusively when traversing the `ja-JP` URL locale segment.
4. **Resilient Form Validation**: Safely broadened the `posters.regex.name` validation property inside `application.yml` by injecting exact CJK Unicode blocks (`\u3040-\u30FF` and `\u4E00-\u9FAF`) to tolerate non-Latin characters in checkout form constraints.

## Application Plumbing

*   The locale was registered inside `posters.languages`, immediately unlocking dynamic locale-switchers in the UI drop-downs.
*   **Currency & Unit Mapping**: The `CommonDataInterceptor` natively binds the `¥` currency symbol and implements `cm` measurement bindings specifically when encountering the `ja-JP` path variable.
*   **Search Tokenization**: Since Lucene indexing breaks on CJK strings by default, the `lucene-analysis-kuromoji` analyzer dependency was patched into Maven. The `DataImportService` and `LuceneSearchService` trigger the `JapaneseAnalyzer` indexing pipeline upon startup seeding for the `ja-JP` catalog map.

## Strategic Omissions (Out of Scope for Demo)

*   Regional address flows (e.g. prefecture → city → block ordering) inherently remain structured as Western inputs.
*   Yen-exact tax rate calculations.
*   Backend administrative panels remain untranslated.
