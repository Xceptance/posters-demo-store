package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: Customer → Profile → Session lifecycle.
 * Covers registration, login, session binding, and multi-entity consistency.
 */
@DataJpaTest
class CustomerSessionIntegrationTest {

    @Autowired
    private TestEntityManager em;

    private CatalogCustomer customer;
    private CustomerProfile profile;

    @BeforeEach
    void setUp() {
        customer = new CatalogCustomer();
        customer.setEmail("integration@example.com");
        customer.setFirstName("Inga");
        customer.setLastName("Tester");
        customer = em.persistAndFlush(customer);

        profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setPassword("$2a$10$hashedpassword");
        profile = em.persistAndFlush(profile);

        em.clear();
    }

    @Test
    void testCustomerRegistrationCreatesProfile() {
        CatalogCustomer c = em.find(CatalogCustomer.class, customer.getId());
        assertThat(c).isNotNull();
        assertThat(c.getEmail()).isEqualTo("integration@example.com");
        assertThat(c.getCreatedAt()).isNotNull();
    }

    @Test
    void testAnonymousSessionToAuthenticated() {
        // Create anonymous session
        CatalogSession session = new CatalogSession();
        session.setId(UUID.randomUUID().toString());
        session = em.persistAndFlush(session);

        assertThat(session.getAnonymous()).isTrue();
        assertThat(session.getCustomer()).isNull();

        // Authenticate
        CatalogCustomer c = em.find(CatalogCustomer.class, customer.getId());
        session.authenticate(c);
        em.persistAndFlush(session);
        em.clear();

        CatalogSession reloaded = em.find(CatalogSession.class, session.getId());
        assertThat(reloaded.getAuthenticated()).isTrue();
        assertThat(reloaded.getCustomer().getEmail()).isEqualTo("integration@example.com");
    }

    @Test
    void testCustomerWithAddressAndCard() {
        CatalogCustomer c = em.find(CatalogCustomer.class, customer.getId());

        CatalogAddress addr = new CatalogAddress();
        addr.setCustomer(c);
        addr.setRecipientFirstName("Inga");
        addr.setRecipientLastName("Tester");
        addr.setAddressLine1("1 Test St");
        addr.setCity("Testville");
        addr.setState("TS");
        addr.setPostalCode("12345");
        addr.setCountry("DE");
        em.persist(addr);

        CatalogCreditCard card = new CatalogCreditCard();
        card.setNumber("555555******4444");
        card.setVendor("Mastercard");
        card.setName("Inga Tester");
        card.setExpMonth(6);
        card.setExpYear(2030);
        card = em.persist(card);

        c.getCreditCards().add(card);
        em.persistAndFlush(c);
        em.clear();

        CatalogCustomer reloaded = em.find(CatalogCustomer.class, c.getId());
        assertThat(reloaded.getCreditCards()).hasSize(1);
    }

    @Test
    void testFailedLoginTracking() {
        CustomerProfile p = em.find(CustomerProfile.class, profile.getId());
        assertThat(p.getFailedLoginAttempts()).isEqualTo(0);

        p.setFailedLoginAttempts(p.getFailedLoginAttempts() + 1);
        p.setFailedLoginAttempts(p.getFailedLoginAttempts() + 1);
        p.setFailedLoginAttempts(p.getFailedLoginAttempts() + 1);
        em.persistAndFlush(p);

        assertThat(p.getFailedLoginAttempts()).isEqualTo(3);

        // Successful login resets
        p.setFailedLoginAttempts(0);
        em.persistAndFlush(p);

        assertThat(p.getFailedLoginAttempts()).isEqualTo(0);
    }
}
