package com.xceptance.posters.entity;

import java.io.Serializable;
import java.util.Objects;

public class TaxRateId implements Serializable {

    private Integer taxTable;
    private String name;

    public TaxRateId() {
    }

    public TaxRateId(Integer taxTable, String name) {
        this.taxTable = taxTable;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxRateId that = (TaxRateId) o;
        return Objects.equals(taxTable, that.taxTable) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxTable, name);
    }
}
