package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

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
    private String name;

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
     * Returns the name of the category.
     * 
     * @return the name of the category
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the category.
     * 
     * @param name
     *            the name of the category
     */
    public void setName(final String name)
    {
        this.name = name;
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
     * Returns all top categories which are stored in the database.
     * 
     * @return all top categories which are stored in the database
     */
    public static List<TopCategory> getAllTopCategories()
    {
        return Ebean.find(TopCategory.class).findList();
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
        return Ebean.find(TopCategory.class, id);
    }
}
