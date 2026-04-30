package com.xceptance.posters.entity;

import java.io.Serializable;
import java.util.Objects;

public class InventoryEntryId implements Serializable {

    private Integer inventoryTable;
    private String sku;

    public InventoryEntryId() {
    }

    public InventoryEntryId(Integer inventoryTable, String sku) {
        this.inventoryTable = inventoryTable;
        this.sku = sku;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryEntryId that = (InventoryEntryId) o;
        return Objects.equals(inventoryTable, that.inventoryTable) && Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryTable, sku);
    }
}
