package com.xceptance.posters.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object wrapping the full shopping cart structure for template rendering.
 * Provides the identical field presentation layout that templates (like guest checkout and 
 * mini-cart views) expect globally.
 */
public record CartDto
(
    List<CartItemDto> products,
    BigDecimal subTotalPrice,
    BigDecimal totalTaxPrice,
    BigDecimal totalPrice,
    BigDecimal shippingCosts,
    BigDecimal taxRate,
    int productCount
)
{
}
