package com.xceptance.posters.model;

import java.text.DecimalFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A product entry within an {@link Order} — snapshot of the product at time of purchase.
 */
@Entity
@Table(name = "order_product")
public class OrderProduct
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private PosterSize size;

    private String finish;
    private int productCount;
    private double price;

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

    public String getFinish()
    {
        return finish;
    }

    public void setFinish(String finish)
    {
        this.finish = finish;
    }

    public int getProductCount()
    {
        return productCount;
    }

    public void setProductCount(int productCount)
    {
        this.productCount = productCount;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        return f.format(Math.round(price * 100.0) / 100.0).replace(',', '.');
    }

    public double getTotalProductPrice()
    {
        return Math.round(price * productCount * 100.0) / 100.0;
    }

    public String getTotalProductPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double total = Math.round(price * productCount * 100.0) / 100.0;
        return f.format(total).replace(',', '.');
    }
}
