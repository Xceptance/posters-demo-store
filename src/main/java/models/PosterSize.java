package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "posterSize")
public class PosterSize
{

    /**
     * The id of the poster size.
     */
    @Id
    private int id;

    /**
     * The width of the poster.
     */
    private int width;

    /**
     * The height of the poster.
     */
    private int height;

    /**
     * The products which are available in this size.
     */
    @OneToMany(mappedBy = "size")
    private List<Product_PosterSize> products;

    public PosterSize()
    {
        this.products = new ArrayList<Product_PosterSize>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public List<Product_PosterSize> getProducts()
    {
        return products;
    }

    public void setProducts(List<Product_PosterSize> products)
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
