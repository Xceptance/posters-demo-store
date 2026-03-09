package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "CatalogSession")
@Table(name = "sessions")
public class CatalogSession {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CatalogCustomer customer;

    @Column(nullable = false)
    private Boolean anonymous = true;

    @Column(nullable = false)
    private Boolean authenticated = false;

    @Column(name = "authenticated_at")
    private LocalDateTime authenticatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastAccessedAt == null) {
            lastAccessedAt = LocalDateTime.now();
        }
    }

    /**
     * Transition from Anonymous to Identified (customer known but not authenticated).
     */
    public void identify(CatalogCustomer customer) {
        this.customer = customer;
        this.anonymous = false;
    }

    /**
     * Transition from any state to Authenticated.
     */
    public void authenticate(CatalogCustomer customer) {
        this.customer = customer;
        this.anonymous = false;
        this.authenticated = true;
        this.authenticatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CatalogCustomer getCustomer() { return customer; }
    public void setCustomer(CatalogCustomer customer) { this.customer = customer; }

    public Boolean getAnonymous() { return anonymous; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }

    public Boolean getAuthenticated() { return authenticated; }
    public void setAuthenticated(Boolean authenticated) { this.authenticated = authenticated; }

    public LocalDateTime getAuthenticatedAt() { return authenticatedAt; }
    public void setAuthenticatedAt(LocalDateTime authenticatedAt) { this.authenticatedAt = authenticatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
}
