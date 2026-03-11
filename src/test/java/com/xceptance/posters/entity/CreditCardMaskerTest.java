package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD tests for CreditCardMasker — PAN masking (first 6 + last 4 visible,
 * middle replaced with asterisks).
 */
class CreditCardMaskerTest
{
    // -- Standard 16-digit cards --
    @Test
    void mask16DigitCard()
    {
        // 16 digits: first 6 + last 4 visible = 10, masked = 6
        assertThat(CreditCardMasker.mask("4111111111111111")).isEqualTo("411111******1111");
    }

    @Test
    void mask16DigitMastercard()
    {
        assertThat(CreditCardMasker.mask("5123456789013456")).isEqualTo("512345******3456");
    }

    // -- 15-digit Amex --
    @Test
    void mask15DigitAmex()
    {
        // 15 digits: first 6 + last 4 = 10, masked = 5
        assertThat(CreditCardMasker.mask("340000000000009")).isEqualTo("340000*****0009");
    }

    // -- 14-digit Diners Club --
    @Test
    void mask14DigitDinersClub()
    {
        // 14 digits: first 6 + last 4 = 10, masked = 4
        assertThat(CreditCardMasker.mask("30000000000000")).isEqualTo("300000****0000");
    }

    // -- 19-digit cards (some UnionPay, JCB, Discover) --
    @Test
    void mask19DigitCard()
    {
        // 19 digits: first 6 + last 4 = 10, masked = 9
        assertThat(CreditCardMasker.mask("6200000000000000001")).isEqualTo("620000*********0001");
    }

    // -- 13-digit Visa (old style) --
    @Test
    void mask13DigitCard()
    {
        // 13 digits: first 6 + last 4 = 10, masked = 3
        assertThat(CreditCardMasker.mask("4111111111111")).isEqualTo("411111***1111");
    }

    // -- Edge cases: too short to mask --
    @Test
    void maskShortNumber()
    {
        // 10 or fewer digits: nothing to mask (first 6 + last 4 = the whole number)
        assertThat(CreditCardMasker.mask("1234567890")).isEqualTo("1234567890");
    }

    // -- Input with spaces/dashes (should be stripped) --
    @Test
    void maskStripsNonDigits()
    {
        assertThat(CreditCardMasker.mask("4111 1111 1111 1111")).isEqualTo("411111******1111");
        assertThat(CreditCardMasker.mask("4111-1111-1111-1111")).isEqualTo("411111******1111");
    }

    // -- Null / empty --
    @Test
    void maskNullAndEmpty()
    {
        assertThat(CreditCardMasker.mask(null)).isEqualTo("");
        assertThat(CreditCardMasker.mask("")).isEqualTo("");
        assertThat(CreditCardMasker.mask("  ")).isEqualTo("");
    }

    // -- 12-digit Maestro --
    @Test
    void mask12DigitCard()
    {
        // 12 digits: first 6 + last 4 = 10, masked = 2
        assertThat(CreditCardMasker.mask("501800000000")).isEqualTo("501800**0000");
    }
}
