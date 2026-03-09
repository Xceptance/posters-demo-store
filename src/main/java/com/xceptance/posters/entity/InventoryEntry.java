package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_entries")
@IdClass(InventoryEntryId.class)
public class InventoryEntry {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "inventory_table_id", nullable = false)
    private InventoryTable inventoryTable;

    @Id
    @Column(nullable = false)
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    public InventoryTable getInventoryTable() {
        return inventoryTable;
    }

    public void setInventoryTable(InventoryTable inventoryTable) {
        this.inventoryTable = inventoryTable;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
