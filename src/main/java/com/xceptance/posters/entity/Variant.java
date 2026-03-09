package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "variants")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "variant_number", nullable = false)
    private Integer variantNumber;

    @Column(name = "name_text_id")
    private Integer nameTextId;

    @Column(name = "description_text_id")
    private Integer descriptionTextId;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "variant_attribute_values",
        joinColumns = @JoinColumn(name = "variant_id"),
        inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    private Set<VariationAttributeValue> attributeValues = new HashSet<>();

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getVariantNumber() {
        return variantNumber;
    }

    public void setVariantNumber(Integer variantNumber) {
        this.variantNumber = variantNumber;
    }

    public Integer getNameTextId() {
        return nameTextId;
    }

    public void setNameTextId(Integer nameTextId) {
        this.nameTextId = nameTextId;
    }

    public Integer getDescriptionTextId() {
        return descriptionTextId;
    }

    public void setDescriptionTextId(Integer descriptionTextId) {
        this.descriptionTextId = descriptionTextId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<VariationAttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(Set<VariationAttributeValue> attributeValues) {
        this.attributeValues = attributeValues;
    }

    /**
     * Computes the full SKU string: {product.sku}-{variantNumber padded to 4 digits}.
     */
    public String getFullSku() {
        if (product == null || product.getSku() == null) {
            return null;
        }
        return String.format("%s-%04d", product.getSku(), variantNumber);
    }
}
