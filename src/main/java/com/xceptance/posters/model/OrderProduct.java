package com.xceptance.posters.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
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

    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

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

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public String getPriceAsString()
    {
        return price.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public BigDecimal getTotalProductPrice()
    {
        return price.multiply(BigDecimal.valueOf(productCount)).setScale(2, RoundingMode.HALF_UP);
    }

    public String getTotalProductPriceAsString()
    {
        return getTotalProductPrice().toPlainString();
    }
}
