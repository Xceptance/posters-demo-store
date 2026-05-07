## ADDED Requirements

### Requirement: Customer List Search and Pagination

The customers module SHALL display a paginated list of customers with free-text search and sorting capabilities.

#### Scenario: Search execution
- **WHEN** an admin enters a query and presses Enter
- **THEN** the system returns matching customers based on exact, partial email, name, or customer number
- **AND** an empty query returns all customers

#### Scenario: Empty search results
- **WHEN** a search returns no matching customers
- **THEN** the table is not rendered
- **AND** a "No customers found for your search." message is shown

#### Scenario: Pagination and sorting
- **WHEN** an admin views the customer list
- **THEN** they can select page sizes of 25, 50, 100, or 200
- **AND** they can sort by clicking column headers
