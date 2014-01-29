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

/**
 * This {@link Entity} provides an order in the poster store. The order contains a list of {@link OrderProduct}s, a
 * shipping and a billing address and some information about the shipping costs, the tax and the total price of the
 * order.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "ordering")
public class Order
{

    /**
     * The {@link UUID} of the entity.
     */
    @Id
    private UUID id;

    /**
     * The date, the order was made.
     */
    private String orderDate;

    /**
     * The {@link ShippingAddress} of the order.
     */
    @ManyToOne
    private ShippingAddress shippingAddress;

    /**
     * The {@link BillingAddress} of the order.
     */
    @ManyToOne
    private BillingAddress billingAddress;

    /**
     * The shipping costs of the order.
     */
    private double shippingCosts;

    /**
     * The tax that will be added to the sub-total price.
     */
    private double tax;

    /**
     * The total costs of the order.
     */
    private double totalCosts;

    /**
     * The {@link CreditCard}, the order is paid with.
     */
    @ManyToOne
    private CreditCard creditCard;

    /**
     * The {@link Customer} of the order. Can be {@code null}, if the order was made by a guest.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private Customer customer;

    /**
     * The products of the order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderProduct> products;

    /**
     * The {@link Timestamp} of the entity.
     */
    @Version
    private Timestamp lastUpdate;

    /**
     * Constructor.
     */
    public Order()
    {
        this.products = new ArrayList<OrderProduct>();
    }

    /**
     * Returns the {@link UUID} of the entity.
     * 
     * @return the {@link UUID} of the entity
     */
    public UUID getId()
    {
        return id;
    }

    /**
     * Sets the {@link UUID} of the entity.
     * 
     * @param id
     *            the {@link UUID} of the entity
     */
    public void setId(UUID id)
    {
        this.id = id;
    }

    /**
     * Returns the {@link ShippingAddress} of the order.
     * 
     * @return the {@link ShippingAddress} of the order
     */
    public ShippingAddress getShippingAddress()
    {
        return shippingAddress;
    }

    /**
     * Sets the {@link ShippingAddress} of the order.
     * 
     * @param shippingAddress
     *            the {@link ShippingAddress} of the order
     */
    public void setShippingAddress(ShippingAddress shippingAddress)
    {
        this.shippingAddress = shippingAddress;
    }

    /**
     * Returns the {@link BillingAddress} of the order.
     * 
     * @return the {@link BillingAddress} of the order
     */
    public BillingAddress getBillingAddress()
    {
        return billingAddress;
    }

    /**
     * Sets the {@link BillingAddress} of the order.
     * 
     * @param billingAddress
     *            the {@link BillingAddress} of the order
     */
    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    /**
     * Returns the shipping costs of the order.
     * 
     * @return the shipping costs of the order
     */
    public double getShippingCosts()
    {
        return shippingCosts;
    }

    /**
     * Sets the shipping costs of the order.
     * 
     * @param shippingCosts
     *            the shipping costs of the order
     */
    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    /**
     * Returns the shipping costs of the order as a well formatted String.
     * 
     * @return the shipping costs of the order
     */
    public String getShippingCostsAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = shippingCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Returns the tax of the order.
     * 
     * @return the tax of the order
     */
    public double getTax()
    {
        return tax;
    }

    /**
     * Sets the tax of the order.
     * 
     * @param tax
     *            the tax of the order
     */
    public void setTax(double tax)
    {
        this.tax = tax;
    }

    /**
     * Returns the tax of the order as a String.
     * 
     * @return the tax of the order
     */
    public String getTaxAsString()
    {
        return String.valueOf(this.tax);
    }

    /**
     * Returns the total costs of the order.
     * 
     * @return the total costs of the order
     */
    public double getTotalCosts()
    {
        return totalCosts;
    }

    /**
     * Sets the total costs of the order.
     * 
     * @param totalCosts
     *            the total costs of the order
     */
    public void setTotalCosts(double totalCosts)
    {
        this.totalCosts = totalCosts;
    }

    /**
     * Returns the total costs of the order as a well formatted String.
     * 
     * @return
     */
    public String getTotalCostsAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Returns the {@link CreditCard} the order is paid with.
     * 
     * @return the {@link CreditCard} the order is paid with
     */
    public CreditCard getCreditCard()
    {
        return creditCard;
    }

    /**
     * Sets the {@link CreditCard} the order is paid with
     * 
     * @param creditCard
     *            the {@link CreditCard} the order is paid with
     */
    public void setCreditCard(CreditCard creditCard)
    {
        this.creditCard = creditCard;
    }

    /**
     * Returns the {@link Customer} of the order.
     * 
     * @return the {@link Customer} of the order
     */
    public Customer getCustomer()
    {
        return customer;
    }

    /**
     * Sets the {@link Customer} of the order.
     * 
     * @param customer
     *            the {@link Customer} of the order
     */
    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    /**
     * Returns the {@link OrderProduct}s of the order.
     * 
     * @return the {@link OrderProduct}s of the order
     */
    public List<OrderProduct> getProducts()
    {
        return products;
    }

    /**
     * Sets the {@link OrderProduct}s of the order.
     * 
     * @param products
     *            the {@link OrderProduct}s of the order
     */
    public void setProducts(List<OrderProduct> products)
    {
        this.products = products;
    }

    /**
     * Returns the date the order was made.
     * 
     * @return the date the order was made
     */
    public String getOrderDate()
    {
        return orderDate;
    }

    /**
     * Sets the date the order was made.
     * 
     * @param date
     *            the date the order was made
     */
    public void setOrderDate(String date)
    {
        this.orderDate = date;
    }

    /**
     * Returns the {@link Timestamp} of the entity.
     * 
     * @return the {@link Timestamp} of the entity
     */
    public Timestamp getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Sets the {@link Timestamp} of the entity.
     * 
     * @param lastUpdate
     *            the {@link Timestamp} of the entity
     */
    public void setLastUpdate(Timestamp lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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
     * Adds the tax to the total costs of the order.
     */
    public void addTaxToTotalCosts()
    {
        this.setTotalCosts(this.getTotalCosts() * this.getTax() + this.getTotalCosts());
    }

    /**
     * Adds the shipping costs to the total costs of the order.
     */
    public void addShippingCostsToTotalCosts()
    {
        this.setTotalCosts(this.getShippingCosts() + this.getTotalCosts());
    }

    /**
     * Adds the product with the given finish and size to the order.
     * 
     * @param product
     *            the {@link Product} to add
     * @param finish
     *            the finish of the Product
     * @param size
     *            the {@link PosterSize} of the product
     */
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
            orderProduct.save();
            products.add(orderProduct);
        }
        // this product is in the order at least one time
        else
        {
            // increment the count of this product
            orderProduct.incProductCount();
            orderProduct.update();
        }
        // recalculate total costs
        this.setTotalCosts(getTotalCosts() + orderProduct.getPrice());
    }

    /**
     * Adds all products from the given cart to the order.
     * 
     * @param cart
     *            the {@link Cart} which will be ordered
     */
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
     *            the {@link UUID} of the order
     * @return the order with the given ID
     */
    public static Order getOrderById(UUID id)
    {
        return Ebean.find(Order.class, id);
    }

    /**
     * Creates and returns a new order.
     * 
     * @return a new {@link Order}.
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
