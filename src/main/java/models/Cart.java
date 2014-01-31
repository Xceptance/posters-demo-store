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
     * The products which are in the cart.
     */
    @OneToMany(mappedBy = "cart")
    private List<CartProduct> products;

    /**
     * The total price of the cart.
     */
    private double totalPrice;

    /**
     * The constructor.
     */
    public Cart()
    {
        this.products = new ArrayList<CartProduct>();
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
    public void setId(UUID id)
    {
        this.id = id;
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
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalPrice;
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
    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
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
     * Sets the customer of the cart.
     * 
     * @param customer
     *            the customer of the cart
     */
    public void setCustomer(Customer customer)
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
    public void setProducts(List<CartProduct> products)
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
    public void addProduct(Product product, final String finish, final PosterSize size)
    {
        // check, whether the product with the given finish and size is in the cart
        CartProduct cartProduct = Ebean.find(CartProduct.class).where().eq("cart", this).eq("product", product)
                                       .eq("finish", finish).eq("size", size).findUnique();
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
            cartProduct.setPrice(Ebean.find(ProductPosterSize.class).where().eq("product", product).eq("size", size)
                                      .findUnique().getPrice());
            cartProduct.save();
            products.add(cartProduct);
        }
        // the product is in the cart
        else
        {
            // increment the count of the product
            cartProduct.incProductCount();
        }
        // recalculate total price
        this.setTotalPrice(getTotalPrice() + cartProduct.getPrice());
        this.update();
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
            this.products.remove(cartProduct);
        }
        // recalculate total price
        this.setTotalPrice(getTotalPrice() - cartProduct.getPrice());
        this.update();
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
        this.clearProducts();
        // refresh to prevent foreign key violation
        this.update();
        // finally delete the cart
        Ebean.delete(this);
    }

    /**
     * Removes all products from the cart
     */
    public void clearProducts()
    {
        // get all products of the cart
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        // remove each product
        for (CartProduct cartProduct : cartProducts)
        {
            cartProduct.delete();
            this.products.remove(cartProduct);
        }
        // adjust total price
        this.setTotalPrice(0);
    }

    /**
     * Returns the {@link Cart} with the given {@link UUID}. Returns {@code null}, if no cart was found.
     * 
     * @param id
     *            the {@link UUID} of the cart
     * @return the {@link Cart} that matches the unique id
     */
    public static Cart getCartById(UUID id)
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
     * Creates and returns a new {@link Cart}.
     * 
     * @return a new {@link Cart}
     */
    public static Cart createNewCart()
    {
        // create new cart
        Cart cart = new Cart();
        // save cart
        cart.save();
        // get new cart by id
        Cart newCart = Cart.getCartById(cart.getId());
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
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        // get the count of each product
        for (CartProduct cartProduct : cartProducts)
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
        return Ebean.find(CartProduct.class).where().eq("cart", this).eq("product", product).eq("finish", finish)
                    .eq("size", size).findUnique();
    }
}
