# Storefront Spec

## 1. Overview
Specifications for the customer-facing storefront presentation, including product tiles, navigation, and call-to-action elements.

## 2. Requirements

### 2.1. Product Tile Call-to-Action Text
The call-to-action (CTA) text for the link on product tiles that leads to the product detail page must be "Shop Now" (or its localized equivalent).

#### Scenario: Verify CTA text in English
- **WHEN** the user is on the home page or search result page in the English locale (en-US)
- **THEN** the product tile buttons should display the text "Shop Now"

#### Scenario: Verify CTA text in German
- **WHEN** the user is on the home page or search result page in the German locale (de-DE)
- **THEN** the product tile buttons should display the text "Jetzt shoppen"
