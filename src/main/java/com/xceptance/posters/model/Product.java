package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

/**
 * A product in the poster demo store with name, descriptions, images, and available sizes.
 */
@Entity
@Table(name = "product")
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private DefaultText name;

    @OneToOne
    private DefaultText descriptionDetail;

    @OneToOne
    private DefaultText descriptionOverview;

    @OneToMany(mappedBy = "product")
    private List<ProductPosterSize> availableSizes = new ArrayList<>();

    private String imageURL;
    private String smallImageURL;
    private String mediumImageURL;
    private String largeImageURL;
    private String originalImageURL;

    private boolean showInCarousel;

    @ManyToOne
    private SubCategory subCategory;

    @ManyToOne
    private TopCategory topCategory;

    private double minimumPrice;

    @Column(name = "available_finishes")
    private String availableFinishes = "matte,gloss";

    public Product()
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

    public DefaultText getDescriptionDetail()
    {
        return descriptionDetail;
    }

    public void setDescriptionDetail(DefaultText descriptionDetail)
    {
        this.descriptionDetail = descriptionDetail;
    }

    public String getDefaultDescriptionDetail()
    {
        return descriptionDetail != null ? descriptionDetail.getOriginalText() : null;
    }

    public DefaultText getDescriptionOverview()
    {
        return descriptionOverview;
    }

    public void setDescriptionOverview(DefaultText descriptionOverview)
    {
        this.descriptionOverview = descriptionOverview;
    }

    public String getDefaultDescriptionOverview()
    {
        return descriptionOverview != null ? descriptionOverview.getOriginalText() : null;
    }

    public List<ProductPosterSize> getAvailableSizes()
    {
        return availableSizes;
    }

    public void setAvailableSizes(List<ProductPosterSize> availableSizes)
    {
        this.availableSizes = availableSizes;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public void setImageURL(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public String getSmallImageURL()
    {
        return smallImageURL;
    }

    public void setSmallImageURL(String smallImageURL)
    {
        this.smallImageURL = smallImageURL;
    }

    public String getMediumImageURL()
    {
        return mediumImageURL;
    }

    public void setMediumImageURL(String mediumImageURL)
    {
        this.mediumImageURL = mediumImageURL;
    }

    public String getLargeImageURL()
    {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL)
    {
        this.largeImageURL = largeImageURL;
    }

    public String getOriginalImageURL()
    {
        return originalImageURL;
    }

    public void setOriginalImageURL(String originalImageURL)
    {
        this.originalImageURL = originalImageURL;
    }

    public boolean isShowInCarousel()
    {
        return showInCarousel;
    }

    public void setShowInCarousel(boolean showInCarousel)
    {
        this.showInCarousel = showInCarousel;
    }

    public SubCategory getSubCategory()
    {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory)
    {
        this.subCategory = subCategory;
    }

    public TopCategory getTopCategory()
    {
        return topCategory;
    }

    public void setTopCategory(TopCategory topCategory)
    {
        this.topCategory = topCategory;
    }

    public double getMinimumPrice()
    {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice)
    {
        this.minimumPrice = minimumPrice;
    }

    public String getAvailableFinishes()
    {
        return availableFinishes;
    }

    public void setAvailableFinishes(String availableFinishes)
    {
        this.availableFinishes = availableFinishes;
    }

    /**
     * Returns the available finishes as a list for template iteration.
     */
    public List<String> getAvailableFinishesList()
    {
        if (availableFinishes == null || availableFinishes.isBlank())
        {
            return List.of("matte", "gloss");
        }
        return Arrays.stream(availableFinishes.split(","))
                     .map(String::trim)
                     .collect(Collectors.toList());
    }

    /**
     * Returns the minimum price for the given locale by checking
     * locale-specific prices on all available sizes. Falls back to the
     * base minimumPrice if no locale prices exist.
     */
    public double getMinimumPrice(String locale)
    {
        if (locale == null || availableSizes == null || availableSizes.isEmpty())
        {
            return minimumPrice;
        }
        double min = Double.MAX_VALUE;
        for (ProductPosterSize pps : availableSizes)
        {
            double p = pps.getPrice(locale);
            if (p < min) min = p;
        }
        return min < Double.MAX_VALUE ? min : minimumPrice;
    }
}
