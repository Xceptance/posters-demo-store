package com.xceptance.posters.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Stores a locale-specific price override for a {@link ProductPosterSize}.
 * If no LocalizedPrice exists for a given locale, the base price on
 * ProductPosterSize is used as fallback.
 */
@Entity
@Table(name = "localized_price")
public class LocalizedPrice
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private ProductPosterSize productPosterSize;

    @ManyToOne(optional = false)
    private Language language;

    private double price;

    public LocalizedPrice()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public ProductPosterSize getProductPosterSize()
    {
        return productPosterSize;
    }

    public void setProductPosterSize(ProductPosterSize productPosterSize)
    {
        this.productPosterSize = productPosterSize;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }
}
