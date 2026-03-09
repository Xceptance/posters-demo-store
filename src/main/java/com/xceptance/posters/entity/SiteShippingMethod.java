package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "site_shipping_methods")
@IdClass(SiteShippingMethodId.class)
public class SiteShippingMethod {

    @Id
    @Column(name = "site_id")
    private Integer siteId;

    @Id
    @Column(name = "shipping_method_id")
    private Integer shippingMethodId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", insertable = false, updatable = false)
    private Site site;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id", insertable = false, updatable = false)
    private ShippingMethod shippingMethod;

    @Column(nullable = false)
    private Boolean active = true;

    // Getters and Setters

    public Integer getSiteId() { return siteId; }
    public Integer getShippingMethodId() { return shippingMethodId; }

    public Site getSite() { return site; }
    public void setSite(Site site) {
        this.site = site;
        this.siteId = site != null ? site.getId() : null;
    }

    public ShippingMethod getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
        this.shippingMethodId = shippingMethod != null ? shippingMethod.getId() : null;
    }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
