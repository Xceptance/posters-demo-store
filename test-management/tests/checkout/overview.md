# Checkout Domain Test Cases

This directory contains manual functional test cases for the Checkout domain, covering both guest and registered user checkout flows.

## Test Case Index

| Test ID | Title | Priority | Suite | Objective |
| :--- | :--- | :--- | :--- | :--- |
| [TC_CHK_001](./TC_CHK_001.md) | Guest Checkout (Happy Path) | 🔴 Critical | Smoke, Regression, Full | Verify a guest user can successfully place an order. |
| [TC_CHK_002](./TC_CHK_002.md) | Registered User Checkout | 🔴 Critical | Regression, Full | Verify a logged-in user can check out using saved details. |
| [TC_CHK_003](./TC_CHK_003.md) | Cart Modification During Checkout | 🟡 Medium | Regression, Full | Verify total recalculates correctly when quantity is changed mid-checkout. |
