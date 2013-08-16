package util.xml;

import models.Product;
import models.SubCategory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.avaje.ebean.Ebean;

public class ProductHandler extends DefaultHandler
{

    private String currentValue;

    private Product product;

    @Override
    public void characters(char[] ch, int start, int length)
    {
        currentValue = new String(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
        if (localName.equals("product"))
        {
            product = new Product();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        if (localName.equals("name"))
        {
            product.setName(currentValue);
            String url = currentValue; //.replaceAll(" ", "");
            product.setUrl(url);
        }
        if (localName.equals("shortDescription"))
        {
            product.setDescriptionOverview(currentValue);
        }
        if (localName.equals("longDescription"))
        {
            product.setDescriptionDetail(currentValue);
        }
        if (localName.equals("price"))
        {
            product.setPrice(Double.parseDouble(currentValue));
        }
        if (localName.equals("imageURL"))
        {
            product.setImageURL(currentValue);
        }
        if (localName.equals("subCategory"))
        {
            SubCategory subCategory = Ebean.find(SubCategory.class).where().eq("name", currentValue).findUnique();
            if (subCategory == null)
            {
                subCategory = new SubCategory();
                subCategory.setName(currentValue);
            }
            product.setSubCategory(subCategory);
        }
        if (localName.equals("carousel"))
        {
            if (currentValue.equals("true"))
            {
                product.setShowInCarousel(true);
            }
            else
            {
                product.setShowInCarousel(false);
            }
        }
        if (localName.equals("showInTopCategory"))
        {
            if (currentValue.equals("true"))
            {
                product.setShowInTopCategorie(true);
            }
            else
            {
                product.setShowInTopCategorie(false);
            }
        }
        if (localName.equals("product"))
        {
            Ebean.save(product);
        }
    }
}
