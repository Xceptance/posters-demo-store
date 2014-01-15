package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "cart")
public class Cart
{

    /**
     * The id of the cart.
     */
    @Id
    private int id;

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

    public Cart()
    {
        this.products = new ArrayList<CartProduct>();
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
        return totalPrice;
    }

    public String getTotalPriceAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double temp = totalPrice;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
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

    public List<CartProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<CartProduct> products)
    {
        this.products = products;
    }

    /**
     * Adds the product with the given finish and size to the cart.
     * 
     * @param product
     * @param finish
     * @param size
     */
    public void addProduct(Product product, final String finish, final PosterSize size)
    {
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
     * Removes the given product from the cart.
     * 
     * @param cartProduct
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
     * removes all products from the cart
     */
    public void clearProducts()
    {
        // get all products of the cart
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        // delete each product
        for (CartProduct cartProduct : cartProducts)
        {
            cartProduct.delete();
            this.products.remove(cartProduct);
        }
        this.setTotalPrice(0);
    }

    public static Cart getCartById(int id)
    {
        return Ebean.find(Cart.class, id);
    }

    /**
     * Returns all carts, which are stored in the data base.
     * 
     * @return
     */
    public static List<Cart> getAllCarts()
    {
        return Ebean.find(Cart.class).findList();
    }

    /**
     * Creates and returns a new cart.
     * 
     * @return
     */
    public static Cart createNewCart()
    {
        // create new cart
        Cart cart = new Cart();
        // save cart
        cart.save();
        // get new cart by id
        Cart newCart = Ebean.find(Cart.class, cart.getId());
        // return new cart
        return newCart;
    }

    /**
     * Returns the total product count of the cart.
     * 
     * @return
     */
    public int getProductCount()
    {
        int productCount = 0;
        // get all products of the cart
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", this).findList();
        for (CartProduct cartProduct : cartProducts)
        {
            productCount += cartProduct.getProductCount();
        }
        return productCount;
    }

    public static CartProduct getCartProduct(final Cart cart, final Product product, final String finish,
                                             final PosterSize size)
    {
        return Ebean.find(CartProduct.class).where().eq("cart", cart).eq("product", product).eq("finish", finish)
                    .eq("size", size).findUnique();
    }
}
