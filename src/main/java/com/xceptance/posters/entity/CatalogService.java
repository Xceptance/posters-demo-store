package com.xceptance.posters.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for catalog operations using the new entity model.
 * Provides methods for browsing categories, products, and variants.
 */
@Service
@Transactional(readOnly = true)
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);

    private final CategoryRepository categoryRepository;
    private final CatalogProductRepository productRepository;
    private final VariantRepository variantRepository;

    public CatalogService(CategoryRepository categoryRepository,
                          CatalogProductRepository productRepository,
                          VariantRepository variantRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
    }

    /**
     * Returns all top-level categories (no parent).
     */
    public List<Category> getTopCategories() {
        return categoryRepository.findByParentIsNull();
    }

    /**
     * Returns all sub-categories for a given top-level category.
     */
    public List<Category> getSubCategories(int parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    /**
     * Returns a category by its ID.
     */
    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /**
     * Returns all products in a given category (works for both top and sub).
     */
    public List<Product> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * Returns a product by its ID.
     */
    public Optional<Product> getProductById(int productId) {
        return productRepository.findById(productId);
    }

    /**
     * Returns a product by its SKU.
     */
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    /**
     * Returns all variants for a given product.
     */
    public List<Variant> getVariantsByProduct(int productId) {
        return variantRepository.findByProductId(productId);
    }
}
