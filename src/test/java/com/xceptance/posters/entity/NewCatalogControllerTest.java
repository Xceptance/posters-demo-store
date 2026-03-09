package com.xceptance.posters.entity;

import com.xceptance.posters.controller.NewCatalogController;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration test for the new catalog API:
 * Controller → Service → Repository → DB → JSON response.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NewCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    private int topCatId;
    private int subCatId;
    private int productId;

    @BeforeEach
    void setUp() {
        // Category hierarchy
        Category topCat = new Category();
        topCat.setNameTextId(8001);
        em.persist(topCat);

        Category subCat = new Category();
        subCat.setNameTextId(8002);
        subCat.setParent(topCat);
        em.persist(subCat);

        // Product in subcategory
        Product product = new Product();
        product.setSku("APITEST01");
        product.setSmallImageUrl("/img/test.webp");
        product.getCategories().add(subCat);
        em.persist(product);

        // Variant
        Variant variant = new Variant();
        variant.setProduct(product);
        variant.setVariantNumber(1);
        em.persist(variant);

        em.flush();

        topCatId = topCat.getId();
        subCatId = subCat.getId();
        productId = product.getId();
    }

    @Test
    void testGetTopCategories() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[?(@.id == " + topCatId + ")].hasParent", hasItem(false)));
    }

    @Test
    void testGetSubCategories() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/categories/" + topCatId + "/subcategories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].hasParent", is(true)));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/categories/" + subCatId + "/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].sku", is("APITEST01")));
    }

    @Test
    void testGetProductById() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/products/" + productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sku", is("APITEST01")))
            .andExpect(jsonPath("$.smallImageUrl", is("/img/test.webp")));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/products/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetVariants() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/products/" + productId + "/variants"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].fullSku", is("APITEST01-0001")));
    }

    @Test
    void testGetProductBySku() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/products/sku/APITEST01"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId)));
    }

    @Test
    void testGetProductBySkuNotFound() throws Exception {
        mockMvc.perform(get("/api/v2/catalog/products/sku/NONEXIST"))
            .andExpect(status().isNotFound());
    }
}
