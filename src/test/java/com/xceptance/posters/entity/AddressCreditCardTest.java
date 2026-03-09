package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AddressCreditCardTest {

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
    void testCreateAddress() {
        CatalogCustomer c = createCustomer("addr@example.com");

        CatalogAddress addr = new CatalogAddress();
        addr.setCustomer(c);
        addr.setRecipientFirstName("John");
        addr.setRecipientLastName("Doe");
        addr.setAddressLine1("123 Main St");
        addr.setCity("Anytown");
        addr.setState("CA");
        addr.setPostalCode("90210");
        addr.setCountry("US");
        addr = em.persistAndFlush(addr);

        assertThat(addr.getId()).isNotNull();
        assertThat(addr.getCustomer().getEmail()).isEqualTo("addr@example.com");
    }

    @Test
    void testCreateCreditCard() {
        CatalogCustomer c = createCustomer("cc@example.com");

        CatalogCreditCard card = new CatalogCreditCard();
        card.setNumber("411111******1111");
        card.setVendor("Visa");
        card.setName("John Doe");
        card.setExpMonth(12);
        card.setExpYear(2028);
        card = em.persistAndFlush(card);

        c.getCreditCards().add(card);
        em.persistAndFlush(c);
        em.clear();

        CatalogCustomer reloaded = em.find(CatalogCustomer.class, c.getId());
        assertThat(reloaded.getCreditCards()).hasSize(1);
    }

    @Test
    void testCustomerMultipleAddresses() {
        CatalogCustomer c = createCustomer("multi@example.com");

        CatalogAddress billing = new CatalogAddress();
        billing.setCustomer(c);
        billing.setRecipientFirstName("Jane");
        billing.setRecipientLastName("Doe");
        billing.setAddressLine1("456 Billing Ave");
        billing.setCity("Billington");
        billing.setState("NY");
        billing.setPostalCode("10001");
        billing.setCountry("US");
        em.persist(billing);

        CatalogAddress shipping = new CatalogAddress();
        shipping.setCustomer(c);
        shipping.setRecipientFirstName("Jane");
        shipping.setRecipientLastName("Doe");
        shipping.setAddressLine1("789 Shipping Rd");
        shipping.setCity("Shipville");
        shipping.setState("TX");
        shipping.setPostalCode("75001");
        shipping.setCountry("US");
        em.persistAndFlush(shipping);

        assertThat(billing.getId()).isNotNull();
        assertThat(shipping.getId()).isNotNull();
        assertThat(billing.getId()).isNotEqualTo(shipping.getId());
    }
}
