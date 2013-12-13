package models;

import javax.persistence.Entity;
import javax.persistence.Id;
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

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }
}
