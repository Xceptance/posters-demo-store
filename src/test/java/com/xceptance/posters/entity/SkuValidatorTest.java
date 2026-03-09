package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SkuValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "ABCDEF-0000",   // simple product, 6-char prefix
        "ABCDEF-0001",   // variant
        "ABCDEF1234-9999", // 10-char prefix, max variant
        "A1B2C3-0001",   // mixed alphanumeric prefix
        "ABC123-1234",   // typical variant
        "ABCDEFGHIJ-0001" // 10-char prefix
    })
    void testValidSkus(String sku) {
        assertThat(SkuValidator.isValid(sku)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ABCDE-0001",    // prefix too short (5 chars)
        "ABCDEFGHIJK-0001", // prefix too long (11 chars)
        "abcdef-0001",   // lowercase not allowed
        "ABCDEF-001",    // suffix too short (3 digits)
        "ABCDEF-00001",  // suffix too long (5 digits)
        "ABCDEF",        // no suffix (master prefix only)
        "ABCDEF-",       // missing suffix digits
        "-0001",         // missing prefix
        "",              // empty
        "ABC DEF-0001",  // space in prefix
        "ABCDEF_0001",   // wrong separator
    })
    void testInvalidSkus(String sku) {
        assertThat(SkuValidator.isValid(sku)).isFalse();
    }

    @Test
    void testValidMasterPrefix() {
        assertThat(SkuValidator.isValidMasterPrefix("ABCDEF")).isTrue();
        assertThat(SkuValidator.isValidMasterPrefix("ABCDEFGHIJ")).isTrue();
        assertThat(SkuValidator.isValidMasterPrefix("A1B2C3")).isTrue();
    }

    @Test
    void testInvalidMasterPrefix() {
        assertThat(SkuValidator.isValidMasterPrefix("ABCDE")).isFalse();  // too short
        assertThat(SkuValidator.isValidMasterPrefix("ABCDEFGHIJK")).isFalse();  // too long
        assertThat(SkuValidator.isValidMasterPrefix("abcdef")).isFalse();  // lowercase
    }

    @Test
    void testExtractVariantNumber() {
        assertThat(SkuValidator.extractVariantNumber("ABCDEF-0001")).isEqualTo(1);
        assertThat(SkuValidator.extractVariantNumber("POSTER01-0042")).isEqualTo(42);
        assertThat(SkuValidator.extractVariantNumber("ABCDEF-0000")).isEqualTo(0);
    }
}
