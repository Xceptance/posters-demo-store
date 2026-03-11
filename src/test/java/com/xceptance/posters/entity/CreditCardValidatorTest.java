package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD tests for CreditCardValidator — Luhn, vendor-specific length,
 * expiry range, and CVV length validation.
 */
class CreditCardValidatorTest
{
    private final CreditCardValidator validator = new CreditCardValidator();

    // ---- Luhn checksum ----

    @ParameterizedTest
    @CsvSource({
        "4111111111111111, true",   // Visa test card
        "5500000000000004, true",   // Mastercard test card
        "340000000000009,  true",   // Amex test card
        "6011000000000004, true",   // Discover test card
        "3528000000000007, true",   // JCB test card
        "4111111111111112, false",  // invalid checksum
        "0000000000000000, true",   // all zeros passes Luhn
        "1234567890123456, false",  // random digits
    })
    void luhnValidation(String number, boolean expected)
    {
        assertThat(validator.isLuhnValid(number)).isEqualTo(expected);
    }

    @Test
    void luhnRejectsNullAndEmpty()
    {
        assertThat(validator.isLuhnValid(null)).isFalse();
        assertThat(validator.isLuhnValid("")).isFalse();
        assertThat(validator.isLuhnValid("  ")).isFalse();
    }

    @Test
    void luhnStripsNonDigits()
    {
        // Same valid number with spaces/dashes
        assertThat(validator.isLuhnValid("4111 1111 1111 1111")).isTrue();
        assertThat(validator.isLuhnValid("4111-1111-1111-1111")).isTrue();
    }

    // ---- Vendor-specific length ----

    @Test
    void validLengthForVendor()
    {
        // Visa: 13, 16, or 19 digits
        assertThat(validator.isValidLength("4111111111111111", CreditCardVendor.VISA)).isTrue();

        // Amex: 15 digits
        assertThat(validator.isValidLength("340000000000009", CreditCardVendor.AMEX)).isTrue();
    }

    @Test
    void invalidLengthForVendor()
    {
        // 15 digits is not valid for Visa
        assertThat(validator.isValidLength("411111111111111", CreditCardVendor.VISA)).isFalse();

        // 16 digits is not valid for Amex
        assertThat(validator.isValidLength("3400000000000090", CreditCardVendor.AMEX)).isFalse();
    }

    // ---- Expiry date ----

    @Test
    void validFutureExpiry()
    {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear() % 100; // 2-digit year

        // Current month/year is valid
        assertThat(validator.isExpiryValid(currentMonth, currentYear)).isTrue();

        // One year in the future
        assertThat(validator.isExpiryValid(currentMonth, currentYear + 1)).isTrue();
    }

    @Test
    void expiredCard()
    {
        LocalDate now = LocalDate.now();
        int pastYear = (now.getYear() - 1) % 100;

        assertThat(validator.isExpiryValid(1, pastYear)).isFalse();
    }

    @Test
    void invalidMonthValues()
    {
        int futureYear = (LocalDate.now().getYear() + 1) % 100;

        assertThat(validator.isExpiryValid(0, futureYear)).isFalse();
        assertThat(validator.isExpiryValid(13, futureYear)).isFalse();
        assertThat(validator.isExpiryValid(-1, futureYear)).isFalse();
    }

    @Test
    void expiryTooFarInFuture()
    {
        int tooFar = (LocalDate.now().getYear() + 21) % 100;

        assertThat(validator.isExpiryValid(6, tooFar)).isFalse();
    }

    @Test
    void expiryMaxAllowed()
    {
        int maxYear = (LocalDate.now().getYear() + 20) % 100;

        assertThat(validator.isExpiryValid(12, maxYear)).isTrue();
    }

    // ---- CVV length ----

    @Test
    void cvvValidForVendor()
    {
        assertThat(validator.isCvvValid("123", CreditCardVendor.VISA)).isTrue();
        assertThat(validator.isCvvValid("1234", CreditCardVendor.AMEX)).isTrue();
    }

    @Test
    void cvvInvalidForVendor()
    {
        assertThat(validator.isCvvValid("1234", CreditCardVendor.VISA)).isFalse();
        assertThat(validator.isCvvValid("123", CreditCardVendor.AMEX)).isFalse();
    }

    @Test
    void cvvRejectsNullEmptyNonDigits()
    {
        assertThat(validator.isCvvValid(null, CreditCardVendor.VISA)).isFalse();
        assertThat(validator.isCvvValid("", CreditCardVendor.VISA)).isFalse();
        assertThat(validator.isCvvValid("abc", CreditCardVendor.VISA)).isFalse();
        assertThat(validator.isCvvValid("12a", CreditCardVendor.VISA)).isFalse();
    }

    @Test
    void cvvWithNullVendorDefaultsToThree()
    {
        assertThat(validator.isCvvValid("123", null)).isTrue();
        assertThat(validator.isCvvValid("1234", null)).isFalse();
    }
}
