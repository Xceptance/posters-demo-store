package com.xceptance.posters.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "CatalogOrder")
@Table(name = "catalog_orders")
public class CatalogOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false)
    private String currency;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_state", nullable = false)
    private String orderState;

    @Column(name = "payment_state", nullable = false)
    private String paymentState;

    @Column(name = "shipping_method_sku")
    private String shippingMethodSku;

    @Column(name = "shipping_method_name")
    private String shippingMethodName;

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

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OrderCreditCard creditCard;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStateHistory> stateHistory = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderPaymentHistory> paymentHistory = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderAddress> addresses = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        if (orderDate == null) orderDate = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getOrderState() { return orderState; }
    public void setOrderState(String orderState) { this.orderState = orderState; }

    public String getPaymentState() { return paymentState; }
    public void setPaymentState(String paymentState) { this.paymentState = paymentState; }

    public String getShippingMethodSku() { return shippingMethodSku; }
    public void setShippingMethodSku(String v) { this.shippingMethodSku = v; }

    public String getShippingMethodName() { return shippingMethodName; }
    public void setShippingMethodName(String v) { this.shippingMethodName = v; }

    public BigDecimal getShippingCosts() { return shippingCosts; }
    public void setShippingCosts(BigDecimal v) { this.shippingCosts = v; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal v) { this.subTotal = v; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal v) { this.taxRate = v; }

    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal v) { this.totalTax = v; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { this.total = v; }

    public List<OrderLineItem> getLineItems() { return lineItems; }
    public List<OrderStateHistory> getStateHistory() { return stateHistory; }
    public List<OrderPaymentHistory> getPaymentHistory() { return paymentHistory; }
    public List<OrderAddress> getAddresses() { return addresses; }

    public void addLineItem(OrderLineItem item) { lineItems.add(item); item.setOrder(this); }
    public void addStateHistoryEntry(OrderStateHistory entry) { stateHistory.add(entry); entry.setOrder(this); }
    public void addAddress(OrderAddress addr) { addresses.add(addr); addr.setOrder(this); }

    public OrderCreditCard getCreditCard() { return creditCard; }
    public void setCreditCard(OrderCreditCard cc) { this.creditCard = cc; cc.setOrder(this); }

    /** Returns the SHIPPING address snapshot, or null. */
    public OrderAddress getShippingAddress() {
        return addresses.stream().filter(a -> "SHIPPING".equals(a.getType())).findFirst().orElse(null);
    }

    /** Returns the BILLING address snapshot, or null. */
    public OrderAddress getBillingAddress() {
        return addresses.stream().filter(a -> "BILLING".equals(a.getType())).findFirst().orElse(null);
    }
    public void addPaymentHistoryEntry(OrderPaymentHistory entry) { paymentHistory.add(entry); entry.setOrder(this); }
}
