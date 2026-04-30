package com.xceptance.posters.dto;

import com.xceptance.posters.entity.OrderAddress;
import com.xceptance.posters.entity.OrderCreditCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object explicitly abstracting a full historical order instance.
 * Contains both financial snapshot aggregates and the explicitly 
 * mapped list of historical products wrapped in the DTO structural layer.
 */
public record OrderDto
(
    String orderNumber,
    LocalDateTime orderDate,
    BigDecimal subTotal,
    BigDecimal totalTax,
    BigDecimal taxRate,
    BigDecimal shippingCosts,
    BigDecimal total,
    OrderAddress shippingAddress,
    OrderAddress billingAddress,
    OrderCreditCard creditCard,
    List<OrderItemDto> lineItems
) 
{
}
