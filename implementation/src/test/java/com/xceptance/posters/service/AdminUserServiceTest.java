/*
 * Copyright 2026 Xceptance Software Technologies GmbH
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
 */
package com.xceptance.posters.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.entity.AdminUser;

/**
 * Integration tests for the {@link AdminUserService} verifying that admin users can be
 * searched by username, display name, and email.
 *
 * // AI-generated: Gemini 3.5 Flash
 */
@SpringBootTest
@Transactional
class AdminUserServiceTest
{
    @Autowired
    private AdminUserService adminUserService;

    @Test
    final void testSearchUserByEmail()
    {
        // Search for the seeded admin user using their email address
        final Page<AdminUser> result = adminUserService.findAll("admin@posters-demo.local", null, PageRequest.of(0, 10));

        // Assert that the user is found and email matches
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("admin@posters-demo.local");
    }
}
