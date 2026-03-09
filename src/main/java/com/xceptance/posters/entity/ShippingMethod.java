package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shipping_methods")
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name = "name_text_id")
    private Integer nameTextId;

    @Column(name = "description_text_id")
    private Integer descriptionTextId;

    // Getters and Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getNameTextId() { return nameTextId; }
    public void setNameTextId(Integer nameTextId) { this.nameTextId = nameTextId; }

    public Integer getDescriptionTextId() { return descriptionTextId; }
    public void setDescriptionTextId(Integer descriptionTextId) { this.descriptionTextId = descriptionTextId; }
}
