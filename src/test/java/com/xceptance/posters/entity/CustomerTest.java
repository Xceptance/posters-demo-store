package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testCreateCustomer() {
        CatalogCustomer c = new CatalogCustomer();
        c.setEmail("john@example.com");
        c.setFirstName("John");
        c.setLastName("Doe");

        CatalogCustomer saved = em.persistAndFlush(c);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void testCreateCustomerProfile() {
        CatalogCustomer c = new CatalogCustomer();
        c.setEmail("jane@example.com");
        c.setFirstName("Jane");
        c.setLastName("Smith");
        c = em.persistAndFlush(c);

        CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(c);
        profile.setPassword("$2a$10$hashedpassword");
        profile = em.persistAndFlush(profile);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getCustomer().getEmail()).isEqualTo("jane@example.com");
        assertThat(profile.getFailedLoginAttempts()).isEqualTo(0);
    }

    @Test
    void testCustomerWithOptionalMiddleName() {
        CatalogCustomer c = new CatalogCustomer();
        c.setEmail("bob@example.com");
        c.setFirstName("Robert");
        c.setMiddleName("James");
        c.setLastName("Brown");
        c = em.persistAndFlush(c);

        assertThat(c.getMiddleName()).isEqualTo("James");
    }

    @Test
    void testCustomerProfileLoginTracking() {
        CatalogCustomer c = new CatalogCustomer();
        c.setEmail("track@example.com");
        c.setFirstName("Track");
        c.setLastName("User");
        c = em.persistAndFlush(c);

        CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(c);
        profile.setPassword("$2a$10$hash");
        profile.setFailedLoginAttempts(3);
        profile = em.persistAndFlush(profile);

        assertThat(profile.getFailedLoginAttempts()).isEqualTo(3);
    }
}
