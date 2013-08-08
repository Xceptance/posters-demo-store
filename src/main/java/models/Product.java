package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

/**
 * A product belongs to one sub category. It has different informations like name, price and a description.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "product")
public class Product
{

    /**
     * The ID of the product.
     */
    @Id
    private int id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The URL of the product.
     */
    private String url;

    /**
     * The netto price of the product.
     */
    private double price;

    /**
     * A detailed description of the product.
     */
    @Column(length = 4096)
    private String descriptionDetail;

    /**
     * A overview description of the product.
     */
    @Column(length = 1024)
    private String descriptionOverview;

    /**
     * The url of the product image.
     */
    private String imageURL;

    /**
     * Defines, whether or not the product is shown in the carousel at the main page.
     */
    private boolean showInCarousel;

    /**
     * Defines, whether or not the product is shown in the top category overview.
     */
    private boolean showInTopCategorie;

    /**
     * The sub category, the product belongs to.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subCategory_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product")
    private List<Basket_Product> basket;

    @OneToMany(mappedBy = "product")
    private List<Order_Product> order;

    public Product()
    {
        this.basket = new ArrayList<Basket_Product>();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescriptionOverview()
    {
        return descriptionOverview;
    }

    public void setDescriptionOverview(String descriptionOverview)
    {
        this.descriptionOverview = descriptionOverview;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public void setImageURL(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getDescriptionDetail()
    {
        return descriptionDetail;
    }

    public void setDescriptionDetail(String descriptionDetail)
    {
        this.descriptionDetail = descriptionDetail;
    }

    public SubCategory getSubCategory()
    {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory)
    {
        this.subCategory = subCategory;
    }

    public boolean isShowInCarousel()
    {
        return showInCarousel;
    }

    public void setShowInCarousel(boolean showInCarousel)
    {
        this.showInCarousel = showInCarousel;
    }

    public boolean isShowInTopCategorie()
    {
        return showInTopCategorie;
    }

    public void setShowInTopCategorie(boolean showInTopCategorie)
    {
        this.showInTopCategorie = showInTopCategorie;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<Basket_Product> getBasket()
    {
        return basket;
    }

    public void setBasket(List<Basket_Product> basket)
    {
        this.basket = basket;
    }

    public List<Order_Product> getOrder()
    {
        return order;
    }

    public void setOrder(List<Order_Product> order)
    {
        this.order = order;
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
