package com.xceptance.posters.controller;

import com.xceptance.posters.entity.CatalogService;
import com.xceptance.posters.entity.Category;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.entity.Variant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for the new catalog entity model.
 * Provides JSON endpoints for categories, products, and variants.
 * This coexists with the legacy CatalogController (which serves Thymeleaf pages)
 * and will eventually replace it.
 */
@RestController
@RequestMapping("/api/v2/catalog")
public class NewCatalogController {

    private final CatalogService catalogService;

    public NewCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getTopCategories() {
        List<Category> categories = catalogService.getTopCategories();
        var result = categories.stream().map(this::categoryToMap).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<List<Map<String, Object>>> getSubCategories(@PathVariable int categoryId) {
        List<Category> subs = catalogService.getSubCategories(categoryId);
        var result = subs.stream().map(this::categoryToMap).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(@PathVariable int categoryId) {
        List<Product> products = catalogService.getProductsByCategory(categoryId);
        var result = products.stream().map(this::productToMap).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable int productId) {
        return catalogService.getProductById(productId)
            .map(p -> ResponseEntity.ok(productToMap(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<List<Map<String, Object>>> getVariants(@PathVariable int productId) {
        List<Variant> variants = catalogService.getVariantsByProduct(productId);
        var result = variants.stream().map(this::variantToMap).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/sku/{sku}")
    public ResponseEntity<Map<String, Object>> getProductBySku(@PathVariable String sku) {
        return catalogService.getProductBySku(sku)
            .map(p -> ResponseEntity.ok(productToMap(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    // ── Mappers ─────────────────────────────────────────────────

    private Map<String, Object> categoryToMap(Category c) {
        return Map.of(
            "id", c.getId(),
            "nameTextId", c.getNameTextId() != null ? c.getNameTextId() : 0,
            "hasParent", c.getParent() != null
        );
    }

    private Map<String, Object> productToMap(Product p) {
        var map = new java.util.LinkedHashMap<String, Object>();
        map.put("id", p.getId());
        map.put("sku", p.getSku());
        map.put("nameTextId", p.getNameTextId() != null ? p.getNameTextId() : 0);
        map.put("smallImageUrl", p.getSmallImageUrl());
        map.put("mediumImageUrl", p.getMediumImageUrl());
        return map;
    }

    private Map<String, Object> variantToMap(Variant v) {
        return Map.of(
            "id", v.getId(),
            "variantNumber", v.getVariantNumber(),
            "fullSku", v.getFullSku() != null ? v.getFullSku() : ""
        );
    }
}
