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
 * The sub category belongs to one {@link TopCategory}. A sub category has different products.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "subCategory")
public class SubCategory
{

    /**
     * The ID of the sub category.
     */
    @Id
    private int id;

    /**
     * The name of the sub category.
     */
    private String name;

    /**
     * The URL of the sub category.
     */
    private String url;

    /**
     * The top category of the sub category.
     */
    @ManyToOne
    @JoinColumn(name = "topCategory_id")
    private TopCategory topCategory;

    /**
     * The products of the sub category.
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

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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
