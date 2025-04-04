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

import java.util.ArrayList;
import java.util.List;

import models.DefaultText;
import models.Language;
import models.SubCategory;
import models.TopCategory;
import models.Translation;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import io.ebean.Ebean;

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

    private boolean isDefault = true;

    private DefaultText dtext;

    private Translation transl;

    private Language defaultLanguage = Ebean.find(Language.class).where().eq("code", "en-US").findOne();

    private String translationCode;


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
        if (localName.equals("nameCategory") || localName.equals("nameSubCategory"))
        {
            String lang = atts.getValue("xml:lang");
            if (lang.equals("x-default")) 
            {
                // make an entry in the default texts table
                isDefault = true;
                dtext = new DefaultText();
            } 
            else if (StringUtils.isBlank(lang))
            {
                // error handling
                isDefault = true;
                dtext = new DefaultText();
            }
            else
            {
                // add as translation
                isDefault = false;
                transl = new Translation();
                translationCode = lang;
            }
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
    {
        if (localName.equals("category"))
        {
            category.setSubCategories(subCategoryList);
            Ebean.save(category);
        }
        if (localName.equals("subCategory"))
        {
            subCategoryList.add(subCategory);
        }
        if (localName.equals("nameCategory"))
        {
            if (isDefault == true)
            {
                // add as default text and add reference to it to product
                dtext.setOriginalText(currentValue);
                dtext.setOriginalLanguage(defaultLanguage);
                dtext.save();
                category.setName(dtext);
            }
            else
            {
                // or add as translation and add reference to default text
                // NOTE: this was implemented 'quick and dirty' it assumes a default text
                // always precedes a translation! The data model requires a default text
                // to access a translation in the application
                transl.setTranslationText(currentValue);
                transl.setText(dtext);

                // get the translations language
                Language transLanguage;
                Boolean langExists = Ebean.find(Language.class).where().eq("code", translationCode).exists();
                if (langExists == true) 
                {
                    transLanguage = Ebean.find(Language.class).where().eq("code", translationCode).findOne();
                    transl.setTranslationLanguageId(transLanguage);
                    transl.save();
                }
            }
        }
        if (localName.equals("nameSubCategory"))
        {
            if (isDefault == true)
            {
                // add as default text and add reference to it to product
                dtext.setOriginalText(currentValue);
                dtext.setOriginalLanguage(defaultLanguage);
                dtext.save();
                subCategory.setName(dtext);
            }
            else
            {
                // or add as translation and add reference to default text
                // NOTE: this was implemented 'quick and dirty' it assumes a default text
                // always precedes a translation! The data model requires a default text
                // to access a translation in the application
                transl.setTranslationText(currentValue);
                transl.setText(dtext);

                // get the translations language
                Language transLanguage;
                Boolean langExists = Ebean.find(Language.class).where().eq("code", translationCode).exists();
                if (langExists == true) 
                {
                    transLanguage = Ebean.find(Language.class).where().eq("code", translationCode).findOne();
                    transl.setTranslationLanguageId(transLanguage);
                    transl.save();
                }
            }
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
    {
        currentValue = new String(ch, start, length);
    }
}
