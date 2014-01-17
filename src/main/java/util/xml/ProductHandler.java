package util.xml;

import java.util.List;

import models.PosterSize;
import models.Product;
import models.ProductPosterSize;
import models.SubCategory;
import models.TopCategory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.avaje.ebean.Ebean;

/**
 * This {@link DefaultHandler} parses an XML file with product data and persists them in the database.
 * 
 * @author sebastianloob
 */
public class ProductHandler extends DefaultHandler
{

    private StringBuilder currentValue;

    private Product product;

    @Override
    public void characters(char[] ch, int start, int length)
    {
        currentValue.append(new String(ch, start, length));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
    {
        if (localName.equals("product"))
        {
            product = new Product();
            product.save();
        }
        currentValue = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        String toAdd = currentValue.toString();
        if (localName.equals("name"))
        {
            product.setName(toAdd);
            product.setUrl(toAdd);
        }
        if (localName.equals("shortDescription"))
        {
            product.setDescriptionOverview(toAdd);
        }
        if (localName.equals("longDescription"))
        {
            product.setDescriptionDetail(toAdd);
        }
        if (localName.equals("price"))
        {
            // get all available prices
            String[] prices = toAdd.split(";");
            // add first price as minimum price
            product.setMinimumPrice(Double.parseDouble(prices[0]));
            // add the price for each size
            List<ProductPosterSize> productPosterSizes = Ebean.find(ProductPosterSize.class).where()
                                                               .eq("product", product).findList();
            for (int i = 0; i < productPosterSizes.size(); i++)
            {
                productPosterSizes.get(i).setPrice(Double.parseDouble(prices[i]));
                productPosterSizes.get(i).update();
            }
        }
        if (localName.equals("imageURL"))
        {
            product.setImageURL(toAdd);
        }
        if (localName.equals("subCategory"))
        {
            SubCategory subCategory = Ebean.find(SubCategory.class).where().eq("name", toAdd).findUnique();
            if (subCategory == null)
            {
                subCategory = new SubCategory();
                subCategory.setName(toAdd);
            }
            product.setSubCategory(subCategory);
            TopCategory topCategory = Ebean.find(TopCategory.class).where().eq("subCategories", subCategory)
                                           .findUnique();
            product.setTopCategory(topCategory);
        }
        if (localName.equals("carousel"))
        {
            if (toAdd.equals("true"))
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
            if (toAdd.equals("true"))
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
            Ebean.update(product);
        }
        if (localName.equals("availableSize"))
        {
            String[] sizes = toAdd.split(";");
            for (String size : sizes)
            {
                String[] dummy = size.split("x");
                int width = Integer.parseInt(dummy[0]);
                int height = Integer.parseInt(dummy[1]);
                PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                             .findUnique();
                if (posterSize == null)
                {
                    posterSize = new PosterSize();
                    posterSize.setWidth(width);
                    posterSize.setHeight(height);
                    posterSize.save();
                }
                ProductPosterSize productPosterSize = new ProductPosterSize();
                productPosterSize.setProduct(product);
                productPosterSize.setSize(posterSize);
                productPosterSize.save();
                product.addAvailableSize(productPosterSize);
            }
        }
    }
}
