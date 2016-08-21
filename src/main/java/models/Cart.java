package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} provides a shopping cart. Each customer of the demo poster store has its own cart. A cart
 * contains a list of {@link CartProduct}s.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "cart")
public class Cart
{

    /**
     * The {@link UUID} of the entity.
     */
    @Id
    private UUID id;

    /**
     * The customer of the cart.
     */
    @OneToOne
    private Customer customer;

    /**
     * The shipping costs of the order.
     */
    private double shippingCosts;

    /**
     * The sub total price of the order.
     */
    private double subTotalPrice;

    /**
     * The tax that will be added to the sub-total price.
     */
    private double tax;

    /**
     * The total tax price of the order.
     */
    private double totalTaxPrice;

    /**
     * The total price of the cart.
     */
    private double totalPrice;

    /**
     * The products which are in the cart.
     */
    @OneToMany(mappedBy = "cart")
    private List<CartProduct> products;

    /**
     * The constructor.
     */
    public Cart()
    {
        products = new ArrayList<CartProduct>();
    }

    /**
     * Returns the cart {@link UUID}.
     * 
     * @return cart ID
     */
    public UUID getId()
    {
        return id;
    }

    /**
     * Sets the cart id.
     * 
     * @param id
     *            {@link UUID} of the cart.
     */
    public void setId(final UUID id)
    {
        this.id = id;
    }

    /**
     * @return the shippingCosts
     */
    public double getShippingCosts()
    {
        return shippingCosts;
    }

    /**
     * @param shippingCosts
     *            the shippingCosts to set
     */
    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    /**
     * Returns the shipping costs of the cart as a well formatted String.
     * 
     * @return the shipping costs of the cart
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
     * @return the subTotalPrice
     */
    public double getSubTotalPrice()
    {
        return subTotalPrice;
    }

    /**
     * Returns Sub Total Price of the cart as a well formatted String.
     * 
     * @return the sub total price of the cart
     */
    public String getSubTotalPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = getSubTotalPrice();
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * @param subTotalPrice
     *            the subTotalPrice to set
     */
    public void setSubTotalPrice(double subTotalPrice)
    {
        this.subTotalPrice = subTotalPrice;
    }

    /**
     * @return the tax
     */
    public double getTax()
    {
        return tax;
    }

    /**
     * Returns tax as a well formatted String.
     * 
     * @return the tax
     */
    public String getTaxAsString()
    {
        return String.valueOf(tax * 100);
    }

    /**
     * @param tax
     *            the tax to set
     */
    public void setTax(double tax)
    {
        this.tax = tax;
    }

    /**
     * @return the totalTaxPrice
     */
    public double getTotalTaxPrice()
    {
        // check totalTaxPrice
        if (totalTaxPrice > 0)
        {
            return totalTaxPrice;
        }
        else
            return -1;
    }

    /**
     * Returns Sub Total Tax Price of the cart as a well formatted String.
     * 
     * @return the sub total tax price of the cart
     */
    public String getTotalTaxPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalTaxPrice;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * @param totalTaxPrice
     *            the totalTaxPrice to set
     */
    public void setTotalTaxPrice(double totalTaxPrice)
    {
        this.totalTaxPrice = totalTaxPrice;
    }

    public void calculateTotalTaxPrice()
    {
        setTotalTaxPrice(getTax() * getSubTotalPrice());
    }

    /**
     * Returns the total price of all products, which are in the cart.
     * 
     * @return total price of the cart
     */
    public double getTotalPrice()
    {
        return totalPrice;
    }

    /**
     * Returns the total price of all products, which are stored in the database.
     * 
     * @return total price of the cart in a well formatted {@link String}.
     */
    public String getTotalPriceAsString()
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalPrice;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Returns the total price of all products with tax, shippingCosts.
     * 
     * @param subTotalPrice
     * @param tax
     * @param shippingCosts
     * @return
     */
    public String getTotalPriceAsString(double subTotalPrice, double tax, double shippingCosts)
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = subTotalPrice + (subTotalPrice * tax) + shippingCosts;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }

    /**
     * Sets the total price of the cart.
     * 
     * @param totalPrice
     *            the total price of the cart
     */
    public void setTotalPrice(final double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    /**
     * Calculate the Total Price.
     */
    public void calculateTotalPrice()
    {
        setTotalPrice(getSubTotalPrice() + getTotalTaxPrice() + getShippingCosts());
    }

    /**
     * Returns the customer of the cart.
     * 
     * @return customer of the cart
     */
    public Customer getCustomer()
    {
        return customer;
    }

    /**
     * Sets the customer of the cart.   "0.00"
     * 
     * @param customer
     *            the customer of the cart
     */
    public void setCustomer(final Customer customer)
    {
        this.customer = customer;
    }

    /**
     * Returns all {@link CartProduct}s of this cart.
     * 
     * @return all {@link CartProduct}s of the cart
     */
    public List<CartProduct> getProducts()
    {
        return products;
    }

    /**
     * Sets the {@link CartProduct}s of the cart.
     * 
     * @param products
     *            the {@link CartProduct}s of the cart
     */
    public void setProducts(final List<CartProduct> products)
    {
        this.products = products;
    }

    /**
     * Adds the product with the given finish and size to the cart.
     * 
     * @param product
     *            the {@link Product} which is to be added
     * @param finish
     *            the finish of the product
     * @param size
     *            the {@link PosterSize} of the product
     */
    public void addProduct(final Product product, final String finish, final PosterSize size)
    {
        // check, whether the product with the given finish and size is in the cart
        CartProduct cartProduct = Ebean.find(CartProduct.class).where().eq("cart", this).eq("product", product).eq("finish", finish)
                                       .eq("size", size).findUnique();
        
        // the product is not in the cart
        if (cartProduct == null)
        {
            // add product to cart
            cartProduct = new CartProduct();
            cartProduct.setCart(this);
            cartProduct.setProduct(product);
            // set product count to one
            cartProduct.setProductCount(1);
            // set finish
            cartProduct.setFinish(finish);
            // set size
            cartProduct.setSize(size);
            // set price of the product
            cartProduct.setPrice(Ebean.find(ProductPosterSize.class).where().eq("product", product).eq("size", size).findUnique()
                                      .getPrice());
            cartProduct.save();
            products.add(cartProduct);
        }
        // the product is in the cart
        else
        {
            // increment the count of the product
            cartProduct.incProductCount();
        }

        // add product price to SubTotalPrice
        setSubTotalPrice(getSubTotalPrice() + (cartProduct.getPrice()));

        // recalculate TotalTaxPrice, TotalPrice
        calculateTotalTaxPrice();
        calculateTotalPrice();

        // update values in db
        update();
    }
 
    /**
     * Removes one of the product from the cart.
     * 
     * @param cartProduct
     *            the {@link CartProduct} which is to be removed from the cart
     */
    public void removeProduct(final CartProduct cartProduct)
    {
        // product is in the cart more than once
        if (cartProduct.getProductCount() > 1)
        {
            // decrement the count of the product
            cartProduct.decrementProductCount();
        }
        else
        {
            cartProduct.delete();
            products.remove(cartProduct);
        }
        // recalculate total price
        setSubTotalPrice(getSubTotalPrice() - cartProduct.getPrice());
        calculateTotalTaxPrice();
        calculateTotalPrice();
        update();
    }

    /**
     * Updates the {@link Cart} in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the {@link Cart} in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Deletes the {@link Cart} from the database.
     */
    public void delete()
    {
        // remove all products from the cart
        clearProducts();
        // refresh to prevent foreign key violation
        update();
        // finally delete the cart
        Ebean.delete(this);
    }

    /**
     * Removes all products from the cart
     */
    public void clearProducts()
    {
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        // remove each product
        for (final CartProduct cartProduct : cartProducts)
        {
            cartProduct.delete();
            products.remove(cartProduct);
        }
        // adjust price
        setSubTotalPrice(0);
        setTotalTaxPrice(0);
        setTotalPrice(0);
    }

    /**
     * Returns the {@link Cart} with the given {@link UUID}. Returns {@code null}, if no cart was found.
     * 
     * @param id
     *            the {@link UUID} of the cart
     * @return the {@link Cart} that matches the unique id
     */
    public static Cart getCartById(final UUID id)
    {
        return Ebean.find(Cart.class, id);
    }

    /**
     * Returns all {@link Cart}s, which are stored in the database.
     * 
     * @return {@link Cart}s
     */
    public static List<Cart> getAllCarts()
    {
        return Ebean.find(Cart.class).findList();
    }

    /**
     * Creates and returns a new cart.
     * 
     * @return a new {@link Cart}
     */
    public static Cart createNewCart()
    {
        // create new cart
        final Cart cart = new Cart();

        // save cart
        cart.save();
        // get new cart by id
        final Cart newCart = Cart.getCartById(cart.getId());
        // return new cart
        return newCart;
    }
    /**
     * Creates and returns a new cart with parameter.
     * 
     * @return a new {@link Cart}
     */
    public static Cart createNewCart(final double tax, final double shippingCosts)
    {
        // create new cart
        final Cart cart = new Cart();

        // start value = 0
        cart.setSubTotalPrice(0);
        cart.setTotalPrice(0);
        cart.setTotalTaxPrice(0);

        // set default tax
        cart.setTax(tax);

        // set default shipping costs
        cart.setShippingCosts(shippingCosts);

        // save cart
        cart.save();
        // get new cart by id
        final Cart newCart = Cart.getCartById(cart.getId());
        // return new cart
        return newCart;
    }

    /**
     * Returns the total product count of the cart.
     * 
     * @return total count of products
     */
    public int getProductCount()
    {
        int productCount = 0;
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        // get the count of each product
        for (final CartProduct cartProduct : cartProducts)
        {
            // add the count of the current product to total count
            productCount += cartProduct.getProductCount();
        }
        return productCount;
    }

    /**
     * Returns a {@link CartProduct}, which is in the cart. The {@link CartProduct} is specified by the given
     * parameters. Returns {@code null}, if no {@link CartProduct} was found, that matches the given parameters.
     * 
     * @param product
     *            the {@link Product} which is in the cart
     * @param finish
     *            the finish of the product
     * @param size
     *            the {@link PosterSize} of the product
     * @return a {@link CartProduct}, that matches the given parameters, or {@code null}, if no {@link CartProduct}
     *         matches
     */
    public CartProduct getCartProduct(final Product product, final String finish, final PosterSize size)
    {
        return Ebean.find(CartProduct.class).where().eq("cart", this).eq("product", product).eq("finish", finish).eq("size", size)
                    .findUnique();
    }
}
