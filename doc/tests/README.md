# Posters Demo Store - Manual Test Suite

This directory contains the manual functional test suite for the Posters Demo Store. Tests are grouped by business domain (e.g., checkout, catalog, account).

## Directory Structure

- `search/`: Exact matches, broad terms, and result handling.
- `catalog/`: Category browsing, product detail views, and pagination.
- `cart/`: Adding, removing, and updating item quantities.
- `checkout/`: Guest and registered user checkout flows.
- `account/`: Registration, login, and profile management.
- `images/`: Put all screenshot/reference images in this folder to keep your test cases clean. 

## Test Case Format

Each test case is a Markdown file following a strict template:
1. **Metadata & Tags** (`## Metadata`) - Priority, Status, Execution Targets (Locales, Viewports), and Tags.
2. **Tester Notes** - Special hints and visual cues for testers.
3. **Preconditions** - State requirements before beginning.
4. **Test Data** - Data requirements, specifically parametrized by language/locale.
5. **Steps** - The execution table.
6. **Postconditions** - What to verify in the DB or session state after execution.

## Embedding Screenshots

When writing a new test case, if you need to provide a visual hint, place the image in the `doc/tests/images/` directory and link it natively in Markdown:
```markdown
![Hint Description](../images/login_error.png)
```
