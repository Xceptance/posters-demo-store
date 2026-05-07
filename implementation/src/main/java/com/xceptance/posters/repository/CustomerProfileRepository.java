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
package com.xceptance.posters.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xceptance.posters.entity.CustomerProfile;

/**
 * Spring Data repository for {@link CustomerProfile} entities.
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Integer>
{
    /**
     * Finds a customer profile by the owning customer's UUID.
     *
     * @param customerId UUID of the customer
     * @return the profile, or empty if none exists
     */
    Optional<CustomerProfile> findByCustomer_Id(UUID customerId);
}
