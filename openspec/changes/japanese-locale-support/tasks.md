# Tasks: Japanese Locale Support

## Chunk 1: Font Setup
- [x] Download Noto Sans JP woff2 files (variable font) to `static/fonts/noto-sans-jp/`
- [x] Add `@font-face` declarations for Noto Sans JP in `style_reworked.css`
- [x] Add conditional font stylesheet loading in `layout/default.html` for `ja-JP` locale
- [x] Update CSS font-family stacks to include Noto Sans JP as CJK fallback

## Chunk 2: Config & Plumbing
- [x] Add `ja-JP` to `application.yml` languages list
- [x] Add `ja-JP` currency (¥) and unit (cm) mapping in `CommonDataInterceptor`
- [x] Add `<locale code="ja-JP"/>` to `catalog-import.xml` locales section
- [x] Add `ja-JP` locale-ref to site definitions in `catalog-import.xml`

## Chunk 3: UI Message Bundle
- [x] Create `messages_ja_JP.properties` with Japanese translations of all UI keys

## Chunk 4: Catalog — Categories & Shipping
- [x] Add `<name xml:lang="ja-JP">` to all 4 top categories in `catalog-import.xml`
- [x] Add `<name xml:lang="ja-JP">` to all 13 sub-categories in `catalog-import.xml`
- [x] Add `<name xml:lang="ja-JP">` to shipping methods in `catalog-import.xml`

## Chunk 5: Catalog — Products
- [x] Add `<name xml:lang="ja-JP">` and `<description xml:lang="ja-JP">` to all 125 products

## Chunk 6: Form Validation
- [x] Update name regex in `application.yml` to accept CJK characters
