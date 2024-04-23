/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} provides a product of the poster demo store. Each product has a name, a short and a long description,
 * different sizes the product is available in and the path to an image of the product.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "product")
public class Product
{

    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * A long description of the product.
     */
    @Column(length = 4096)
    private String descriptionDetail;

    /**
     * A short description of the product.
     */
    @Column(length = 1024)
    private String descriptionOverview;

    /**
     * The {@link ProductPosterSize}s which are available for this product.
     */
    @OneToMany(mappedBy = "product")
    private List<ProductPosterSize> availableSizes;

    /**
     * The URL to the product image.
     */
    private String imageURL;

     /**
     * The URL to the product small image.
     */
    private String smallImageURL;

    /**
     * The URL to the product medium image.
     */
    private String mediumImageURL;

    /**
     * The URL to the product large image.
     */
    private String largeImageURL;

    /**
     * The URL to the product large image.
     */
    private String originalImageURL;

    /**
     * Defines, whether or not the product is shown in the carousel on the main page.
     */
    private boolean showInCarousel;

    /**
     * Defines, whether or not the product is shown on the top category overview page.
     */
    private boolean showInTopCategorie;

    /**
     * The sub category, the product belongs to.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subCategory_id")
    private SubCategory subCategory;

    /**
     * The top category, the product belongs to.
     */
    @ManyToOne
    private TopCategory topCategory;

    /**
     * A list of {@link CartProduct}s.
     */
    @OneToMany(mappedBy = "product")
    private List<CartProduct> cart;

    /**
     * A list of {@link OrderProduct}s.
     */
    @OneToMany(mappedBy = "product")
    private List<OrderProduct> order;

    /**
     * The minimum price of the product. The price of a product depends on the selected size.
     */
    private double minimumPrice;

    /**
     * Constructor.
     */
    public Product()
    {
        cart = new ArrayList<CartProduct>();
        availableSizes = new ArrayList<ProductPosterSize>();
    }

    /**
     * Returns the name of the product.
     * 
     * @return the name of the product
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the product.
     * 
     * @param name
     *            the name of the product
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the short description of the product.
     * 
     * @return the short description of the product
     */
    public String getDescriptionOverview()
    {
        return descriptionOverview;
    }

    /**
     * Sets the short description of the product
     * 
     * @param descriptionOverview
     *            the short description of the product
     */
    public void setDescriptionOverview(final String descriptionOverview)
    {
        this.descriptionOverview = descriptionOverview;
    }

    /**
     * Returns the URL to the product image.
     * 
     * @return the URL to the product image
     */
    public String getImageURL()
    {
        return imageURL;
    }

    /**
     * Sets the URL to the product image.
     * 
     * @param imageURL
     *            the URL to the product image
     */
    public void setImageURL(final String imageURL)
    {
        this.imageURL = imageURL;
    }

    /**
     * Returns the URL to the product small image.
     * 
     * @return the URL to the product small image
     */
    public String getSmallImageURL()
    {
        return smallImageURL;
    }

     /**
     * Sets the URL to the product small image.
     * 
     * @param smallImageURL
     *            the URL to the product small image
     */
    public void setSmallImageURL(final String smallImageURL)
    {
        this.smallImageURL = smallImageURL;
    }

    /**
     * Returns the URL to the product medium image.
     * 
     * @return the URL to the product medium image
     */
    public String getMediumImageURL()
    {
        return mediumImageURL;
    }

     /**
     * Sets the URL to the product medium image.
     * 
     * @param mediumImageURL
     *            the URL to the product medium image
     */
    public void setMediumImageURL(final String mediumImageURL)
    {
        this.mediumImageURL = mediumImageURL;
    }

    /**
     * Returns the URL to the product large image.
     * 
     * @return the URL to the product large image
     */
    public String getLargeImageURL()
    {
        return largeImageURL;
    }

     /**
     * Sets the URL to the product large image.
     * 
     * @param largeImageURL
     *            the URL to the product large image
     */
    public void setLargeImageURL(final String largeImageURL)
    {
        this.largeImageURL = largeImageURL;
    }

     /**
     * Returns the URL to the product original image.
     * 
     * @return the URL to the product original image
     */
    public String getOriginalImageURL()
    {
        return originalImageURL;
    }

     /**
     * Sets the URL to the product large image.
     * 
     * @param originalImageURL
     *            the URL to the product large image
     */
    public void setOriginalImageURL(final String originalImageURL)
    {
        this.originalImageURL = originalImageURL;
    }


    /**
     * Returns the ID of the entity.
     * 
     * @return the ID of the entity
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of the entity.
     * 
     * @param id
     *            the ID of the entity
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * Returns the long description of the product.
     * 
     * @return the long description of the product
     */
    public String getDescriptionDetail()
    {
        return descriptionDetail;
    }

    /**
     * Sets the long description of the product.
     * 
     * @param descriptionDetail
     *            the long description of the product
     */
    public void setDescriptionDetail(final String descriptionDetail)
    {
        this.descriptionDetail = descriptionDetail;
    }

    /**
     * Returns a list of available {@link ProductPosterSize}s of the product.
     * 
     * @return a list of available {@link ProductPosterSize}s of the product
     */
    public List<ProductPosterSize> getAvailableSizes()
    {
        return availableSizes;
    }

    /**
     * Sets a list of available {@link ProductPosterSize}s of the product.
     * 
     * @param availableSizes
     *            a list of available {@link ProductPosterSize}s of the product
     */
    public void setAvailableSizes(final List<ProductPosterSize> availableSizes)
    {
        this.availableSizes = availableSizes;
    }

    /**
     * Adds a {@link ProductPosterSize} to the available {@link ProductPosterSize}s of the product
     * 
     * @param size
     *            the {@link ProductPosterSize} to add
     */
    public void addAvailableSize(final ProductPosterSize size)
    {
        availableSizes.add(size);
    }

    /**
     * Returns the {@link SubCategory} of the product.
     * 
     * @return the {@link SubCategory} of the product
     */
    public SubCategory getSubCategory()
    {
        return subCategory;
    }

    /**
     * Sets the {@link SubCategory} of the product.
     * 
     * @param subCategory
     *            the {@link SubCategory} of the product
     */
    public void setSubCategory(final SubCategory subCategory)
    {
        this.subCategory = subCategory;
    }

    /**
     * Returns the {@link TopCategory} of the product.
     * 
     * @return the {@link TopCategory} of the product
     */
    public TopCategory getTopCategory()
    {
        return topCategory;
    }

    /**
     * Sets the {@link TopCategory} of the product.
     * 
     * @param topCategory
     *            the {@link TopCategory} of the product
     */
    public void setTopCategory(final TopCategory topCategory)
    {
        this.topCategory = topCategory;
    }

    /**
     * Returns {@code true} if the product will be shown in the carousel on the main page, otherwise {@code false}.
     * 
     * @return {@code true} if the product is shown in the carousel, otherwise {@code false}
     */
    public boolean isShowInCarousel()
    {
        return showInCarousel;
    }

    /**
     * Sets the information, whether or not the product will be shown in the carousel on the main page.
     * 
     * @param showInCarousel
     *            the information, whether or not the product will be shown in the carousel
     */
    public void setShowInCarousel(final boolean showInCarousel)
    {
        this.showInCarousel = showInCarousel;
    }

    /**
     * Returns {@code true} if the product will be shown on the top category overview page, otherwise {@code false}.
     * 
     * @return {@code true} if the product is shown on the top category overview page, otherwise {@code false}
     */
    public boolean isShowInTopCategorie()
    {
        return showInTopCategorie;
    }

    /**
     * Sets the information, whether or not the product will be shown on the top category overview page.
     * 
     * @param showInTopCategorie
     *            the information, whether or not the product will be shown on the top category overview page
     */
    public void setShowInTopCategorie(final boolean showInTopCategorie)
    {
        this.showInTopCategorie = showInTopCategorie;
    }

    /**
     * Returns the {@link CartProduct}s.
     * 
     * @return the {@link CartProduct}s
     */
    public List<CartProduct> getCart()
    {
        return cart;
    }

    /**
     * Sets the {@link CartProduct}s.
     * 
     * @param cart
     *            the {@link CartProduct}s
     */
    public void setCart(final List<CartProduct> cart)
    {
        this.cart = cart;
    }

    /**
     * Returns the {@link OrderProduct}s.
     * 
     * @return the {@link OrderProduct}s
     */
    public List<OrderProduct> getOrder()
    {
        return order;
    }

    /**
     * Sets the {@link OrderProduct}s.
     * 
     * @param order
     *            the {@link OrderProduct}s
     */
    public void setOrder(final List<OrderProduct> order)
    {
        this.order = order;
    }

    /**
     * Returns the minimum price of the product.
     * 
     * @return the minimum price of the product
     */
    public double getMinimumPrice()
    {
        return minimumPrice;
    }

    /**
     * Returns the minimum price of the product as a well formatted String.
     * 
     * @return the minimum price of the product
     */
    public String getPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = minimumPrice;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Sets the minimum price of the product.
     * 
     * @param minimumPrice
     *            the minimum price of the product
     */
    public void setMinimumPrice(final double minimumPrice)
    {
        this.minimumPrice = minimumPrice;
    }

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Returns a {@link Product} that matches the given ID.
     * 
     * @param id
     *            the ID of the product
     * @return a {@link Product} that matches the given ID
     */
    public static Product getProductById(final int id)
    {
        // get product by id
        return Ebean.find(Product.class, id);
    }
}
