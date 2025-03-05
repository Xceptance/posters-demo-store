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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.ebean.DB;

/**
 * This {@link Entity} provides a top category in the catalog of the poster demo store. A top category is a category, which
 * has at least one {@link SubCategory}. A top category contains all products of the corresponding sub categories.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "topCategory")
public class TopCategory
{

    /**
     * The ID of the category.
     */
    @Id 
    private int id;

    /**
     * The name of the category.
     */
    @OneToOne
    private DefaultText name;

    /**
     * The {@link SubCategory} list of the category.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topCategory")
    private List<SubCategory> subCategories;

    /**
     * The products of the top category.
     */
    @OneToMany
    private List<Product> products;

    /**
     * Returns the ID of the entity.
     * 
     * @return the ID of the entity
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
     * Returns the name reference of the category.
     * 
     * @return the name reference of the category
     */
    public DefaultText getName()
    {
        return name;
    }

    /**
     * Returns a specific name of the category.
     * 
     * @return a specific name of the category
     */
    public String getName(Language language)
    {
        if (language.getCode().equals("en-US"))
        {
            return this.getDefaultName();
        }
        else
        {
            Translation result = DB.find(Translation.class).where().eq("originalText", name).eq("translationLanguage", language).findOne();
            if (result == null) 
            {
                return this.getDefaultName();
            }
            else 
            {
                return result.getTranslationText();
            }
        }
    }

    /**
     * Returns a specific name of the category.
     * 
     * @return a specific name of the category
     */
    public String getName(String code)
    {
        if (code.equals("en-US"))
        {
            return name.getOriginalText();
        }
        else
        {
            Language language = DB.find(Language.class).where().eq("code", code).findOne();
            if (language == null) 
            {
                return this.getDefaultName();
            }
            Translation result = DB.find(Translation.class).where().eq("originalText", name).eq("translationLanguage", language).findOne();
            if (result == null)
            {
                return this.getDefaultName();
            } 
            else 
            {
                return result.getTranslationText();
            }
        }
    }

    /**
     * Returns the default name of the category.
     * 
     * @return the default name of the category
     */
    public String getDefaultName()
    {
        return name.getOriginalText();
    }

    /**
     * Sets the name reference of the category.
     * 
     * @param name
     *            the name reference of the category
     */
    public void setName(final DefaultText name)
    {
        this.name = name;
    }

    /**
     * Sets the name of the category.
     * 
     * @param name
     *            the name of the category
     */
    public void setDefaultName(final String name)
    {
        this.name.setOriginalText(name);
    }

    /**
     * Returns the {@link SubCategory} of the top category.
     * 
     * @return the {@link SubCategory} of the top category
     */
    public List<SubCategory> getSubCategories()
    {
        return subCategories;
    }

    /**
     * Sets the {@link SubCategory} of the top category.
     * 
     * @param subCategories
     *            the {@link SubCategory} of the top category
     */
    public void setSubCategories(final List<SubCategory> subCategories)
    {
        this.subCategories = subCategories;
    }

    /**
     * Returns the {@link Product}s of the top category.
     * 
     * @return the {@link Product}s of the top category
     */
    public List<Product> getProducts()
    {
        return products;
    }

    /**
     * Sets the {@link Product}s of the top category.
     * 
     * @param products
     *            the {@link Product}s of the top category
     */
    public void setProducts(final List<Product> products)
    {
        this.products = products;
    }

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        DB.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        DB.save(this);
    }

    /**
     * Returns all top categories which are stored in the database.
     * 
     * @return all top categories which are stored in the database
     */
    public static List<TopCategory> getAllTopCategories()
    {
        return DB.find(TopCategory.class).findList();
    }

    /**
     * Returns the {@link TopCategory} that matches the given ID.
     * 
     * @param id
     *            the ID of the {@link TopCategory}
     * @return the {@link TopCategory} that matches the given ID
     */
    public static TopCategory getTopCategoryById(final int id)
    {
        return DB.find(TopCategory.class, id);
    }
}
