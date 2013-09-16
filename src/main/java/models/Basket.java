package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "basket")
public class Basket
{

    /**
     * The id of the basket.
     */
    @Id
    private int id;

    /**
     * The customer of the basket.
     */
    @OneToOne
    private Customer customer;

    /**
     * The products of the basket.
     */
    @OneToMany(mappedBy = "basket")
    private List<Basket_Product> products;

    /**
     * The total price of the basket.
     */
    private double totalPrice;

    public Basket()
    {
        this.products = new ArrayList<Basket_Product>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public double getTotalPrice()
    {
        double temp = totalPrice;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return temp;
    }

    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public List<Basket_Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Basket_Product> products)
    {
        this.products = products;
    }

    public void addProduct(Product product)
    {
        Basket_Product basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", this)
                                             .eq("product", product).findUnique();
        // this product is not in the basket
        if (basketProducts == null)
        {
            // add product to basket
            basketProducts = new Basket_Product();
            basketProducts.setBasket(this);
            basketProducts.setProduct(product);
            // set product count to one
            basketProducts.setCountProduct(1);
            basketProducts.save();
            products.add(basketProducts);
        }
        // this product is in the basket at least one time
        else
        {
            // increment the count of this product
            basketProducts.incCountProduct();
            basketProducts.update();
        }
        // recalculate total price
        this.setTotalPrice(getTotalPrice() + product.getPrice());
    }

    public void deleteProduct(Product product)
    {
        Basket_Product basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", this)
                                             .eq("product", product).findUnique();
        // product is not in the basket
        if (basketProducts == null)
        {
            // do nothing
        }
        else
        {
            // product is in the basket more than once
            if (basketProducts.getCountProduct() > 1)
            {
                basketProducts.decrementProductCount();
                basketProducts.update();
            }
            else
            {
                Ebean.delete(basketProducts);
                this.products.remove(basketProducts);
            }
            // recalculate total price
            this.setTotalPrice(getTotalPrice() - product.getPrice());
        }
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }

    public void delete()
    {
        this.clearProducts();
        Ebean.update(this);
        Ebean.delete(this);
    }

    /**
     * removes all products from the basket
     */
    public void clearProducts()
    {
        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", this).findList();
        // delete each product
        for (Basket_Product basketProduct : basketProducts)
        {
            // delete all of this kind of product
            for (int i = 0; i < basketProduct.getCountProduct(); i++)
            {
                this.deleteProduct(basketProduct.getProduct());
            }
        }
    }
}
