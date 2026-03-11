package com.xceptance.posters.entity;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

/**
 * Credit card validation service — Luhn checksum, vendor-specific length,
 * expiry date range, and CVV length.
 *
 * <p>Mirrors the client-side JS validation to ensure server-side safety net.
 */
@Service
public class CreditCardValidator
{
    /**
     * Validates a card number using the Luhn algorithm (mod-10 checksum).
     */
    public boolean isLuhnValid(String cardNumber)
    {
        if (cardNumber == null)
        {
            return false;
        }

        String digits = cardNumber.replaceAll("\\D", "");
        if (digits.isEmpty())
        {
            return false;
        }

        int sum = 0;
        boolean doubleDigit = false;

        for (int i = digits.length() - 1; i >= 0; i--)
        {
            int n = digits.charAt(i) - '0';

            if (doubleDigit)
            {
                n *= 2;
                if (n > 9)
                {
                    n -= 9;
                }
            }

            sum += n;
            doubleDigit = !doubleDigit;
        }

        return sum % 10 == 0;
    }

    /**
     * Checks if the card number length is valid for the given vendor.
     */
    public boolean isValidLength(String cardNumber, CreditCardVendor vendor)
    {
        if (cardNumber == null || vendor == null)
        {
            return false;
        }

        String digits = cardNumber.replaceAll("\\D", "");
        return vendor.isValidLength(digits.length());
    }

    /**
     * Validates the expiry date (MM/YY format as separate ints).
     * Month must be 1–12. Expiry must not be in the past.
     * Maximum accepted year is current year + 20.
     *
     * @param month 1-indexed month (1–12)
     * @param twoDigitYear 2-digit year (e.g., 26 for 2026)
     */
    public boolean isExpiryValid(int month, int twoDigitYear)
    {
        if (month < 1 || month > 12)
        {
            return false;
        }

        // Convert 2-digit year to 4-digit
        int fullYear = 2000 + twoDigitYear;
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // Check not in the past
        if (fullYear < currentYear || (fullYear == currentYear && month < currentMonth))
        {
            return false;
        }

        // Check not too far in the future (max current year + 20)
        if (fullYear > currentYear + 20)
        {
            return false;
        }

        return true;
    }

    /**
     * Validates CVV length for the given vendor.
     * Amex requires 4 digits, all others require 3.
     * If vendor is null, defaults to 3.
     */
    public boolean isCvvValid(String cvv, CreditCardVendor vendor)
    {
        if (cvv == null || cvv.isEmpty())
        {
            return false;
        }

        // Must be all digits
        if (!cvv.matches("\\d+"))
        {
            return false;
        }

        int expectedLength = vendor != null ? vendor.getCvvLength() : 3;
        return cvv.length() == expectedLength;
    }
}
