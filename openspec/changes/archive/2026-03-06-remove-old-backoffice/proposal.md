## Why

The project carries ~27MB of dead backoffice assets (AdminLTE, jQuery, DataTables, FontAwesome 4, pdfmake, etc.) across three directories. No Java controller, no template, and no JavaScript in the storefront references them. They inflate the repository, slow clones, and create confusion about what's active code vs. legacy.

## What Changes

- **Remove** `src/main/resources/static/js/backoffice/` (1 legacy JS file)
- **Remove** `src/main/resources/static/js/backofficeNew/` (AdminLTE + 16 plugin dirs)
- **Remove** `src/main/resources/static/css/backofficeNew/` (AdminLTE CSS + webfonts)
- Total: 268 files, ~27MB of dead weight

## Capabilities

### New Capabilities

- `backoffice-cleanup`: Removal of all legacy backoffice static assets

### Modified Capabilities

<!-- None — this is a pure deletion with no behavior changes -->

## Impact

- **Repository size**: ~27MB reduction
- **Build artifacts**: Smaller WAR/JAR since Spring Boot packages static resources
- **No runtime impact**: Zero references from Java code or templates
