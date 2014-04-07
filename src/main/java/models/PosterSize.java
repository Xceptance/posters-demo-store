package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * This {@link Entity} provides the size of a poster. Each poster is available in different sizes. A size is defined by
 * its width and height.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "posterSize")
public class PosterSize
{

    /**
     * The ID of entity.
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
     * Returns the width of the poster.
     * 
     * @return the width of the poster
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Sets the width of the poster.
     * 
     * @param width
     *            the width of the poster
     */
    public void setWidth(final int width)
    {
        this.width = width;
    }

    /**
     * Returns the height of the poster.
     * 
     * @return the height of the poster
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the height of the poster.
     * 
     * @param height
     *            the height of the poster
     */
    public void setHeight(final int height)
    {
        this.height = height;
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
}
