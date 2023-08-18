package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

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
    private String name;

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
