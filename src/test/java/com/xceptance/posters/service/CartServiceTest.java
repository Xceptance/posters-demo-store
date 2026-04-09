package com.xceptance.posters.service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CartService.class, PostersProperties.class, LocalizedTextService.class})
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private EntityManager em;

    @Test
    void testAddProductToCartPersistsPriceAndName() {
        // 1. Setup mock database records
        final Locale locale = new Locale();
        locale.setLocale("en-US");
        em.persist(locale);

        final LocalizedText nameText = new LocalizedText();
        nameText.setTextId(100);
        nameText.setLocale(locale);
        nameText.setText("TDD Poster");
        em.persist(nameText);
        em.flush();

        final Product product = new Product();
        product.setSku("TDD-001");
        product.setNameTextId(nameText.getTextId());
        em.persist(product);

        final Variant variant = new Variant();
        variant.setProduct(product);
        variant.setVariantNumber(1);
        product.getVariants().add(variant);
        em.persist(variant);

        final Site site = new Site();
        site.setName("Test Site");
        site.setCurrency("USD");
        site.setMainLocale(locale);
        site.setFallbackLocale(locale);
        em.persist(site);

        final PriceTable priceTable = new PriceTable();
        priceTable.setCurrency("USD");
        priceTable.setName("Standard Price");
        em.persist(priceTable);
        site.setPriceTable(priceTable);

        final Price price = new Price();
        price.setSku(variant.getFullSku());
        price.setPriceTable(priceTable);
        price.setPrice(new BigDecimal("19.99"));
        em.persist(price);

        final CatalogCart cart = new CatalogCart();
        em.persist(cart);
        em.flush();
        em.clear();

        // 2. Add product to cart
        final boolean success = cartService.addProductToCart(cart, product, null, null, 1, "USD");
        assertThat(success).isTrue();

        // 3. Verify CartLineItem contains fully persisted price and name!
        assertThat(cart.getLineItems()).hasSize(1);
        final CartLineItem capturedItem = cart.getLineItems().get(0);
        
        assertThat(capturedItem.getSku()).isEqualTo("TDD-001-0001");
        assertThat(capturedItem.getUnitPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
        assertThat(capturedItem.getProductName()).isEqualTo("TDD Poster");
        
        // Ensure cart subtotal is correctly based on persisted unit price
        assertThat(cart.getSubTotal()).isEqualByComparingTo(new BigDecimal("19.99"));
    }
}
