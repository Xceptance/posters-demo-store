## Why
The Posters Demo Store currently supports three Western locales (en-US, en-GB, de-DE) and one Scandinavian locale (sv-SE). Adding Japanese (ja-JP) demonstrates the store's ability to handle a non-Western language with fundamentally different character sets (Kanji, Hiragana, Katakana), font requirements (CJK web fonts), and cultural conventions. This makes the store a more compelling demo for internationalization testing, load testing with varied character encodings, and showcasing real-world i18n challenges.

## What Changes
- Add `ja-JP` as a supported locale across config, catalog data, and UI message bundles.
- Self-host Noto Sans JP font (static woff2 weights: 400, 600, 700) and conditionally load it only for Japanese locale visitors.
- Create `messages_ja_JP.properties` with Japanese translations of all ~393 UI chrome keys.
- Add Japanese translations for all catalog data: 4 top categories, 13 sub-categories, 125 products (names + descriptions), and 2 shipping methods.
- Update name validation regex to accept CJK characters.
- Add currency (¥) and unit (cm) mapping for the ja-JP locale.

## Capabilities

### New Capabilities
- `japanese-locale`: Full ja-JP locale support including UI translations, catalog data translations, CJK font loading, and locale-appropriate currency/unit display.

### Modified Capabilities
- `i18n-system`: Extended to handle CJK character sets, conditional font loading based on active locale, and broadened form validation for non-Latin scripts.

## Impact
- **Fonts/Assets**: ~4.5MB of Noto Sans JP woff2 files added to `static/fonts/noto-sans-jp/`. These are loaded conditionally — zero impact on non-Japanese locale page weight.
- **CSS**: Updated font-family fallback stack to include Noto Sans JP for CJK coverage. New `@font-face` declarations in a separate or inline stylesheet.
- **Config**: `application.yml` languages list extended. `CommonDataInterceptor` gets a ja-JP currency/unit branch.
- **Catalog XML**: All `<name>` and `<description>` elements gain `xml:lang="ja-JP"` variants. Locales section gains `ja-JP` entry.
- **Message Bundle**: New `messages_ja_JP.properties` file (~393 keys). AI-generated translations are acceptable for this demo context.
- **Form Validation**: Name regex broadened to accept Unicode letter ranges including CJK.
- **Address Format**: Intentionally kept as-is (Western format) for now — a deliberate known gap for future work.

## Out of Scope
- Japanese address format (prefecture → city → block ordering)
- Right-to-left or vertical text layout
- Yen-specific pricing / tax calculation changes
- Backoffice/admin panel translations
