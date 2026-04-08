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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Validates password complexity for customer registration.
 *
 * <p>Rules enforced:
 * <ul>
 *   <li>Minimum 8 characters</li>
 *   <li>At least one uppercase letter (A-Z)</li>
 *   <li>At least one lowercase letter (a-z)</li>
 *   <li>At least one digit (0-9)</li>
 *   <li>At least one special character (non-alphanumeric)</li>
 * </ul>
 *
 * <p>Returns a list of human-readable error messages for each
 * violated rule — an empty list indicates a valid password.
 */
@Service
public final class PasswordComplexityValidator
{
    /** Minimum number of characters required. */
    private static final int MIN_LENGTH = 8;

    /**
     * Validates the given password against the complexity rules.
     *
     * @param password the plain-text password to validate (may be {@code null})
     * @return a list of error messages; empty if the password is valid
     */
    public final List<String> validate(final String password)
    {
        final List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty())
        {
            errors.add("Password is required.");
            return errors;
        }

        if (password.length() < MIN_LENGTH)
        {
            errors.add("Password must be at least 8 characters long.");
        }

        if (!password.chars().anyMatch(Character::isUpperCase))
        {
            errors.add("Password must contain at least one uppercase letter.");
        }

        if (!password.chars().anyMatch(Character::isLowerCase))
        {
            errors.add("Password must contain at least one lowercase letter.");
        }

        if (!password.chars().anyMatch(Character::isDigit))
        {
            errors.add("Password must contain at least one digit.");
        }

        if (password.chars().allMatch(Character::isLetterOrDigit))
        {
            errors.add("Password must contain at least one special character.");
        }

        return errors;
    }
}
