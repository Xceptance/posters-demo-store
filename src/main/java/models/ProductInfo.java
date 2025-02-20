package models;

import java.util.List;

/*
 * Data of a product for info purposes
 */
public class ProductInfo {
    /**
     * The ID of the entity.
     */
    private int id;

    /**
     * The name of the product.
     */
    private DefaultText name;

    /**
     * A long description of the product.
     */
    private DefaultText descriptionDetail;

    /**
     * A short description of the product.
     */
    private DefaultText descriptionOverview;

    /**
     * The {@link ProductPosterSize}s which are available for this product.
     */
    private List<ProductPosterSize> availableSizes;

    /**
     * The URL to the product image.
     */
    private String imageURL;

     /**
     * The URL to the product small image.
     */
    private String smallImageURL;

    /**
     * The URL to the product medium image.
     */
    private String mediumImageURL;

    /**
     * The URL to the product large image.
     */
    private String largeImageURL;

    /**
     * The URL to the product large image.
     */
    private String originalImageURL;

    /**
     * Defines, whether or not the product is shown in the carousel on the main page.
     */
    private boolean showInCarousel;

    /**
     * Defines, whether or not the product is shown on the top category overview page.
     */
    private boolean showInTopCategorie;

    /**
     * The sub category, the product belongs to.
     */
    private SubCategory subCategory;

    /**
     * The top category, the product belongs to.
     */
    private TopCategory topCategory;

    /**
     * A list of {@link CartProduct}s.
     */
    private List<CartProduct> cart;

    /**
     * A list of {@link OrderProduct}s.
     */
    private List<OrderProduct> order;

    /**
     * The minimum price of the product. The price of a product depends on the selected size.
     */
    private double minimumPrice;

    /**
     * The name in the current localization
     */
    private String localizedName;

    /**
     * The short description in the current localization
     */
    private String localizedDescriptionOverview;

    /**
     * The long description in the current localization
     */
    private String localizedDescriptionDetail;

    /**
     * Constructor.
     */
    public ProductInfo(Product product, String locale) {
        this.id = product.getId();
        this.name = product.getName();
        this.descriptionOverview = product.getDescriptionOverview();
        this.descriptionDetail = product.getDescriptionDetail();
        this.availableSizes = product.getAvailableSizes();
        this.imageURL = product.getImageURL();
        this.smallImageURL = product.getSmallImageURL();
        this.mediumImageURL = product.getMediumImageURL();
        this.largeImageURL = product.getLargeImageURL();
        this.originalImageURL = product.getOriginalImageURL();
        this.showInCarousel = product.isShowInCarousel();
        this.showInTopCategorie = product.isShowInTopCategorie();
        this.subCategory = product.getSubCategory();
        this.topCategory = product.getTopCategory();
        this.cart = product.getCart();
        this.order = product.getOrder();
        this.minimumPrice = product.getMinimumPrice();
        this.localizedName = product.getName(locale);
        this.localizedDescriptionOverview = product.getDescriptionOverview(locale);
        this.localizedDescriptionDetail = product.getDescriptionDetail(locale);
    }

    public int getId() {
        return id;
    }

    public DefaultText getName() {
        return name;
    }

    public String getName(String locale) {
        return localizedName;
    }

    public DefaultText getDescriptionOverview() {
        return descriptionOverview;
    }

    public String getDescriptionOverview(String locale) {
        return localizedDescriptionOverview;
    }

    public DefaultText getDescriptionDetail() {
        return descriptionDetail;
    }

    public String getDescriptionDetail(String locale) {
        return localizedDescriptionDetail;
    }

    public List<ProductPosterSize> getAvailableSizes() {
        return availableSizes;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public String getMediumImageURL() {
        return mediumImageURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public String getOriginalImageURL() {
        return originalImageURL;
    }

    public boolean isShowInCarousel() {
        return showInCarousel;
    }

    public boolean isShowInTopCategorie() {
        return showInTopCategorie;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public TopCategory getTopCategory() {
        return topCategory;
    }

    public List<CartProduct> getCart() {
        return cart;
    }

    public List<OrderProduct> getOrder() {
        return order;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getLocalizedDescriptionOverview() {
        return localizedDescriptionOverview;
    }

    public String getLocalizedDescriptionDetail() {
        return localizedDescriptionDetail;
    }
}
