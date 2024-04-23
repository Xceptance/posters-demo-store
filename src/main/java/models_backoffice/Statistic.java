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
package models_backoffice;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "statistic")
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
    private Integer topCategoryAmount;

    /**
     * Number of sub catalog.
     */
    private Integer subCategoryAmount;

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
     * Returns the number of top categories.
     * 
     * @return the number of top categories.
     */
    public Integer getTopCategoryAmount()
    {
        return topCategoryAmount;
    }

    /**
     * Sets the number of top categories.
     * 
     * @param topCategoryAmount
     *                  the number of top categories.
     */
    public void setTopCategoryAmount(final Integer topCategoryAmount)
    {
        this.topCategoryAmount = topCategoryAmount;
    }

    /**
     * Returns the number of sub categories.
     * 
     * @return the number of sub categories.
     */
    public Integer getSubCategoryAmount()
    {
        return subCategoryAmount;
    }

    /**
     * Sets the number of sub categories.
     * 
     * @param subCategoryAmount
     *                  the number of sub categories.
     */
    public void setSubCategoryAmount(final Integer subCategoryAmount)
    {
        this.subCategoryAmount = subCategoryAmount;
    }
}
