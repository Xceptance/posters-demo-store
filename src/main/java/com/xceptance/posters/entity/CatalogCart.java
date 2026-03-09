package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "CatalogCart")
@Table(name = "catalog_carts")
public class CatalogCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_table_id")
    private PriceTable priceTable;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private CartAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private CartAddress billingAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_card_id")
    private CartCreditCard creditCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_costs", precision = 10, scale = 2)
    private BigDecimal shippingCosts;

    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "tax_rate", precision = 5, scale = 4)
    private BigDecimal taxRate;

    @Column(name = "total_tax", precision = 10, scale = 2)
    private BigDecimal totalTax;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartLineItem> lineItems = new ArrayList<>();

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PriceTable getPriceTable() { return priceTable; }
    public void setPriceTable(PriceTable priceTable) { this.priceTable = priceTable; }

    public CartAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(CartAddress shippingAddress) { this.shippingAddress = shippingAddress; }

    public CartAddress getBillingAddress() { return billingAddress; }
    public void setBillingAddress(CartAddress billingAddress) { this.billingAddress = billingAddress; }

    public CartCreditCard getCreditCard() { return creditCard; }
    public void setCreditCard(CartCreditCard creditCard) { this.creditCard = creditCard; }

    public ShippingMethod getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(ShippingMethod shippingMethod) { this.shippingMethod = shippingMethod; }

    public BigDecimal getShippingCosts() { return shippingCosts; }
    public void setShippingCosts(BigDecimal shippingCosts) { this.shippingCosts = shippingCosts; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<CartLineItem> getLineItems() { return lineItems; }

    public void addLineItem(CartLineItem item) {
        lineItems.add(item);
        item.setCart(this);
    }
}
