package com.xceptance.posters.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing a single line item within a shopping cart.
 * Maps raw database entity properties to presentation-friendly formats, supporting
 * product metadata resolutions such as sizes, finishes, and image URLs.
 */
public record CartItemDto
(
    int lineItemId,
    int productId,
    String productName,
    String imageURL,
    String finish,
    String sizeLabel,
    BigDecimal price,
    int productCount,
    BigDecimal totalProductPrice,
    String sku
)
{
    public String getImageURL() {
        return this.imageURL;
    }
    
    public String getProductName() {
        return this.productName;
    }
}
