## ADDED Requirements

### Requirement: WebMCP Search Discoverability
The system SHALL expose the site's search functionality to AI agents via standard WebMCP declarations (e.g., a static WebMCP manifest or semantic HTML attributes).

#### Scenario: Agent discovers search
- **WHEN** an AI agent inspects the website context
- **THEN** it can identify the search tool, its endpoint, and required parameters (like the search query).

### Requirement: WebMCP Integration Testing
The system SHALL include an automated test to verify the presence of the WebMCP integration.

#### Scenario: Validate WebMCP metadata
- **WHEN** the test suite is executed
- **THEN** it confirms that the WebMCP manifest or metadata is correctly served on the site.
