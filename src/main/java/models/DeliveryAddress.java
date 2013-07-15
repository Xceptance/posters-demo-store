package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "deliveryAddress")
public class DeliveryAddress
{

    @Id
    private int id;

    @Version
    private Timestamp lastUpdate;

    private String name;

    private String addressline1;

    private String addressline2;

    private String city;

    private String state;

    private String country;

    private String zip;

    @ManyToOne
    private Customer customer;

    @OneToMany
    private List<Order> order;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getAddressline1()
    {
        return addressline1;
    }

    public void setAddressline1(String street)
    {
        this.addressline1 = street;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddressline2()
    {
        return addressline2;
    }

    public void setAddressline2(String addressline2)
    {
        this.addressline2 = addressline2;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public List<Order> getOrder()
    {
        return order;
    }

    public void setOrder(List<Order> order)
    {
        this.order = order;
    }

    public Timestamp getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }
}
