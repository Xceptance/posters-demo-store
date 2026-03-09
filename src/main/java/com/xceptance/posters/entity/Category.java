package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @Column(name = "name_text_id", nullable = false)
    private Integer nameTextId;

    @Column(name = "description_text_id")
    private Integer descriptionTextId;

    @Column(name = "overview_text_id")
    private Integer overviewTextId;

    @Column(name = "image_path")
    private String imagePath;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
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

    public Integer getOverviewTextId() {
        return overviewTextId;
    }

    public void setOverviewTextId(Integer overviewTextId) {
        this.overviewTextId = overviewTextId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
