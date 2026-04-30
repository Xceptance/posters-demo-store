# Test Card Numbers

Luhn-valid card numbers for each supported vendor, for demo/testing use.
These numbers are **NOT real credit cards** — they are test numbers that pass the Luhn checksum.

| Vendor           | Card Number         | CVV  | Expiry |
|------------------|---------------------|------|--------|
| Visa             | 4111 1111 1111 1111 | 123  | 12/30  |
| Mastercard       | 5500 0000 0000 0004 | 123  | 12/30  |
| American Express | 3400 000000 00009   | 1234 | 12/30  |
| UnionPay         | 6200 0000 0000 0003 | 123  | 12/30  |
| JCB              | 3530 1113 3330 0000 | 123  | 12/30  |
| Discover         | 6011 0000 0000 0004 | 123  | 12/30  |
| Diners Club      | 3056 930902 5904    | 123  | 12/30  |
| Maestro          | 5018 0000 0009      | 123  | 12/30  |

## Notes

- The Amex card is 15 digits and requires a 4-digit CVV.
- All other vendors use a 3-digit CVV.
- Expiry date `12/30` is far enough in the future for testing.
- These numbers are the standard test numbers used by payment processors (Stripe, Braintree, etc.).
