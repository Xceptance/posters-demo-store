package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "catalog_order_payment_history")
public class OrderPaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CatalogOrder order;

    @Column(name = "old_state")
    private String oldState;

    @Column(name = "new_state", nullable = false)
    private String newState;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    private void onCreate() {
        if (changedAt == null) changedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CatalogOrder getOrder() { return order; }
    public void setOrder(CatalogOrder order) { this.order = order; }

    public String getOldState() { return oldState; }
    public void setOldState(String oldState) { this.oldState = oldState; }

    public String getNewState() { return newState; }
    public void setNewState(String newState) { this.newState = newState; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
