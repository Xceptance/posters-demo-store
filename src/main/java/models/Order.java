package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "ordering")
public class Order
{

    @Id
    private int id;

    private String date;

    @ManyToOne(cascade = CascadeType.ALL)
    private DeliveryAddress deliveryAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private BillingAddress billingAddress;

    private double shippingCosts;

    private double tax;

    private double totalCosts;

    @ManyToOne(cascade = CascadeType.ALL)
    private CreditCard creditCard;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<Order_Product> products;

    public Order()
    {
        this.products = new ArrayList<Order_Product>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DeliveryAddress getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }

    public BillingAddress getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public double getShippingCosts()
    {
        double temp = shippingCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return temp;
    }

    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public double getTax()
    {
        return tax;
    }

    public void setTax(double tax)
    {
        this.tax = tax;
    }

    public double getTotalCosts()
    {
        double temp = totalCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return temp;
    }

    public void setTotalCosts(double totalCosts)
    {
        this.totalCosts = totalCosts;
    }

    public CreditCard getCreditCard()
    {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard)
    {
        this.creditCard = creditCard;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public List<Order_Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Order_Product> products)
    {
        this.products = products;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }

    public void addTaxToTotalCosts()
    {
         this.setTotalCosts(this.getTotalCosts() * this.getTax() + this.getTotalCosts());
    }
    
    public void addShippingCostsToTotalCosts()
    {
         this.setTotalCosts(this.getShippingCosts() + this.getTotalCosts());
    }

    private void addProduct(Product product)
    {
        Order_Product orderProducts = Ebean.find(Order_Product.class).where().eq("order", this).eq("product", product)
                                           .findUnique();
        // this product is not in the order
        if (orderProducts == null)
        {
            // add product to order
            orderProducts = new Order_Product();
            orderProducts.setOrder(this);
            orderProducts.setProduct(product);
            // set product count to one
            orderProducts.setCountProduct(1);
            Ebean.save(orderProducts);
            products.add(orderProducts);

        }
        // this product is in the order at least one time
        else
        {
            // increment the count of this product
            orderProducts.incCountProduct();
            Ebean.update(orderProducts);
        }
        // recalculate total costs
        this.setTotalCosts(getTotalCosts() + product.getPrice());
    }

    public void addProductsFromBasket(Basket basket)
    {
        // get all products from basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // for each product
        for (Basket_Product orderProduct : basketProducts)
        {
            // add the product to the order
            for (int i = 0; i < orderProduct.getCountProduct(); i++)
            {
                this.addProduct(orderProduct.getProduct());
            }
        }
    }
}
