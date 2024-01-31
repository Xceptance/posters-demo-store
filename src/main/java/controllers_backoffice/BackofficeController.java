package controllers_backoffice;

import java.util.Optional;
import java.util.UUID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import controllers.WebShopController;
import filters.SessionUserExistFilter;
import models_backoffice.Backofficeuser;
import models_backoffice.Statistic;
import models.Order;
import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.Product;
import models.SubCategory;
import models.TopCategory;
import models.PosterSize;
import models.ProductPosterSize;
import models.ShippingAddress;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
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
    Messages msg;
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<Backofficeuser> users = Ebean.find(Backofficeuser.class).findList();
        // Add all users into the back office
        result.render("users", users);
        return result;
    }
    
    //  Homepage BackOffice ADmin LTE

    @FilterWith(SessionUserExistFilter.class)
    public Result homepageN(final Context context)
    {
        Result result = Results.html();

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<Backofficeuser> users = Ebean.find(Backofficeuser.class).findList();
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
        Backofficeuser user = Ebean.find(Backofficeuser.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser user = Ebean.find(Backofficeuser.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser user = Ebean.find(Backofficeuser.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user");
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
        Backofficeuser user = Ebean.find(Backofficeuser.class, userId);
        // Render user into template
        result.render("user", user);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
            return Results.redirect(context.getContextPath() + "/posters/backoffice/user");
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        // Find all of the admin users
        List<Backofficeuser> users = Ebean.find(Backofficeuser.class).findList();
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        //int pageSize = 10;

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        //Query<Product> query = Ebean.find(Product.class);

        // // Find total products and page for the pagination
        // int totalProductsCount = query.findRowCount();
        // int numberOfPage = totalProductsCount / pageSize;
        // result.render("numberOfPage", numberOfPage);

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
    // @FilterWith(SessionUserExistFilter.class)
    // public Result productPage(final Context context, @PathParam("pageNumber") int pageNumber)
    // {
    //     Result result = Results.html();
    //     int pageSize = 10;

    //     // Find current user
    //     User currentUser = Ebean.find(User.class, SessionHandling.getUserId(context));
    //     // Add current user into the back office
    //     result.render("currentUser", currentUser);

    //     Query<Product> query = Ebean.find(Product.class);

    //     // Find total products and page for the pagination
    //     int totalProductsCount = query.findRowCount();
    //     int numberOfPage = totalProductsCount / pageSize;
    //     result.render("numberOfPage", numberOfPage);
    //     // Find paged product
    //     PagingList<Product> pagingList = query.findPagingList(pageSize);
    //     List<Product> products = pagingList.getPage(pageNumber - 1).getList();
    //     // Add products into the back office
    //     result.render("products", products);

    //     // Add Page Number as logic value to render in freemarker
    //     result.render("pageNumber", pageNumber);

    //     return result.template("views_backoffice/BackofficeController/productList.ftl.html");
    // }
    
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        return result;
    }

    /**
     * Edit a customer's information and credentials, from the Customer List view.
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
    }

    
    /**
     * Update customer information in the database after edit from the customer list view.
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
            return Results.redirect(context.getContextPath() + "/posters/backoffice/customer");
        }
    }



    /**
     * Edit a customer's shipping information, from the Customer Detail view.
     * 
     * @param context
     * @return
     */

    public Result shippingAddressEdit(@Param("addressIdShip") final int shipId, final Context context, @PathParam("customerId") String customerId){
        Result result = Results.html();

        final Map<String, Object> commondata = new HashMap<String, Object>();
        commondata.put("address", ShippingAddress.getShippingAddressById(shipId));
        WebShopController.setCommonData(commondata, context, xcpConf);
        result.render(commondata);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);
        return result;
    
    }

    public Result shippingAddressEditComplete( final Context context, @PathParam("customerId") String customerId, 
                                            @Param("addressIdShip") final int shipId, @Param("fullName") final String fullName, 
                                            @Param("company") final String company, @Param("addressLine") final String addressLine, 
                                            @Param("city") final String city, @Param("state") final String state, @Param("zip") final String zip,
                                           @Param("country") final String country, @Param("addressIdShip") final String addressIdShip)
    {
        Result result = Results.html();

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);
        
        //Shipping Address Section
        if (zip != null)
        {
            // check input

            if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
            {
                final Map<String, Object> data = new HashMap<String, Object>();
                WebShopController.setCommonData(data, context, xcpConf);
                // show error message
                context.getFlashScope().error(msg.get("errorWrongZip", language).get());
                // show inserted values in form
                // final Map<String, String> address = new HashMap<String, String>();
                data.put("addressIdShip", addressIdShip);
                data.put("fullName", fullName);
                data.put("company", company);
                data.put("addressLine", addressLine);
                data.put("city", city);
                data.put("state", state);
                data.put("zip", zip);
                data.put("country", country);
                // data.put("address", address);
                // show page to enter shipping address again
                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/edit-ship-address");
            }
            // all input fields might be correct
            else
            {
                final ShippingAddress address = ShippingAddress.getShippingAddressById(Integer.parseInt(addressIdShip));
                address.setName(fullName);
                address.setCompany(company);
                address.setAddressLine(addressLine);
                address.setCity(city);
                address.setState(state);
                address.setZip(zip);
                address.setCountry(country);
                // update address
                address.update();
                // show success message
                context.getFlashScope().success(msg.get("successUpdate", language).get());
                // return Results.redirect(context.getContextPah() + "/addressOverview");
                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");

            }

        }
        return result;
    }
 
    /**
     * Edit a customer's billing information, from the Customer Detail view.
     * 
     * @param context
     * @return
     */

    public Result billingAddressEdit(@Param("addressIdBill") final int billId, final Context context, @PathParam("customerId") String customerId){
        Result result = Results.html();

        final Map<String, Object> commondata = new HashMap<String, Object>();
        commondata.put("address", BillingAddress.getBillingAddressById(billId));
        WebShopController.setCommonData(commondata, context, xcpConf);
        result.render(commondata);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);
        return result;
    
    }

    public Result billingAddressEditComplete(final Context context, @PathParam("customerId") String customerId,
                                             @Param("fullNameBill") final String nameBill, @Param("companyBill") final String companyBill,
                                             @Param("addressLineBill") final String addressLineBill,
                                             @Param("cityBill") final String cityBill, @Param("stateBill") final String stateBill,
                                             @Param("zipBill") final String zipBill, @Param("countryBill") final String countryBill,
                                             @Param("addressIdBill") final String addressIdBill)
    {
        Result result = Results.html();

        if (zipBill != null)
        {
            // check input

            if (!Pattern.matches(xcpConf.REGEX_ZIP, zipBill))
            {
                final Map<String, Object> data = new HashMap<String, Object>();
                WebShopController.setCommonData(data, context, xcpConf);
                // show error message
                context.getFlashScope().error(msg.get("errorWrongZip", language).get());
                // show inserted values in form
                // final Map<String, String> address = new HashMap<String, String>();
                data.put("addressIdBill", addressIdBill);
                data.put("nameBill", nameBill);
                data.put("companyBill", companyBill);
                data.put("addressLineBill", addressLineBill);
                data.put("cityBill", cityBill);
                data.put("stateBill", stateBill);
                data.put("zipBill", zipBill);
                data.put("countryBill", countryBill);
                // data.put("address", address);
                // show page to enter billing address again
                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/edit-bill-address");
            }
            // all input fields might be correct
            else
            {
                final BillingAddress address = BillingAddress.getBillingAddressById(Integer.parseInt(addressIdBill));
                address.setName(nameBill);
                address.setCompany(companyBill);
                address.setAddressLine(addressLineBill);
                address.setCity(cityBill);
                address.setState(stateBill);
                address.setZip(zipBill);
                address.setCountry(countryBill);
                // update address
                address.update();
                // show success message
                context.getFlashScope().success(msg.get("successUpdate", language).get());
                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
            }
        }

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;

    }



    /**
     * Edit a customer's payment information, from the Customer Detail view.
     * 
     * @param context
     * @return
     */

    public Result paymentInfoEdit(@Param("creditCardId") final int cardId, final Context context, @PathParam("customerId") String customerId){
        Result result = Results.html();

        final Map<String, Object> commondata = new HashMap<String, Object>();
        commondata.put("creditInfo", CreditCard.getCreditCardById(cardId));
        WebShopController.setCommonData(commondata, context, xcpConf);
        
        DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        Date date = new Date();
        commondata.put("expirationDateStartYear", Integer.valueOf(dateFormatYear.format(date)));

        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
                // get current month and year
        commondata.put("currentYear", Integer.valueOf(dateFormatYear.format(date)));
        commondata.put("currentMonth", Integer.valueOf(dateFormatMonth.format(date)));
        
        result.render(commondata);
        
        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);
        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);
        return result;
    
    }

    public Result paymentInfoEditComplete(final Context context, @PathParam("customerId") String customerId,
                                          @Param("creditCardNumber") String creditNumber, @Param("name") final String name,
                                          @Param("expirationDateMonth") final int month, @Param("expirationDateYear") final int year,
                                          @Param("creditCardId") final int cardId)
    {
        Result result = Results.html();

        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check input
        for (final String regExCreditCard : xcpConf.REGEX_CREDITCARD)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // get creditCard by ID
                final CreditCard creditCard = CreditCard.getCreditCardById(cardId);
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // update creditCard
                creditCard.update();
                // success message
                context.getFlashScope().success(msg.get("successSave", language).get());
                // show customer overview page
                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
            }
            else
            {
                // credit card number is not valid
                final Map<String, Object> data = new HashMap<String, Object>();
                WebShopController.setCommonData(data, context, xcpConf);
                // show error message
                context.getFlashScope().error(msg.get("errorWrongCreditCard", language).get());
                // show inserted values in form
                final Map<String, String> card = new HashMap<String, String>();
                card.put("name", name);
                card.put("cardNumber", creditNumber);
                data.put("card", card);
                result.render(data);

                return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/edit-payment-info");

            }
        }

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;

    }


   

    /**
     * Update customer information in the database after edit from the customer view.
     * 
     * @param context
     * @return
     */
    public Result customerViewEditComplete(final Context context, @PathParam("customerId") String customerId,
                                           @Param("email") final String email, @Param("firstName") final String firstName,
                                           @Param("name") final String name, @Param("password") final String password,
                                           //Shipping Info
                                           @Param("fullName") final String fullName, @Param("company") final String company,
                                           @Param("addressLine") final String addressLine, @Param("city") final String city,
                                           @Param("state") final String state, @Param("zip") final String zip,
                                           @Param("country") final String country, @Param("addressIdShip") final String addressIdShip,
                                            //Billing Info
                                           @Param("fullNameBill") final String nameBill, @Param("companyBill") final String companyBill,
                                           @Param("addressLineBill") final String addressLineBill, @Param("cityBill") final String cityBill,
                                           @Param("stateBill") final String stateBill, @Param("zipBill") final String zipBill,
                                           @Param("countryBill") final String countryBill,
                                           @Param("addressIdBill") final String addressIdBill)
    {

        Result result = Results.html();
        //Shipping Address Section
        if (zip != null)
        {
            // check input

            if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
            {
                final Map<String, Object> data = new HashMap<String, Object>();
                WebShopController.setCommonData(data, context, xcpConf);
                // show error message
                context.getFlashScope().error(msg.get("errorWrongZip", language).get());
                // show inserted values in form
                // final Map<String, String> address = new HashMap<String, String>();
                data.put("addressIdShip", addressIdShip);
                data.put("fullName", fullName);
                data.put("company", company);
                data.put("addressLine", addressLine);
                data.put("city", city);
                data.put("state", state);
                data.put("zip", zip);
                data.put("country", country);
                // data.put("address", address);
                // show page to enter shipping address again
                return Results.html().render(data).template(xcpConf.TEMPLATE_UPDATE_SHIPPING_ADDRESS);
            }
            // all input fields might be correct
            else
            {
                final ShippingAddress address = ShippingAddress.getShippingAddressById(Integer.parseInt(addressIdShip));
                address.setName(fullName);
                address.setCompany(company);
                address.setAddressLine(addressLine);
                address.setCity(city);
                address.setState(state);
                address.setZip(zip);
                address.setCountry(country);
                // update address
                address.update();
                // show success message
                context.getFlashScope().success(msg.get("successUpdate", language).get());
                // return Results.redirect(context.getContextPah() + "/addressOverview");
            }

        }

            //Billing Address Section

        if (zipBill != null)
            {
            // check input

            if (!Pattern.matches(xcpConf.REGEX_ZIP, zipBill))
            {
                final Map<String, Object> data = new HashMap<String, Object>();
                WebShopController.setCommonData(data, context, xcpConf);
                // show error message
                context.getFlashScope().error(msg.get("errorWrongZip", language).get());
                // show inserted values in form
                // final Map<String, String> address = new HashMap<String, String>();
                data.put("addressIdBill", addressIdBill);
                data.put("nameBill", nameBill);
                data.put("companyBill", companyBill);
                data.put("addressLineBill", addressLineBill);
                data.put("cityBill", cityBill);
                data.put("stateBill", stateBill);
                data.put("zipBill", zipBill);
                data.put("countryBill", countryBill);
                // data.put("address", address);
                // show page to enter shipping address again
                return Results.html().render(data).template(xcpConf.TEMPLATE_UPDATE_BILLING_ADDRESS);
            }
            // all input fields might be correct
            else
            {
            final BillingAddress address = BillingAddress.getBillingAddressById(Integer.parseInt(addressIdBill));
                address.setName(nameBill);
                address.setCompany(companyBill);
                address.setAddressLine(addressLineBill);
                address.setCity(cityBill);
                address.setState(stateBill);
                address.setZip(zipBill);
                address.setCountry(countryBill);
                // update address
                address.update();
                // show success message
                context.getFlashScope().success(msg.get("successUpdate", language).get());
                // return Results.redirect(context.getContextPath() + "/addressOverview");
            }

        }

        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
            return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
        }
    }

    
     public Result customerViewShipppingAddressDelete(final Context context, @Param("addressIdShip") final int addressIdShip, @PathParam("customerId") String customerId)
    {
            // remove shipping address
            ShippingAddress.removeCustomerFromShippingAddress(addressIdShip);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            // show address overview page
            return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
    }

    public Result customerViewBillingAddressDelete(final Context context, @Param("addressIdBill") final int addressIdBill, @PathParam("customerId") String customerId)
    {
            // remove Billing address
            BillingAddress.removeCustomerFromBillingAddress(addressIdBill);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            // show address overview page
            return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
    }
    

    public Result paymentInfoDelete(final Context context, @Param("creditCardId") final int cardId, @PathParam("customerId") String customerId)
    {
            // remove Payment Method
            CreditCard.removeCustomerFromCreditCard(cardId);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            // show address overview page
            return Results.redirect(context.getContextPath() + "/posters/backoffice/customer/" + customerId + "/view");
    }

/**
     * View customer order information in the database.
     * 
     * @param context
     * @return
     */
    public Result customerViewOrders(final Context context, @PathParam("customerId") String customerId)
{
        Result result = Results.html();

        final Map<String, Object> data = new HashMap<String, Object>();

        UUID customerUUID = UUID.fromString(customerId);
        WebShopController.setCommonData(data, context, xcpConf);
        List<Order> orders = Customer.getCustomerById(customerUUID).getAllOrders();

        data.put("orderOverview", orders);
        result.render(data);


        // Find customer with the id from the params
        Customer customer = Ebean.find(Customer.class, customerId);
        // Render customer into template
        result.render("customer", customer);

        // Find current user
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
        // Add current user into the back office
        result.render("currentUser", currentUser);

        return result;
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
            List<Backofficeuser> users = Ebean.find(Backofficeuser.class).where().icontains("firstName", searchQuery).findList();
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Backofficeuser currentUser = Ebean.find(Backofficeuser.class, SessionHandling.getUserId(context));
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
        Integer adminUserAmount = Ebean.find(Backofficeuser.class).findList().size();
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
