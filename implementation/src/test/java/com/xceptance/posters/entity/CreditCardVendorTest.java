package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD tests for CreditCardVendor BIN prefix detection.
 */
class CreditCardVendorTest
{
    // -- Visa --
    @Test
    void detectVisa()
    {
        assertThat(CreditCardVendor.detect("4111111111111111")).isEqualTo(CreditCardVendor.VISA);
        assertThat(CreditCardVendor.detect("4")).isEqualTo(CreditCardVendor.VISA);
        assertThat(CreditCardVendor.detect("4012888888881881")).isEqualTo(CreditCardVendor.VISA);
    }

    // -- Mastercard --
    @ParameterizedTest
    @CsvSource({
        "5100000000000000", "5200000000000000", "5300000000000000",
        "5400000000000000", "5500000000000000",
        "2221000000000000", "2720000000000000", "2500000000000000"
    })
    void detectMastercard(String number)
    {
        assertThat(CreditCardVendor.detect(number)).isEqualTo(CreditCardVendor.MASTERCARD);
    }

    // -- American Express --
    @Test
    void detectAmex()
    {
        assertThat(CreditCardVendor.detect("340000000000000")).isEqualTo(CreditCardVendor.AMEX);
        assertThat(CreditCardVendor.detect("370000000000000")).isEqualTo(CreditCardVendor.AMEX);
        assertThat(CreditCardVendor.detect("34")).isEqualTo(CreditCardVendor.AMEX);
        assertThat(CreditCardVendor.detect("37")).isEqualTo(CreditCardVendor.AMEX);
    }

    // -- UnionPay --
    @Test
    void detectUnionPay()
    {
        assertThat(CreditCardVendor.detect("6200000000000000")).isEqualTo(CreditCardVendor.UNIONPAY);
        assertThat(CreditCardVendor.detect("62")).isEqualTo(CreditCardVendor.UNIONPAY);
    }

    // -- JCB --
    @Test
    void detectJcb()
    {
        assertThat(CreditCardVendor.detect("3528000000000000")).isEqualTo(CreditCardVendor.JCB);
        assertThat(CreditCardVendor.detect("3589000000000000")).isEqualTo(CreditCardVendor.JCB);
    }

    // -- Discover --
    @Test
    void detectDiscover()
    {
        assertThat(CreditCardVendor.detect("6011000000000000")).isEqualTo(CreditCardVendor.DISCOVER);
        assertThat(CreditCardVendor.detect("6440000000000000")).isEqualTo(CreditCardVendor.DISCOVER);
        assertThat(CreditCardVendor.detect("6500000000000000")).isEqualTo(CreditCardVendor.DISCOVER);
    }

    // -- Diners Club --
    @Test
    void detectDinersClub()
    {
        assertThat(CreditCardVendor.detect("30000000000000")).isEqualTo(CreditCardVendor.DINERS_CLUB);
        assertThat(CreditCardVendor.detect("30500000000000")).isEqualTo(CreditCardVendor.DINERS_CLUB);
        assertThat(CreditCardVendor.detect("36000000000000")).isEqualTo(CreditCardVendor.DINERS_CLUB);
        assertThat(CreditCardVendor.detect("38000000000000")).isEqualTo(CreditCardVendor.DINERS_CLUB);
    }

    // -- Maestro --
    @ParameterizedTest
    @CsvSource({
        "5018000000000000", "5020000000000000", "5038000000000000",
        "5893000000000000", "6304000000000000", "6759000000000000",
        "6761000000000000", "6762000000000000", "6763000000000000"
    })
    void detectMaestro(String number)
    {
        assertThat(CreditCardVendor.detect(number)).isEqualTo(CreditCardVendor.MAESTRO);
    }

    // -- Unknown / null --
    @Test
    void detectUnknown()
    {
        assertThat(CreditCardVendor.detect("0000000000000000")).isNull();
        assertThat(CreditCardVendor.detect("9999999999999999")).isNull();
        assertThat(CreditCardVendor.detect(null)).isNull();
        assertThat(CreditCardVendor.detect("")).isNull();
        assertThat(CreditCardVendor.detect("   ")).isNull();
    }

    // -- Partial input --
    @Test
    void detectPartialInput()
    {
        assertThat(CreditCardVendor.detect("4")).isEqualTo(CreditCardVendor.VISA);
        assertThat(CreditCardVendor.detect("51")).isEqualTo(CreditCardVendor.MASTERCARD);
        assertThat(CreditCardVendor.detect("3")).isNotNull(); // could be Amex, JCB, or Diners
    }

    // -- Valid lengths --
    @Test
    void validLengths()
    {
        assertThat(CreditCardVendor.VISA.isValidLength(16)).isTrue();
        assertThat(CreditCardVendor.VISA.isValidLength(13)).isTrue();
        assertThat(CreditCardVendor.VISA.isValidLength(15)).isFalse();

        assertThat(CreditCardVendor.AMEX.isValidLength(15)).isTrue();
        assertThat(CreditCardVendor.AMEX.isValidLength(16)).isFalse();

        assertThat(CreditCardVendor.DINERS_CLUB.isValidLength(14)).isTrue();
        assertThat(CreditCardVendor.DINERS_CLUB.isValidLength(16)).isTrue();
    }

    // -- CVV lengths --
    @Test
    void cvvLengths()
    {
        assertThat(CreditCardVendor.AMEX.getCvvLength()).isEqualTo(4);
        assertThat(CreditCardVendor.VISA.getCvvLength()).isEqualTo(3);
        assertThat(CreditCardVendor.MASTERCARD.getCvvLength()).isEqualTo(3);
    }

    // -- Format groups --
    @Test
    void formatGroups()
    {
        assertThat(CreditCardVendor.VISA.getFormatGroups()).containsExactly(4, 4, 4, 4);
        assertThat(CreditCardVendor.AMEX.getFormatGroups()).containsExactly(4, 6, 5);
        assertThat(CreditCardVendor.DINERS_CLUB.getFormatGroups()).containsExactly(4, 6, 4);
    }

    // -- Non-digit handling --
    @Test
    void detectStripsNonDigits()
    {
        assertThat(CreditCardVendor.detect("4111-1111-1111-1111")).isEqualTo(CreditCardVendor.VISA);
        assertThat(CreditCardVendor.detect("4111 1111 1111 1111")).isEqualTo(CreditCardVendor.VISA);
    }
}
