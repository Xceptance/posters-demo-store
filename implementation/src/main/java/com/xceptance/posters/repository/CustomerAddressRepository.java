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

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xceptance.posters.entity.CustomerAddress;

/**
 * Spring Data repository for {@link CustomerAddress} entities.
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer>
{
    /**
     * Finds all addresses belonging to the given customer.
     *
     * @param customerId UUID of the owning customer
     * @return list of addresses, possibly empty
     */
    List<CustomerAddress> findByCustomer_Id(UUID customerId);
}
