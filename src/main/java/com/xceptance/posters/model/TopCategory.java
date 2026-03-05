package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A top-level category in the product catalog (e.g. "Nature", "Animals").
 */
@Entity
@Table(name = "top_category")
public class TopCategory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private DefaultText name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topCategory")
    private List<SubCategory> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "topCategory")
    private List<Product> products = new ArrayList<>();

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DefaultText getName()
    {
        return name;
    }

    public void setName(DefaultText name)
    {
        this.name = name;
    }

    public String getDefaultName()
    {
        return name != null ? name.getOriginalText() : null;
    }

    public List<SubCategory> getSubCategories()
    {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories)
    {
        this.subCategories = subCategories;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
    }
}
