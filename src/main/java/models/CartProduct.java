package models;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} is the relationship between the {@link Cart} and the {@link Product}. It provides additional
 * informations of the product, e.g. the count, the finish, the size and the price.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "cartProduct")
public class CartProduct
{
    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The {@link Product} of the cart-product relationship.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The {@link Cart} of the cart-product relationship.
     */
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * The count of the product in the cart.
     */
    private int productCount;

    /**
     * The finish of the poster, e.g. matte or gloss.
     */
    private String finish;

    /**
     * The {@link PosterSize} of the product.
     */
    @ManyToOne
    @JoinColumn(name = "posterSize_id")
    private PosterSize size;

    /**
     * The price of the product. The price depends on the selected {@link PosterSize}.
     */
    private double price;

    /**
     * The {@link Timestamp} of the entity.
     */
    @Version
    private Timestamp lastUpdate;

    /**
     * Return the ID of the entity.
     * 
     * @return the ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of the entity.
     * 
     * @param id
     *            the ID
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * Returns the {@link Product} of the cart-product relationship.
     * 
     * @return the {@link Product}
     */
    public Product getProduct()
    {
        return product;
    }

    /**
     * Sets the {@link Product} of the cart-product relationship.
     * 
     * @param product
     *            the {@link Product}
     */
    public void setProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * Returns the {@link Cart} of the cart-product relationship.
     * 
     * @return the {@link Cart}
     */
    public Cart getCart()
    {
        return cart;
    }

    /**
     * Sets the {@link Cart} of the cart-product relationship.
     * 
     * @param cart
     *            the {@link Cart}
     */
    public void setCart(final Cart cart)
    {
        this.cart = cart;
    }

    /**
     * Returns the count of the {@link Product} in the {@link Cart}.
     * 
     * @return the product count
     */
    public int getProductCount()
    {
        return productCount;
    }

    /**
     * Sets the count of the {@link Product} in the {@link Cart}.
     * 
     * @param productCount
     *            the product count
     */
    public void setProductCount(final int productCount)
    {
        this.productCount = productCount;
    }

    /**
     * Returns the finish of the product.
     * 
     * @return the finish
     */
    public String getFinish()
    {
        return finish;
    }

    /**
     * Sets the finish of the product.
     * 
     * @param finish
     *            the finish
     */
    public void setFinish(final String finish)
    {
        this.finish = finish;
    }

    /**
     * Returns the {@link PosterSize} of the product.
     * 
     * @return the {@link PosterSize}
     */
    public PosterSize getSize()
    {
        return size;
    }

    /**
     * Sets the {@link PosterSize} of the product.
     * 
     * @param size
     *            the {@link PosterSize}
     */
    public void setSize(final PosterSize size)
    {
        this.size = size;
    }

    /**
     * Returns the price of the product.
     * 
     * @return the price
     */
    public double getPrice()
    {
        return price;
    }

    /**
     * Returns the price of the product as well formatted {@link String}.
     * 
     * @return the price
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
     * Sets the price of the product.
     * 
     * @param price
     *            the price
     */
    public void setPrice(final double price)
    {
        this.price = price;
    }

    /**
     * Returns the {@link Timestamp} of the entity.
     * 
     * @return the {@link Timestamp}
     */
    public Timestamp getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Sets the {@link Timestamp} of the entity.
     * 
     * @param lastUpdate
     *            the {@link Timestamp}
     */
    public void setLastUpdate(final Timestamp lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Increments the product count.
     */
    public void incProductCount()
    {
        setProductCount(getProductCount() + 1);
        update();
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
     * Deletes the entity from the database.
     */
    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Decrements the product count.
     */
    public void decrementProductCount()
    {
        setProductCount(getProductCount() - 1);
        update();
    }
}
