package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionTest {

    @Autowired
    private TestEntityManager em;

    private CatalogCustomer createCustomer(String email) {
        CatalogCustomer c = new CatalogCustomer();
        c.setEmail(email);
        c.setFirstName("Test");
        c.setLastName("User");
        return em.persistAndFlush(c);
    }

    @Test
    void testAnonymousSession() {
        CatalogSession session = new CatalogSession();
        session.setId(UUID.randomUUID().toString());
        session = em.persistAndFlush(session);

        assertThat(session.getAnonymous()).isTrue();
        assertThat(session.getAuthenticated()).isFalse();
        assertThat(session.getCustomer()).isNull();
        assertThat(session.getCreatedAt()).isNotNull();
    }

    @Test
    void testIdentifiedSession() {
        CatalogCustomer c = createCustomer("identified@example.com");

        CatalogSession session = new CatalogSession();
        session.setId(UUID.randomUUID().toString());
        session = em.persistAndFlush(session);

        session.identify(c);
        em.persistAndFlush(session);
        em.clear();

        CatalogSession reloaded = em.find(CatalogSession.class, session.getId());
        assertThat(reloaded.getAnonymous()).isFalse();
        assertThat(reloaded.getAuthenticated()).isFalse();
        assertThat(reloaded.getCustomer()).isNotNull();
        assertThat(reloaded.getCustomer().getEmail()).isEqualTo("identified@example.com");
    }

    @Test
    void testAuthenticatedSession() {
        CatalogCustomer c = createCustomer("auth@example.com");

        CatalogSession session = new CatalogSession();
        session.setId(UUID.randomUUID().toString());
        session = em.persistAndFlush(session);

        session.authenticate(c);
        em.persistAndFlush(session);
        em.clear();

        CatalogSession reloaded = em.find(CatalogSession.class, session.getId());
        assertThat(reloaded.getAnonymous()).isFalse();
        assertThat(reloaded.getAuthenticated()).isTrue();
        assertThat(reloaded.getAuthenticatedAt()).isNotNull();
        assertThat(reloaded.getCustomer().getEmail()).isEqualTo("auth@example.com");
    }

    @Test
    void testTransitionAnonymousToAuthenticated() {
        CatalogCustomer c = createCustomer("transition@example.com");

        CatalogSession session = new CatalogSession();
        session.setId(UUID.randomUUID().toString());
        session = em.persistAndFlush(session);

        // Start anonymous
        assertThat(session.getAnonymous()).isTrue();

        // Move to authenticated directly
        session.authenticate(c);
        em.persistAndFlush(session);

        assertThat(session.getAnonymous()).isFalse();
        assertThat(session.getAuthenticated()).isTrue();
    }
}
