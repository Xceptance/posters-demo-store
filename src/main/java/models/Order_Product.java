package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "order_product")
public class Order_Product
{
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "ordering_id")
    private Order order;

    private int countProduct;

    /**
     * The finish of the poster, e.g. matte or gloss.
     */
    private String finish;

    /**
     * The size of the poster.
     */
    @ManyToOne
    @JoinColumn(name = "postersize_id")
    private PosterSize size;

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

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }

    public int getCountProduct()
    {
        return countProduct;
    }

    public void setCountProduct(int countProduct)
    {
        this.countProduct = countProduct;
    }

    public void incCountProduct()
    {
        this.setCountProduct(this.getCountProduct() + 1);
    }

    public String getFinish()
    {
        return finish;
    }

    public void setFinish(String finish)
    {
        this.finish = finish;
    }

    public PosterSize getSize()
    {
        return size;
    }

    public void setSize(PosterSize size)
    {
        this.size = size;
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }
}
