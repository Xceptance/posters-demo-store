## MODIFIED Requirements

### Requirement: Default Roles SHALL Be Re-Seeded with Updated Module IDs

The default roles SHALL be updated to use the new top-level module IDs. The `AdminDataLoader` SHALL detect and re-seed `role_modules` entries if they contain outdated module IDs (i.e. if the `security` module ID is absent from all role entries).

#### Default Role Definitions (updated)

| Role | Module IDs |
|---|---|
| Admin | `dashboard`, `security`, `catalog`, `customers`, `orders` |
| Super User | `dashboard`, `catalog`, `customers`, `orders` |
| Catalog User | `dashboard`, `catalog` |
| Order User | `dashboard`, `orders` |

#### Scenario: Roles seeded with new module IDs on fresh start

- **WHEN** the application starts for the first time
- **AND** the `roles` table is empty
- **THEN** roles are seeded with the module IDs from the table above
- **AND** `security` is only included in the Admin role

#### Scenario: Role_modules re-seeded on upgrade from old module IDs

- **WHEN** the application starts
- **AND** the `roles` table has entries
- **AND** no role contains the `security` module ID
- **THEN** all `role_modules` entries are cleared and re-seeded with updated IDs
- **AND** the roles themselves (name, description, builtIn) are preserved
