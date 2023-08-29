/*
 * Copyright (c) 2013-2023 Xceptance Software Technologies GmbH
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
