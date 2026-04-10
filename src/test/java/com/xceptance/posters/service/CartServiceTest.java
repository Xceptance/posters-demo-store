package com.xceptance.posters.service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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

    @Test
    void testToCartDtoMapsRichProductData() {
        // 1. Setup mock database records
        final Locale locale = new Locale();
        locale.setLocale("en-US");
        em.persist(locale);

        final LocalizedText nameText = new LocalizedText();
        nameText.setTextId(101);
        nameText.setLocale(locale);
        nameText.setText("Rich Poster");
        em.persist(nameText);

        final Product product = new Product();
        product.setSku("RICH-001");
        product.setNameTextId(nameText.getTextId());
        product.setMediumImageUrl("/images/rich/medium.jpg");
        em.persist(product);

        final VariationAttribute finishAttr = new VariationAttribute();
        finishAttr.setName("Finish");
        em.persist(finishAttr);

        final Variant variant = new Variant();
        variant.setProduct(product);
        variant.setVariantNumber(1);
        product.getVariants().add(variant);
        em.persist(variant);

        final VariationAttributeValue finishValue = new VariationAttributeValue();
        finishValue.setAttribute(finishAttr);
        finishValue.setValue("Glossy");
        em.persist(finishValue);
        variant.getAttributeValues().add(finishValue);

        final Site site = new Site();
        site.setName("Dto Site");
        site.setCurrency("EUR");
        site.setMainLocale(locale);
        site.setFallbackLocale(locale);
        em.persist(site);

        final CatalogCart cart = new CatalogCart();
        em.persist(cart);
        
        final CartLineItem li = new CartLineItem();
        li.setSku("RICH-001-0001");
        li.setQuantity(2);
        li.setUnitPrice(new BigDecimal("10.00"));
        li.setProductName("Rich Poster");
        cart.addLineItem(li);
        
        em.flush();
        em.clear();

        // 2. Map the DTO
        final com.xceptance.posters.dto.CartDto cartDto = cartService.toCartDto(cart, "en-US", "EUR");

        // 3. Verify exact mapping logic and rich product bindings
        assertThat(cartDto.products()).hasSize(1);
        final com.xceptance.posters.dto.CartItemDto itemDto = cartDto.products().get(0);

        assertThat(itemDto.productName()).isEqualTo("Rich Poster");
        assertThat(itemDto.imageURL()).isEqualTo("/images/rich/medium.jpg");
        assertThat(itemDto.finish()).isEqualTo("Glossy");
        assertThat(itemDto.price()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(itemDto.totalProductPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(itemDto.productCount()).isEqualTo(2);
    }
}
