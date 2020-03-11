package models;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * This entity is the relationship between the {@link Product} and the {@link PosterSize}. It provides the price of the
 * product in the selected size.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "productPosterSize")
public class ProductPosterSize
{

    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The {@link Product} of the product-postersize relationship.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The {@link PosterSize} of the product-postersize relationship.
     */
    @ManyToOne
    @JoinColumn(name = "posterSize_id")
    private PosterSize size;

    /**
     * The price of this product with the selected size.
     */
    private double price;

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
     * Return the {@link Product} of the relationship.
     * 
     * @return the {@link Product} of the relationship
     */
    public Product getProduct()
    {
        return product;
    }

    /**
     * Sets the {@link Product} of the relationship.
     * 
     * @param product
     *            the {@link Product} of the relationship
     */
    public void setProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * Returns the {@link PosterSize} of the relationship.
     * 
     * @return the {@link PosterSize} of the relationship
     */
    public PosterSize getSize()
    {
        return size;
    }

    /**
     * Sets the {@link PosterSize} of the relationship.
     * 
     * @param size
     *            the {@link PosterSize} of the relationship
     */
    public void setSize(final PosterSize size)
    {
        this.size = size;
    }

    /**
     * Returns the price of the product in the selected size.
     * 
     * @return the price of the product in the selected size
     */
    public double getPrice()
    {
        return price;
    }

    /**
     * Returns the price of the product in the selected size as a well formatted String.
     * 
     * @return the price of the product in the selected size
     */
    public String getPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = price;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Sets the price of the product in the selected size.
     * 
     * @param price
     *            the price of the product in the selected size
     */
    public void setPrice(final double price)
    {
        this.price = price;
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
}
