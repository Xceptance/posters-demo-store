# backoffice-cleanup Specification

## Purpose
TBD - created by archiving change remove-old-backoffice. Update Purpose after archive.
## Requirements
### Requirement: Legacy Backoffice Assets MUST Be Removed

The project MUST NOT contain any legacy backoffice static assets. The storefront MUST continue to function without them.

#### Scenario: Storefront loads without backoffice assets

- **WHEN** a user navigates to any storefront page
- **THEN** the page loads successfully
- **AND** no 404 errors appear for missing static resources

#### Scenario: No backoffice directories remain

- **WHEN** inspecting the static resources directory
- **THEN** no `backoffice` or `backofficeNew` directories exist under `static/js/` or `static/css/`

