/*
 * Copyright 2013-2026 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * AI-generated file — created by Claude Opus 4.6
 */
package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PasswordComplexityValidator}.
 * Validates password complexity rules:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 */
class PasswordComplexityValidatorTest
{
    private PasswordComplexityValidator validator;

    @BeforeEach
    void setUp()
    {
        validator = new PasswordComplexityValidator();
    }

    // === Happy path ===

    @Test
    void validPassword_shouldReturnNoErrors()
    {
        final List<String> errors = validator.validate("Str0ng!Pass");
        assertThat(errors).isEmpty();
    }

    @Test
    void exactlyEightCharacters_shouldPass()
    {
        final List<String> errors = validator.validate("Aa1!xxxx");
        assertThat(errors).isEmpty();
    }

    // === Null and empty ===

    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmpty_shouldFail(final String password)
    {
        final List<String> errors = validator.validate(password);
        assertThat(errors).isNotEmpty();
    }

    // === Too short ===

    @Test
    void tooShort_shouldReturnLengthError()
    {
        final List<String> errors = validator.validate("Aa1!x");
        assertThat(errors).anyMatch(e -> e.contains("8"));
    }

    // === Missing character classes ===

    @Test
    void missingUppercase_shouldFail()
    {
        final List<String> errors = validator.validate("str0ng!pass");
        assertThat(errors).anyMatch(e -> e.toLowerCase().contains("uppercase"));
    }

    @Test
    void missingLowercase_shouldFail()
    {
        final List<String> errors = validator.validate("STR0NG!PASS");
        assertThat(errors).anyMatch(e -> e.toLowerCase().contains("lowercase"));
    }

    @Test
    void missingDigit_shouldFail()
    {
        final List<String> errors = validator.validate("Strong!Pass");
        assertThat(errors).anyMatch(e -> e.toLowerCase().contains("digit"));
    }

    @Test
    void missingSpecialCharacter_shouldFail()
    {
        final List<String> errors = validator.validate("Str0ngPass1");
        assertThat(errors).anyMatch(e -> e.toLowerCase().contains("special"));
    }

    // === Multiple violations ===

    @Test
    void multipleViolations_shouldReturnAllErrors()
    {
        // "abc" — too short, no uppercase, no digit, no special char
        final List<String> errors = validator.validate("abc");
        assertThat(errors).hasSizeGreaterThanOrEqualTo(3);
    }

    // === Boundary: passwords with whitespace or unicode ===

    @Test
    void passwordWithSpaces_shouldBeAccepted()
    {
        // Spaces are allowed in passwords (passphrase-style)
        final List<String> errors = validator.validate("My P@ss 1!");
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Str0ng!Pass",
        "C0mpl3x#Pw",
        "P@ssw0rd!X",
        "Abcdef1!xy"
    })
    void variousValidPasswords_shouldPass(final String password)
    {
        final List<String> errors = validator.validate(password);
        assertThat(errors).isEmpty();
    }
}
