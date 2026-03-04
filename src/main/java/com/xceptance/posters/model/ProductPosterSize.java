package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Links a {@link Product} to a {@link PosterSize} with a specific price.
 * Supports locale-specific price overrides via {@link LocalizedPrice}.
 */
@Entity
@Table(name = "product_poster_size")
public class ProductPosterSize
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private PosterSize size;

    private double price;

    @OneToMany(mappedBy = "productPosterSize", fetch = FetchType.EAGER)
    private List<LocalizedPrice> localizedPrices = new ArrayList<>();

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public PosterSize getSize()
    {
        return size;
    }

    public void setSize(PosterSize size)
    {
        this.size = size;
    }

    public double getPrice()
    {
        return price;
    }

    /**
     * Returns the price for the given locale (e.g. "de-DE").
     * Checks localizedPrices for a matching language code or fallbackCode,
     * falls back to the base price if none found.
     */
    public double getPrice(String locale)
    {
        if (locale != null && localizedPrices != null)
        {
            for (LocalizedPrice lp : localizedPrices)
            {
                Language lang = lp.getLanguage();
                if (lang != null
                    && (locale.equals(lang.getCode())
                        || locale.equals(lang.getFallbackCode())))
                {
                    return lp.getPrice();
                }
            }
        }
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public List<LocalizedPrice> getLocalizedPrices()
    {
        return localizedPrices;
    }

    public void setLocalizedPrices(List<LocalizedPrice> localizedPrices)
    {
        this.localizedPrices = localizedPrices;
    }
}
