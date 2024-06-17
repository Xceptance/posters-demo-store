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

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import io.ebean.Ebean;


import io.ebean.annotation.DbForeignKey;

/**
 * This {@link Entity} provides an order in the poster demo store. The order contains a list of {@link OrderProduct}s, a
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

    //  /**
    //  * The date, the order was made.
    //  */
    // private String customerIdd;

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
     * The sub total costs of the order.
     */
    private double subTotalCosts;

    /**
     * The total tax costs of the order.
     */
    private double totalTaxCosts;

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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @DbForeignKey(noConstraint = true)
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
     * Order Status: Can be Pending, Failed and Successful
     */
    private String orderStatus;

    /**
     * Constructor.
     */
    public Order()
    {
        products = new ArrayList<OrderProduct>();
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
    public void setId(final UUID id)
    {
        this.id = id;
    }

    //     /**
    //  * Returns the {@link ShippingAddress} of the order.
    //  * 
    //  * @return the {@link ShippingAddress} of the order
    //  */
    // public String getcustomerIdd()
    // {
    //     return customerIdd;
    // }

    // /**
    //  * Sets the {@link ShippingAddress} of the order.
    //  * 
    //  * @param shippingAddress
    //  *            the {@link ShippingAddress} of the order
    //  */
    // public void setcustomerIdd(final String customerIdd)
    // {
    //     this.customerIdd = customerIdd;
    // }

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
    public void setShippingAddress(final ShippingAddress shippingAddress)
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
    public void setBillingAddress(final BillingAddress billingAddress)
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
     * Returns Sub Total Costs of the order as a well formatted String.
     * 
     * @return the sub total costs of the order
     */
    public String getSubTotalCostsAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = subTotalCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * @return the subTotalCosts
     */
    public double getSubTotalCosts()
    {
        return subTotalCosts;
    }

    /**
     * @param subTotalCosts
     *            the subTotalCosts to set
     */
    public void setSubTotalCosts(double subTotalCosts)
    {
        this.subTotalCosts = subTotalCosts;

    }

    /**
     * @return the totalTaxCosts
     */
    public double getTotalTaxCosts()
    {
        // check totalTaxCosts
        if (totalTaxCosts > 0)
        {
            return totalTaxCosts;
        }
        else
            return 0;
    }

    /**
     * @param totalTaxCosts
     *            the totalTaxCosts to set
     */
    public void setTotalTaxCosts(double totalTaxCosts)
    {
        this.totalTaxCosts = totalTaxCosts;
    }

    /**
     * Sets the shipping costs of the order.
     * 
     * @param shippingCosts
     *            the shipping costs of the order
     */
    public void setShippingCosts(final double shippingCosts)
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
        final DecimalFormat f = new DecimalFormat("#0.00");
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
    public void setTax(final double tax)
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
        // return String.valueOf(tax);
        return String.valueOf(tax * 100);
    }

    /**
     * Returns Sub Total Tax Costs of the order as a well formatted String.
     * 
     * @return the sub total tax costs of the order
     */
    public String getTotalTaxCostsAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalTaxCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
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
    public void setTotalCosts(final double totalCosts)
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
        final DecimalFormat f = new DecimalFormat("#0.00");
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
    public void setCreditCard(final CreditCard creditCard)
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
    public void setCustomer(final Customer customer)
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
    public void setProducts(final List<OrderProduct> products)
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
    public void setOrderDate(final String date)
    {
        orderDate = date;
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
    public void setLastUpdate(final Timestamp lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Returns the date the order was made.
     * 
     * @return the date the order was made
     */
    public String getOrderStatus()
    {
        return orderStatus;
    }

    /**
     * Sets the date the order was made.
     * 
     * @param date
     *            the date the order was made
     */
    public void setOrderStatus(final String status)
    {
        orderStatus = status;
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
     * Adds the product with the given finish and size to the order.
     * 
     * @param product
     *            the {@link Product} to add
     * @param finish
     *            the finish of the Product
     * @param size
     *            the {@link PosterSize} of the product
     */
    private void addProduct(final Product product, final String finish, final PosterSize size)
    {
        OrderProduct orderProduct = Ebean.find(OrderProduct.class).where().eq("order", this).eq("product", product).eq("finish", finish)
                                         .eq("size", size).findOne();
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
            orderProduct.setPrice(Ebean.find(ProductPosterSize.class).where().eq("product", product).eq("size", size).findOne()
                                       .getPrice());
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
        // recalculate subTotalCosts
        setSubTotalCosts(getSubTotalCosts() + orderProduct.getPrice());
        // recalculate totalTaxCosts
        setTotalTaxCosts((getSubTotalCosts() + getShippingCosts()) * getTax());
        // recalculate total costs
        setTotalCosts(getSubTotalCosts() + getTotalTaxCosts() + getShippingCosts());
    }

    /**
     * Adds all products from the given cart to the order.
     * 
     * @param cart
     *            the {@link Cart} which will be ordered
     */
    public void addProductsFromCart(final Cart cart)
    {
        // get all products from cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // for each product
        for (final CartProduct cartProduct : cartProducts)
        {
            // add the product to the order
            for (int i = 0; i < cartProduct.getProductCount(); i++)
            {
                addProduct(cartProduct.getProduct(), cartProduct.getFinish(), cartProduct.getSize());
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
    public static Order getOrderById(final UUID id)
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
        final Order order = new Order();
        // save order
        order.save();
        // get new order by id
        final Order newOrder = Ebean.find(Order.class, order.getId());
        // return new order
        return newOrder;
    }

    /***
     * Creates and returns a new order with parameter
     * @param tax
     * @param shippingCosts
     * @return
     */
    public static Order createNewOrder(final double tax,final double shippingCosts)
    {
        // create new order
        final Order order = new Order();
        
        // start value = 0
        order.setSubTotalCosts(0);
        order.setTotalCosts(0);
        
        //set default tax
        order.setTax(tax);
        
        //set default shipping costs
        order.setShippingCosts(shippingCosts);
        
        // save order
        order.save();
        // get new order by id
        final Order newOrder = Ebean.find(Order.class, order.getId());
        // return new order
        return newOrder;
    }

    /**
     * Returns all {@link Order}s, which are stored in the database.
     * 
     * @return {@link Order}s
     */
    public static List<Order> getEveryOrder()
    {
        return Ebean.find(Order.class).findList();
    }

     /**
     * Deletes pending orders that are older than one day.
     */
    public static void deleteOldPendingOrders() {
        // Calculate the timestamp for one day ago
        long oneDayAgoTimestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);
        
        // Convert timestamp to Date
        Date oneDayAgo = new Date(oneDayAgoTimestamp);

        // Get all orders that are pending and have creation time more than one day ago from the database irrespective of customer. 
        // Delete those orders.
        Ebean.deleteAll(Ebean.find(Order.class)
        .where().eq("orderStatus", "Pending").lt("lastUpdate", oneDayAgo).findList());
    }

}