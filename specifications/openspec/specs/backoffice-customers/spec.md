# Backoffice Customers

## Purpose
TBD: Main specification for the backoffice customers capability.

## Requirements

### Requirement: Premium Customer Detail Layout
The customer detail screen SHALL use a premium, modern card-based layout consistent with the overall backoffice design system, specifically drawing inspiration from the visual language established in the customer dashboard.

#### Scenario: Visual presentation of the detail layout
- **WHEN** the user navigates to a customer detail page (e.g., `/backoffice/customers/{id}`)
- **THEN** the profile, addresses, and credit cards are displayed in distinct `bo-card` containers with appropriate shadows and borders.
- **AND** the layout utilizes clear visual hierarchy via typography and spacing.
- **AND** secondary actions (like delete) on list items (addresses, credit cards) are grouped into a three-dot dropdown menu.
- **AND** the page action toolbar contains the "Back to List" button.
