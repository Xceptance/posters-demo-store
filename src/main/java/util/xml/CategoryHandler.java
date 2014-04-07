package util.xml;

import java.util.ArrayList;
import java.util.List;

import models.SubCategory;
import models.TopCategory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.avaje.ebean.Ebean;

/**
 * This {@link DefaultHandler} parses an XML file with top and sub categories and persists them in the database.
 * 
 * @author sebastianloob
 */
public class CategoryHandler extends DefaultHandler
{

    private TopCategory category;

    private SubCategory subCategory;

    private List<SubCategory> subCategoryList;

    private String currentValue;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts)
    {
        if (localName.equals("category"))
        {
            category = new TopCategory();
            subCategoryList = new ArrayList<SubCategory>();
        }
        if (localName.equals("subCategory"))
        {
            subCategory = new SubCategory();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
    {
        if (localName.equals("nameCategory"))
        {
            category.setName(currentValue);
        }
        if (localName.equals("category"))
        {
            category.setSubCategories(subCategoryList);
            Ebean.save(category);
        }
        if (localName.equals("nameSubCategory"))
        {
            subCategory.setName(currentValue);
        }
        if (localName.equals("subCategory"))
        {
            subCategoryList.add(subCategory);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
    {
        currentValue = new String(ch, start, length);
    }
}
