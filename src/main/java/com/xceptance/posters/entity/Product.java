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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name = "CatalogProduct")
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(name = "name_text_id")
    private Integer nameTextId;

    @Column(name = "description_detail_text_id")
    private Integer descriptionDetailTextId;

    @Column(name = "description_overview_text_id")
    private Integer descriptionOverviewTextId;

    @Column(name = "small_image_url")
    private String smallImageUrl;

    @Column(name = "medium_image_url")
    private String mediumImageUrl;

    @Column(name = "large_image_url")
    private String largeImageUrl;

    @Column(name = "original_image_url")
    private String originalImageUrl;

    @Column(name = "show_in_carousel")
    private Boolean showInCarousel = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_variation_attributes",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    private Set<VariationAttribute> variationAttributes = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Variant> variants = new ArrayList<>();

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getNameTextId() {
        return nameTextId;
    }

    public void setNameTextId(Integer nameTextId) {
        this.nameTextId = nameTextId;
    }

    public Integer getDescriptionDetailTextId() {
        return descriptionDetailTextId;
    }

    public void setDescriptionDetailTextId(Integer descriptionDetailTextId) {
        this.descriptionDetailTextId = descriptionDetailTextId;
    }

    public Integer getDescriptionOverviewTextId() {
        return descriptionOverviewTextId;
    }

    public void setDescriptionOverviewTextId(Integer descriptionOverviewTextId) {
        this.descriptionOverviewTextId = descriptionOverviewTextId;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getOriginalImageUrl() {
        return originalImageUrl;
    }

    public void setOriginalImageUrl(String originalImageUrl) {
        this.originalImageUrl = originalImageUrl;
    }

    public Boolean getShowInCarousel() {
        return showInCarousel;
    }

    public void setShowInCarousel(Boolean showInCarousel) {
        this.showInCarousel = showInCarousel;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<VariationAttribute> getVariationAttributes() {
        return variationAttributes;
    }

    public void setVariationAttributes(Set<VariationAttribute> variationAttributes) {
        this.variationAttributes = variationAttributes;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
}
