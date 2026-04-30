package com.xceptance.posters.entity;

import java.io.Serializable;
import java.util.Objects;

public class SiteShippingMethodId implements Serializable {

    private Integer siteId;
    private Integer shippingMethodId;

    public SiteShippingMethodId() {}

    public SiteShippingMethodId(Integer siteId, Integer shippingMethodId) {
        this.siteId = siteId;
        this.shippingMethodId = shippingMethodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteShippingMethodId that)) return false;
        return Objects.equals(siteId, that.siteId) && Objects.equals(shippingMethodId, that.shippingMethodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, shippingMethodId);
    }
}
