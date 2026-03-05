package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A sub-category within a {@link TopCategory} (e.g. "Flowers" under "Nature").
 */
@Entity
@Table(name = "sub_category")
public class SubCategory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private DefaultText name;

    @ManyToOne
    private TopCategory topCategory;

    @OneToMany(mappedBy = "subCategory")
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

    public TopCategory getTopCategory()
    {
        return topCategory;
    }

    public void setTopCategory(TopCategory topCategory)
    {
        this.topCategory = topCategory;
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
