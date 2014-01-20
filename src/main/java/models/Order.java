package models;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "ordering")
public class Order
{

    @Id
    private UUID id;

    private String orderDate;

    @ManyToOne
    private ShippingAddress shippingAddress;

    @ManyToOne
    private BillingAddress billingAddress;

    private double shippingCosts;

    private double tax;

    private double totalCosts;

    @ManyToOne
    private CreditCard creditCard;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderProduct> products;

    @Version
    private Timestamp lastUpdate;

    public Order()
    {
        this.products = new ArrayList<OrderProduct>();
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    public ShippingAddress getShippingAddress()
    {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress)
    {
        this.shippingAddress = shippingAddress;
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
        return shippingCosts;
    }

    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public String getShippingCostsAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = shippingCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    public double getTax()
    {
        return tax;
    }

    public void setTax(double tax)
    {
        this.tax = tax;
    }

    public String getTaxAsString()
    {
        return String.valueOf(this.tax);
    }

    public double getTotalCosts()
    {
        return totalCosts;
    }

    public void setTotalCosts(double totalCosts)
    {
        this.totalCosts = totalCosts;
    }

    public String getTotalCostsAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
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

    public List<OrderProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<OrderProduct> products)
    {
        this.products = products;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String date)
    {
        this.orderDate = date;
    }

    public Timestamp getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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

    private void addProduct(Product product, String finish, PosterSize size)
    {
        OrderProduct orderProduct = Ebean.find(OrderProduct.class).where().eq("order", this).eq("product", product)
                                         .eq("finish", finish).eq("size", size).findUnique();
        // this product is not in the order
        if (orderProduct == null)
        {
            // add product to order
            orderProduct = new OrderProduct();
            orderProduct.setOrder(this);
            orderProduct.setProduct(product);
            // set product count to one
            orderProduct.setProductCount(1);
            // set finish
            orderProduct.setFinish(finish);
            // set size
            orderProduct.setSize(size);
            // set price
            orderProduct.setPrice(Ebean.find(ProductPosterSize.class).where().eq("product", product).eq("size", size)
                                       .findUnique().getPrice());
            Ebean.save(orderProduct);
            products.add(orderProduct);
        }
        // this product is in the order at least one time
        else
        {
            // increment the count of this product
            orderProduct.incProductCount();
            Ebean.update(orderProduct);
        }
        // recalculate total costs
        this.setTotalCosts(getTotalCosts() + orderProduct.getPrice());
    }

    public void addProductsFromCart(Cart cart)
    {
        // get all products from cart
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // for each product
        for (CartProduct cartProduct : cartProducts)
        {
            // add the product to the order
            for (int i = 0; i < cartProduct.getProductCount(); i++)
            {
                this.addProduct(cartProduct.getProduct(), cartProduct.getFinish(), cartProduct.getSize());
            }
        }
    }

    /**
     * Returns the order by the order's id.
     * 
     * @param id
     * @return
     */
    public static Order getOrderById(UUID id)
    {
        return Ebean.find(Order.class, id);
    }

    /**
     * Creates and returns a new order.
     * 
     * @return
     */
    public static Order createNewOrder()
    {
        // create new order
        Order order = new Order();
        // save order
        order.save();
        // get new order by id
        Order newOrder = Ebean.find(Order.class, order.getId());
        // return new order
        return newOrder;
    }
}
