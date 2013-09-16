package util.xml;

import models.BillingAddress;
import models.Customer;
import models.DeliveryAddress;
import models.SubCategory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class CustomerHandler extends DefaultHandler
{

    private Customer customer;
    
    private DeliveryAddress deliveryAddress;
    
    private BillingAddress billingAddress;
    
    private String currentValue;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        if (localName.equals("customer"))
        {
            customer = new Customer();
        }
        
        if (localName.equals("deliveryAddress"))
        {
            deliveryAddress = new DeliveryAddress();
        }
        
        if (localName.equals("billingAddress"))
        {
            billingAddress = new BillingAddress();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        if (localName.equals("customer"))
        {
            customer.save();
        }
        
        if (localName.equals("deliveryAddress"))
        {
            customer.addDeliveryAddress(deliveryAddress);
        }
        
        if (localName.equals("billingAddress"))
        {
            customer.addBillingAddress(billingAddress);
        }
        
        if (localName.equals("email"))
        {
            customer.setEmail(currentValue);
        }
        
        if (localName.equals("password"))
        {
            customer.hashPasswd(currentValue);
        }
        
        if (localName.equals("name"))
        {
            customer.setName(currentValue);
        }
        
        if (localName.equals("firstName"))
        {
            customer.setFirstName(currentValue);
        }
        
        if (localName.equals("delName"))
        {
            deliveryAddress.setName(currentValue);
        }
        
        if (localName.equals("delCompany"))
        {
            deliveryAddress.setCompany(currentValue);
        }
        
        if (localName.equals("delAddressLine"))
        {
            deliveryAddress.setAddressLine(currentValue);
        }
        
        if (localName.equals("delCity"))
        {
            deliveryAddress.setCity(currentValue);
        }
        
        if (localName.equals("delState"))
        {
            deliveryAddress.setState(currentValue);
        }
        
        if (localName.equals("delCountry"))
        {
            deliveryAddress.setCountry(currentValue);
        }
        
        if (localName.equals("delZip"))
        {
            deliveryAddress.setZip(currentValue);
        }
        
        if (localName.equals("billName"))
        {
            billingAddress.setName(currentValue);
        }
        
        if (localName.equals("billCompany"))
        {
            billingAddress.setCompany(currentValue);
        }
        
        if (localName.equals("billAddressLine"))
        {
            billingAddress.setAddressLine(currentValue);
        }
        
        if (localName.equals("billCity"))
        {
            billingAddress.setCity(currentValue);
        }
        
        if (localName.equals("billState"))
        {
            billingAddress.setState(currentValue);
        }
        
        if (localName.equals("billCountry"))
        {
            billingAddress.setCountry(currentValue);
        }
        
        if (localName.equals("billZip"))
        {
            billingAddress.setZip(currentValue);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
    {
        currentValue = new String(ch, start, length);
    }
}
