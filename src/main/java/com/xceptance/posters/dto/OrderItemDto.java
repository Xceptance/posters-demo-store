package com.xceptance.posters.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object explicitly mapping an individual purchased item line format
 * for historical transaction rendering across confirmation and account layout screens.
 */
public record OrderItemDto
(
    int lineItemId,
    String sku,
    String productName,
    String imageUrl,
    String variantDescription,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal totalPrice
) 
{
    /**
     * Explicit Java mapping to reliably bind the image property 
     * during Spring SpEL resolution within Thymeleaf templates, circumventing
     * casing edge cases of the automatically generated record accessors.
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Explicit mapping resolving the textual variant.
     */
    public String getVariantDescription() {
        return this.variantDescription;
    }

    /**
     * Explicit fallback for name representation.
     */
    public String getProductName() {
        return this.productName;
    }
}
