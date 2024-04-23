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

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} provides a billing address. A billing address can be set to at most one {@link Customer}.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "billingAddress")
public class BillingAddress
{

    /**
     * The id of the entity.
     */
    @Id
    private int id;

    /**
     * The name field of the address.
     */
    private String name;

    /**
     * The company field of the address.
     */
    private String company;

    /**
     * The address line.
     */
    private String addressLine;

    /**
     * The city field of the address.
     */
    private String city;

    /**
     * The state field of the address.
     */
    private String state;

    /**
     * The country field of the address.
     */
    private String country;

    /**
     * The ZIP code of the address.
     */
    private String zip;

    /**
     * The {@link Customer} of the address. Can be {@code null}, if its an address of an unregistered customer.
     */
    @ManyToOne
    private Customer customer;

    /**
     * A list of {@link Order}s, for which this address is set as the billing address.
     */
    @OneToMany
    private List<Order> order;

    /**
     * Returns the ID of the billing address.
     * 
     * @return the ID of the address
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of the entity.
     * 
     * @param id
     *            the ID of the entity
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * Returns the company field of the billing address.
     * 
     * @return the company field
     */
    public String getCompany()
    {
        return company;
    }

    /**
     * Sets the company field of the billing address.
     * 
     * @param company
     *            the company field
     */
    public void setCompany(final String company)
    {
        this.company = company;
    }

    /**
     * Returns the {@link Customer} with this billing address.
     * 
     * @return the {@link Customer} with this billing address
     */
    public Customer getCustomer()
    {
        return customer;
    }

    /**
     * Sets a customer to the billing address.
     * 
     * @param customer
     *            the customer with this billing address
     */
    public void setCustomer(final Customer customer)
    {
        this.customer = customer;
    }

    /**
     * Returns the name field of the billing address.
     * 
     * @return the name field
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name field of the billing address.
     * 
     * @param name
     *            the name field
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the address line of the billing address.
     * 
     * @return the address line
     */
    public String getAddressLine()
    {
        return addressLine;
    }

    /**
     * Sets the address line of the billing address.
     * 
     * @param addressLine
     *            the address line
     */
    public void setAddressLine(final String addressLine)
    {
        this.addressLine = addressLine;
    }

    /**
     * Returns the city field of the billing address.
     * 
     * @return the city field
     */
    public String getCity()
    {
        return city;
    }

    /**
     * Sets the city field of the billing address.
     * 
     * @param city
     *            the city field
     */
    public void setCity(final String city)
    {
        this.city = city;
    }

    /**
     * Returns the state field of the billing address.
     * 
     * @return the state field
     */
    public String getState()
    {
        return state;
    }

    /**
     * Sets the state field of the billing address.
     * 
     * @param state
     *            the state field
     */
    public void setState(final String state)
    {
        this.state = state;
    }

    /**
     * Returns the country field of the billing address.
     * 
     * @return the country field
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * Sets the country field of the billing address.
     * 
     * @param country
     *            the country field
     */
    public void setCountry(final String country)
    {
        this.country = country;
    }

    /**
     * Returns the ZIP code of the billing address.
     * 
     * @return the ZIP code
     */
    public String getZip()
    {
        return zip;
    }

    /**
     * Sets the ZIP code of the billing address.
     * 
     * @param zip
     *            the ZIP code
     */
    public void setZip(final String zip)
    {
        this.zip = zip;
    }

    /**
     * Returns all {@link Order}s, for which this address is set as billing address.
     * 
     * @return a list of {@link Order}s
     */
    public List<Order> getOrder()
    {
        return order;
    }

    /**
     * Sets the list of {@link Order}s, for which this address is set as billing address.
     * 
     * @param orders
     *            a list of {@link Order}s
     */
    public void setOrder(final List<Order> orders)
    {
        order = orders;
    }

    /**
     * Updates the {@link BillingAddress} in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the {@link BillingAddress} in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Deletes the {@link BillingAddress} in the database.
     */
    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Returns the {@link BillingAddress} that matches the given ID.
     * 
     * @param id
     *            the ID of the billing address
     * @return the {@link BillingAddress} that matches the given ID
     */
    public static BillingAddress getBillingAddressById(final int id)
    {
        return Ebean.find(BillingAddress.class, id);
    }

    /**
     * Returns the {@link BillingAddress} that matches the given Customer ID.
     * 
     * @param id
     *            the ID of the Customer
     * @return the {@link BillingAddress} that matches the given Customer ID
     */
    public static BillingAddress getBillingAddressByCustomerId(final UUID customerId) {
        return Ebean.find(BillingAddress.class)
                    .where()
                    .eq("customer_id", customerId)
                    .findUnique();
    }

    /**
     * Sets the {@link Customer} of the {@link BillingAddress} to {@code null}.
     * 
     * @param id
     *            the ID of the billing address
     */
    public static void removeCustomerFromBillingAddress(final int id)
    {
        // get the billing address by the ID
        final BillingAddress address = getBillingAddressById(id);
        if (address != null)
        {
            Ebean.delete(address);

        }
    }

    /**
     * Sets the {@link Customer} of the {@link BillingAddress} to {@code null}.
     * 
     * @param id
     *            the ID of the logged in Customer
     */
    public static void removeCustomerFromBillingAddressByCustomerId(final UUID id){
        final BillingAddress address = BillingAddress.getBillingAddressByCustomerId(id);
        if (address != null && address.getCustomer()!=null) {

        address.setCustomer(null);
        address.update();
        }
    }
}
