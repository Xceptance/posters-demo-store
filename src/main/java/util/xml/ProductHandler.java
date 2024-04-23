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

    private String[] prices;

    @Override
    public void characters(final char[] ch, final int start, final int length)
    {
        currentValue.append(new String(ch, start, length));
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
    {
        if (localName.equals("product")) {
            product = new Product();
            product.save();
        } else if (localName.equals("imageURL")) {
            // Initialize product's image URLs
            product.setImageURL("");
            product.setSmallImageURL("");
            product.setMediumImageURL("");
            product.setLargeImageURL("");
            product.setOriginalImageURL("");
        }
        currentValue = new StringBuilder();
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
    {
        final String toAdd = currentValue.toString();
        if (localName.equals("name"))
        {
            product.setName(toAdd);
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
            prices = toAdd.split(";");
            // add first price as minimum price
            product.setMinimumPrice(Double.parseDouble(prices[0]));
        }
        else if (localName.equals("imageURL"))
        {
            //product.setImageURL(toAdd);
        }
        else if (localName.equals("small"))
        {
            product.setSmallImageURL(toAdd);
        }
        else if (localName.equals("medium"))
        {
            product.setMediumImageURL(toAdd);
        }
        else if (localName.equals("large"))
        {
            product.setLargeImageURL(toAdd);
        }
        else if (localName.equals("original"))
        {
            product.setOriginalImageURL(toAdd);
        }
        else if (localName.equals("subCategory"))
        {
            SubCategory subCategory = Ebean.find(SubCategory.class).where().eq("name", toAdd).findUnique();
            if (subCategory == null)
            {
                subCategory = new SubCategory();
                subCategory.setName(toAdd);
            }
            product.setSubCategory(subCategory);
            final TopCategory topCategory = Ebean.find(TopCategory.class).where().eq("subCategories", subCategory).findUnique();
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
            // add the price for each size
            final List<ProductPosterSize> productPosterSizes = Ebean.find(ProductPosterSize.class).where().eq("product", product)
                                                                    .findList();
            for (int i = 0; i < productPosterSizes.size(); i++)
            {
                productPosterSizes.get(i).setPrice(Double.parseDouble(prices[i]));
                productPosterSizes.get(i).update();
            }
            // update product
            product.update();
        }
        if (localName.equals("availableSize"))
        {
            final String[] sizes = toAdd.split(";");
            for (final String size : sizes)
            {
                final String[] dummy = size.split("x");
                final int width = Integer.parseInt(dummy[0]);
                final int height = Integer.parseInt(dummy[1]);
                PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height).findUnique();
                if (posterSize == null)
                {
                    posterSize = new PosterSize();
                    posterSize.setWidth(width);
                    posterSize.setHeight(height);
                    posterSize.save();
                }
                final ProductPosterSize productPosterSize = new ProductPosterSize();
                productPosterSize.setProduct(product);
                productPosterSize.setSize(posterSize);
                productPosterSize.save();
                product.addAvailableSize(productPosterSize);
            }
        }
    }
}
