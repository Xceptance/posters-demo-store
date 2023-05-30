package models_backoffice;


public class Statistic
{
    /**
    * This provides the statistics for the poster website.
    * 
    * @author kennygozali
    */


    /**
     * Number of orders.
     */
    private Integer orderAmount;

    /**
     * Number of customers.
     */
    private Integer customerAmount;

    /**
     * Number of administrators.
     */
    private Integer adminUserAmount;

    /**
     * Number of top catalog.
     */
    private Integer topCatalogAmount;

    /**
     * Number of sub catalog.
     */
    private Integer subCatalogAmount;

    /**
     * Constructor.
     */
    public Statistic()
    {
    }



    /**
     * Returns the number of orders.
     * 
     * @return the number of orders.
     */
    public Integer getOrderAmount()
    {
        return orderAmount;
    }

    /**
     * Sets the number of orders.
     * 
     * @param orderAmount
     *                  the number of orders.
     */
    public void setOrderAmount(final Integer orderAmount)
    {
        this.orderAmount = orderAmount;
    }

    /**
     * Returns the number of customers.
     * 
     * @return the number of customers.
     */
    public Integer getCustomerAmount()
    {
        return customerAmount;
    }

    /**
     * Sets the number of customers.
     * 
     * @param customerAmount
     *                  the number of customers.
     */
    public void setCustomerAmount(final Integer customerAmount)
    {
        this.customerAmount = customerAmount;
    }

    /**
     * Returns the number of admin users.
     * 
     * @return the number of admin users.
     */
    public Integer getAdminUserAmount()
    {
        return adminUserAmount;
    }

    /**
     * Sets the number of admin users.
     * 
     * @param adminUserAmount
     *                  the number of admin users.
     */
    public void setAdminUserAmount(final Integer adminUserAmount)
    {
        this.adminUserAmount = adminUserAmount;
    }

    /**
     * Returns the number of top catalogs.
     * 
     * @return the number of top catalogs.
     */
    public Integer getTopCatalogAmount()
    {
        return topCatalogAmount;
    }

    /**
     * Sets the number of top catalogs.
     * 
     * @param topCatalogAmount
     *                  the number of top catalogs.
     */
    public void setTopCatalogAmount(final Integer topCatalogAmount)
    {
        this.topCatalogAmount = topCatalogAmount;
    }

    /**
     * Returns the number of sub catalogs.
     * 
     * @return the number of sub catalogs.
     */
    public Integer getSubCatalogAmount()
    {
        return subCatalogAmount;
    }

    /**
     * Sets the number of sub catalogs.
     * 
     * @param subCatalogAmount
     *                  the number of sub catalogs.
     */
    public void setSubCatalogAmount(final Integer subCatalogAmount)
    {
        this.subCatalogAmount = subCatalogAmount;
    }
}
