package util.xml;

import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.ShippingAddress;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This {@link DefaultHandler} parses an XML file with customer data and persists them in the database.
 * 
 * @author sebastianloob
 */
public class CustomerHandler extends DefaultHandler
{

    private Customer customer;

    private ShippingAddress shippingAddress;

    private BillingAddress billingAddress;

    private CreditCard creditCard;

    private String currentValue;

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
    {
        if (localName.equals("customer"))
        {
            customer = new Customer();
            customer.save();
        }
        if (localName.equals("shippingAddress"))
        {
            shippingAddress = new ShippingAddress();
        }
        if (localName.equals("billingAddress"))
        {
            billingAddress = new BillingAddress();
        }
        if (localName.equals("paymentMethod"))
        {
            creditCard = new CreditCard();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        if (localName.equals("customer"))
        {
            customer.update();
        }
        if (localName.equals("shippingAddress"))
        {
            customer.addShippingAddress(shippingAddress);
        }
        if (localName.equals("billingAddress"))
        {
            customer.addBillingAddress(billingAddress);
        }
        if (localName.equals("paymentMethod"))
        {
            customer.addCreditCard(creditCard);
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
            shippingAddress.setName(currentValue);
        }
        if (localName.equals("delCompany"))
        {
            shippingAddress.setCompany(currentValue);
        }
        if (localName.equals("delAddressLine"))
        {
            shippingAddress.setAddressLine(currentValue);
        }
        if (localName.equals("delCity"))
        {
            shippingAddress.setCity(currentValue);
        }
        if (localName.equals("delState"))
        {
            shippingAddress.setState(currentValue);
        }
        if (localName.equals("delCountry"))
        {
            shippingAddress.setCountry(currentValue);
        }
        if (localName.equals("delZip"))
        {
            shippingAddress.setZip(currentValue);
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
        if (localName.equals("paymentName"))
        {
            creditCard.setName(currentValue);
        }
        if (localName.equals("cardNumber"))
        {
            creditCard.setCardNumber(currentValue);
        }
        if (localName.equals("month"))
        {
            creditCard.setMonth(Integer.parseInt(currentValue));
        }
        if (localName.equals("year"))
        {
            creditCard.setYear(Integer.parseInt(currentValue));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
    {
        currentValue = new String(ch, start, length);
    }
}
