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
 * This {@link Entity} provides a sub category in the catalog of the poster store. The sub category belongs to one
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

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public TopCategory getTopCategory()
    {
        return topCategory;
    }

    public void setTopCategory(TopCategory topCategory)
    {
        this.topCategory = topCategory;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }

    public static SubCategory getSubCategoryById(int id)
    {
        return Ebean.find(SubCategory.class, id);
    }
}
