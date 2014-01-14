package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * A top category is a category, which has at least one {@link SubCategory}. The top category itself has no products,
 * just the referenced sub categories have products.
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
     * The URL of the top category.
     */
    private String url;

    /**
     * The sub categories of the category.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "topCategory")
    List<SubCategory> subCategories;

    /**
     * The products of the top category.
     */
    @OneToMany
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

    public List<SubCategory> getSubCategories()
    {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories)
    {
        this.subCategories = subCategories;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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
}
