package controllers_backoffice;

import java.util.Optional;
import java.util.List;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.PagingList;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionUserExistFilter;
import models_backoffice.User;
import models_backoffice.Statistic;
import models.Order;
import models.Customer;
import models.Product;
import models.SubCategory;
import models.TopCategory;
import models.PosterSize;
import models.ProductPosterSize;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.Params;
import ninja.params.PathParam;
import util.session.SessionHandling;

/**
 * Backoffice Controller, it controls the behaviour of the backoffice. Must be signed in as a backoffice user in order
 * to access any pages within the backoffice.
 * 
 * @author kennygozali
 */

public class BackofficeController
{
    @Inject
    PosterConstants xcpConf;

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns the homepage of the Backoffice.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result homepage(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<User> users = Ebean.find(User.class).findList();
        // Add all users into the back office
        result.render("users", users);
        return result;
    }

    /**
     * Returns the view of a single admin user.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userView(final Context context, @PathParam("userId") String userId)
    {

        Result result = Results.html();

        // Find user with the id from the params
        User user = Ebean.find(User.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Edit an admin user's information and credentials.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userEdit(final Context context, @PathParam("userId") String userId)
    {

        Result result = Results.html();

        // Find user with the id from the params
        User user = Ebean.find(User.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Persist the updated user into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userEditComplete(final Context context, @PathParam("userId") String userId, @Param("name") final String name,
                                   @Param("firstName") final String firstName, @Param("email") final String email,
                                   @Param("password") final String password)
    {

        Result result = Results.html();

        // Find user with the id from the params
        User user = Ebean.find(User.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        boolean failure = false;
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            failure = true;
        }
        if (failure)
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user/" + userId + "/edit");
        }
        // all input fields might be correct
        else
        {
            user.setName(name);
            user.setFirstName(firstName);
            user.setEmail(email);
            user.hashPasswd(password);
            // save user
            user.save();
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }
    }

    /**
     * Deletes an admin user.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userDelete(final Context context, @PathParam("userId") String userId)
    {

        Result result = Results.html();

        // Find user with the id from the params
        User user = Ebean.find(User.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Don't delete if it is the same as the user
        if (currentUser.getEmail() == user.getEmail())
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice");

        }
        else
        {
            user.delete();
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }

    }

    /**
     * List out all of the admin users.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result userList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<User> users = Ebean.find(User.class).findList();
        // Add all users into the back office
        result.render("users", users);

        return result;
    }

    /**
     * Shows the statistics of the poster website.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result statistics(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Returns the view of an order.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result orderView(final Context context, @PathParam("orderId") String orderId)
    {

        Result result = Results.html();

        // Find order with the id from the params
        Order order = Ebean.find(Order.class, orderId);
        // Render order into template
        result.render("order", order);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * List out all of the orders.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result orderList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the orders
        List<Order> orders = Ebean.find(Order.class).findList();
        // Add orders into the back office
        result.render("orders", orders);

        return result;
    }

    /**
     * List out the catalog.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result catalog(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find topCategories with the id from the params
        List<TopCategory> topCategories = Ebean.find(TopCategory.class).findList();
        // Render topCategories into template
        result.render("topCategories", topCategories);

        return result;
    }

    /**
     * Returns the view of a single product.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productView(final Context context, @PathParam("productId") String productId)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Edit a product's information.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productEdit(final Context context, @PathParam("productId") String productId)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find subCategories with the id from the params
        List<SubCategory> subCategories = Ebean.find(SubCategory.class).findList();
        // Render subCategories into template
        result.render("subCategories", subCategories);

        // Find topCategories with the id from the params
        List<TopCategory> topCategories = Ebean.find(TopCategory.class).findList();
        // Render topCategories into template
        result.render("topCategories", topCategories);

        // Find all available sizes
        List<PosterSize> availableSizes = Ebean.find(PosterSize.class).findList();
        result.render("availableSizes", availableSizes);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Persist the updated product into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productEditComplete(final Context context, @PathParam("productId") String productId, @Param("name") final String name,
                                      @Param("imageURL") final String imageURL, @Param("minimumPrice") final double minimumPrice,
                                      @Param("descriptionDetail") final String descriptionDetail,
                                      @Param("descriptionOverview") final String descriptionOverview,
                                      @Param("subCategory") final String subCategoryName,
                                      @Param("topCategory") final String topCategoryName,
                                      @Params("availableSizes") final Integer[] posterSizesId)
    {

        Result result = Results.html();

        // Find product with the id from the params
        Product product = Ebean.find(Product.class, productId);
        // Render product into template
        result.render("product", product);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find subcategory with the subcategory name from the params
        SubCategory subCategory = Ebean.find(SubCategory.class).where().eq("name", subCategoryName).findUnique();

        // Find subcategory with the subcategory name from the params
        TopCategory topCategory = Ebean.find(TopCategory.class).where().eq("name", topCategoryName).findUnique();

        // Delete all linking element product - poster sizes
        List<ProductPosterSize> currentAvailableSizes = product.getAvailableSizes();
        Ebean.delete(currentAvailableSizes);

        // LINK between product and poster sizes
        ProductPosterSize currentSize = null;
        for (int i = 0; i < posterSizesId.length; i++)
        {
            currentSize = new ProductPosterSize();
            currentSize.setProduct(product);
            currentSize.setPrice(minimumPrice);
            currentSize.setSize(Ebean.find(PosterSize.class, posterSizesId[i]));
            currentSize.save();
        }
        // Update product available sizes
        // product.setAvailableSizes(availableSizes);

        // all input fields might be correct

        product.setName(name);
        product.setImageURL(imageURL);
        product.setMinimumPrice(minimumPrice);
        product.setDescriptionDetail(descriptionDetail);
        product.setDescriptionOverview(descriptionOverview);
        product.setSubCategory(subCategory);
        product.setTopCategory(topCategory);
        // save product
        product.save();
        // show page to log-in
        return Results.redirect(context.getContextPath() + "/posters/backoffice/product/" + productId);

    }

    /**
     * List out all of the products.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productList(final Context context)
    {
        Result result = Results.html();
        int pageSize = 10;

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        Query<Product> query = Ebean.find(Product.class);

        // Find total products and page for the pagination
        int totalProductsCount = query.findRowCount();
        int numberOfPage = totalProductsCount / pageSize;
        result.render("numberOfPage", numberOfPage);

        // Find all of the products
        List<Product> products = Ebean.find(Product.class).findList();
        // Add products into the back office
        result.render("products", products);

        return result;
    }

    /**
     * List out all of the products.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result productPage(final Context context, @PathParam("pageNumber") int pageNumber)
    {
        Result result = Results.html();
        int pageSize = 10;

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        Query<Product> query = Ebean.find(Product.class);

        // Find total products and page for the pagination
        int totalProductsCount = query.findRowCount();
        int numberOfPage = totalProductsCount / pageSize;
        result.render("numberOfPage", numberOfPage);
        // Find paged product
        PagingList<Product> pagingList = query.findPagingList(pageSize);
        List<Product> products = pagingList.getPage(pageNumber - 1).getList();
        // Add products into the back office
        result.render("products", products);

        // Add Page Number as logic value to render in freemarker
        result.render("pageNumber", pageNumber);

        return result.template("views_backoffice/BackofficeController/productList.ftl.html");
    }

    /**
     * Returns the view of a customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerView(final Context context, @PathParam("customerId") String customerId)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Edit a customer's information and credentials.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerEdit(final Context context, @PathParam("customerId") String customerId)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Persist the updated customer into the database.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerEditComplete(final Context context, @PathParam("customerId") String customerId, @Param("name") final String name,
                                       @Param("firstName") final String firstName, @Param("email") final String email,
                                       @Param("password") final String password)
    {

        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        boolean failure = false;
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            failure = true;
        }
        if (failure)
        {
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user/" + customerId + "/edit");
        }
        // all input fields might be correct
        else
        {
            customer.setName(name);
            customer.setFirstName(firstName);
            customer.setEmail(email);
            customer.hashPasswd(password);
            // save customer
            customer.save();
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/posters/backoffice");
        }
    }

    /**
     * List out all of the customers.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result customerList(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the customers
        List<Customer> customers = Ebean.find(Customer.class).findList();
        // Add customers into the back office
        result.render("customers", customers);

        return result;
    }

    /**
     * Returns the search list.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result searchList(final Context context)
    {

        String searchQuery = context.getParameter("searchQuery");
        String searchType = context.getParameter("searchType");

        Result result = Results.html();
        result.render("searchType", searchType);

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        
        // check for search type
        if (searchType.equals("Product"))
        {
            List<Product> products = Ebean.find(Product.class).where().icontains("name", searchQuery).findList();
            result.render("products", products);
        }
        else if (searchType.equals("Customer"))
        {
            List<Customer> customers = Ebean.find(Customer.class).where().icontains("firstName", searchQuery).findList();
            result.render("customers", customers);
        }
        else if (searchType.equals("User"))
        {
            List<User> users = Ebean.find(User.class).where().icontains("firstName", searchQuery).findList();
            result.render("users", users);
        }
        return result;
    }

    /**
     * Creates the preference page.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result preferences(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Controls the import/export operations within the backoffice.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result dataManagement(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * This response appears when a backoffice user inputted an invalid backoffice url.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result Error404(final Context context)
    {
        Result result = Results.html();

        // Find current user
        User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    /**
     * Helps with providing the JSON data for the statistics within the javascript file. It is used in the statistics page.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionUserExistFilter.class)
    public Result statisticsJSON(final Context context)
    {
        Statistic statistic = new Statistic();

        // Find the admin user amount
        Integer adminUserAmount = Ebean.find(User.class).findList().size();
        statistic.setAdminUserAmount(adminUserAmount);
        // Find customer amount
        Integer customerAmount = Ebean.find(Customer.class).findList().size();
        statistic.setCustomerAmount(customerAmount);
        // Find order amount
        Integer orderAmount = Ebean.find(Order.class).findList().size();
        statistic.setOrderAmount(orderAmount);
        // Find sub category amount
        Integer subCategoryAmount = Ebean.find(SubCategory.class).findList().size();
        statistic.setSubCategoryAmount(subCategoryAmount);
        // Find top category amount
        Integer topCategoryAmount = Ebean.find(TopCategory.class).findList().size();
        statistic.setTopCategoryAmount(topCategoryAmount);
        return Results.json().render(statistic);
    }
}
