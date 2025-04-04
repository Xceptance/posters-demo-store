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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.ebean.Ebean;

/**
 * This {@link Entity} provides a sub category in the catalog of the poster demo store. The sub category belongs to one
 * {@link TopCategory}. A sub category has different products.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "subCategory")
public class SubCategory
{

    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The name of the sub category.
     */
    @OneToOne
    private DefaultText name;

    /**
     * The {@link TopCategory} the sub category belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "topCategory_id")
    private TopCategory topCategory;

    /**
     * The {@link Product}s of the sub category.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subCategory")
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
            Translation result = Ebean.find(Translation.class).where().eq("originalText", name).eq("translationLanguage", language).findOne();
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
            Language language = Ebean.find(Language.class).where().eq("code", code).findOne();
            if (language == null) {
                return this.getDefaultName();
            }
            Translation result = Ebean.find(Translation.class).where().eq("originalText", name).eq("translationLanguage", language).findOne();
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
     * Sets the default name of the category.
     * 
     * @param name
     *            the default name of the category
     */
    public void setDefaultName(final String name)
    {
        this.name.setOriginalText(name);
    }

    /**
     * Returns the {@link TopCategory} this sub category belongs to.
     * 
     * @return the {@link TopCategory} this sub category belongs to
     */
    public TopCategory getTopCategory()
    {
        return topCategory;
    }

    /**
     * Sets the {@link TopCategory} this sub category belongs to
     * 
     * @param topCategory
     *            the {@link TopCategory} this sub category belongs to
     */
    public void setTopCategory(final TopCategory topCategory)
    {
        this.topCategory = topCategory;
    }

    /**
     * Returns the {@link Product}s of the category.
     * 
     * @return the {@link Product}s of the category
     */
    public List<Product> getProducts()
    {
        return products;
    }

    /**
     * Sets the {@link Product}s of the category.
     * 
     * @param products
     *            the {@link Product}s of the category
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
        Ebean.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Returns the {@link SubCategory} that matches the given ID.
     * 
     * @param id
     *            the ID of the {@link SubCategory}
     * @return the {@link SubCategory} that matches the given ID
     */
    public static SubCategory getSubCategoryById(final int id)
    {
        return Ebean.find(SubCategory.class, id);
    }
}
