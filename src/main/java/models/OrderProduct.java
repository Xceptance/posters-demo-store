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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import io.ebean.DB;

/**
 * This {@link Entity} is the relationship between the {@link Order} and the {@link Product}. It provides additional
 * informations of the product, e.g. the count, the finish, the size and the price.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "orderProduct")
public class OrderProduct
{

    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The {@link Product} of the order-product relationship.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The {@link Order} of the order-product relationship.
     */
    @ManyToOne
    @JoinColumn(name = "ordering_id")
    private Order order;

    /**
     * The count of the product.
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
    @JoinColumn(name = "postersize_id")
    private PosterSize size;
 
    /**
     * The price of the product. The price depends on the selected {@link PosterSize}.
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
     * Returns the {@link Product} of the order-product relationship.
     * 
     * @return the {@link Product}
     */
    public Product getProduct()
    {
        return product;
    }

    /**
     * Sets the {@link Product} of the order-product relationship.
     * 
     * @param product
     *            the {@link Product}
     */
    public void setProduct(final Product product)
    {
        this.product = product;
    }

    /**
     * Returns the {@link Order} of the order-product relationship.
     * 
     * @return the {@link Order}
     */
    public Order getOrder()
    {
        return order;
    }

    /**
     * Sets the {@link Order} of the order-product relationship.
     * 
     * @param order
     *            the {@link Order}
     */
    public void setOrder(final Order order)
    {
        this.order = order;
    }

    /**
     * Returns the product count.
     * 
     * @return the product count
     */
    public int getProductCount()
    {
        return productCount;
    }

    /**
     * Sets the product count.
     * 
     * @param productCount
     *            the product count
     */
    public void setProductCount(final int productCount)
    {
        this.productCount = productCount;
    }

    /**
     * Increments the current product count.
     */
    public void incProductCount()
    {
        setProductCount(getProductCount() + 1);
    }

    /**
     * Returns the finish of the product.
     * 
     * @return the finish of the product
     */
    public String getFinish()
    {
        return finish;
    }

    /**
     * Sets the finish of the product.
     * 
     * @param finish
     *            the finish of the product
     */
    public void setFinish(final String finish)
    {
        this.finish = finish;
    }

    /**
     * Returns the {@link PosterSize} of the product.
     * 
     * @return the {@link PosterSize} of the product
     */
    public PosterSize getSize()
    {
        return size;
    }

    /**
     * Sets the {@link PosterSize} of the product.
     * 
     * @param size
     *            the {@link PosterSize} of the product
     */
    public void setSize(final PosterSize size)
    {
        this.size = size;
    }

    /**
     * Returns the price of the product.
     * 
     * @return the price of the product
     */
    public double getPrice()
    {
        return price;
    }

    /**
     * Returns the price of the product as well formatted String.
     * 
     * @return the price of the product
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
     * Returns the line item price of the product
     * 
     * @return the line item price of the product
     */
    public double getTotalUnitPrice(){
        double temp = price * this.productCount;
        return temp;
    }
    /**
     * Returns the line item price of the product as well formatted String.
     * 
     * @return the line item price of the product
     */
    public String getTotalUnitPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = price * this.productCount;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Sets the price of the product.
     * 
     * @param price
     *            the price of the product
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
        DB.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        DB.save(this);
    }
}
