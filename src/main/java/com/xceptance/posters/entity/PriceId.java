package com.xceptance.posters.entity;

import java.io.Serializable;
import java.util.Objects;

public class PriceId implements Serializable {

    private String sku;
    private Integer priceTable;

    public PriceId() {
    }

    public PriceId(String sku, Integer priceTable) {
        this.sku = sku;
        this.priceTable = priceTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceId priceId = (PriceId) o;
        return Objects.equals(sku, priceId.sku) && Objects.equals(priceTable, priceId.priceTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, priceTable);
    }
}
