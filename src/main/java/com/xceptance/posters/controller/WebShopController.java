package com.xceptance.posters.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.xceptance.posters.entity.CatalogProductRepository;
import com.xceptance.posters.entity.CatalogService;
import com.xceptance.posters.entity.LocalizedTextService;
import com.xceptance.posters.entity.Product;

/**
 * Handles the storefront homepage.
 * Uses the new entity model (Product, Category, Variant, Price).
 */
@Controller
public class WebShopController
{
    private final CatalogProductRepository catalogProductRepository;
    private final CatalogService catalogService;
    private final LocalizedTextService textService;

    @PersistenceContext
    private EntityManager em;

    public WebShopController(CatalogProductRepository catalogProductRepository,
                             CatalogService catalogService,
                             LocalizedTextService textService)
    {
        this.catalogProductRepository = catalogProductRepository;
        this.catalogService = catalogService;
        this.textService = textService;
    }

    /**
     * Simple DTO for product tiles on the homepage.
     */
    public record HomeProductDto(int id, String name, String descriptionOverview,
                                  String imageURL, String largeImageURL,
                                  BigDecimal minimumPrice) {}

    /**
     * Simple DTO for category tiles on the homepage.
     */
    public static class CategoryDto {
        private final int id;
        private final String name;
        private final String imagePath;

        public CategoryDto(int id, String name, String imagePath) {
            this.id = id;
            this.name = name;
            this.imagePath = imagePath;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getImagePath() { return imagePath; }
    }

    @GetMapping("/")
    public String indexRedirect()
    {
        return "redirect:/en-US/";
    }

    @GetMapping("/{locale}/")
    public String index(@PathVariable String locale, Model model)
    {
        String currency = getCurrencyForLocale(locale);

        // Carousel products (those flagged showInCarousel)
        List<Product> carouselEntities = catalogProductRepository.findByShowInCarouselTrue();
        List<HomeProductDto> carousel = carouselEntities.stream()
            .map(p -> toDto(p, locale, currency))
            .toList();
        model.addAttribute("carousel", carousel);

        // Featured products — pick first 12 products from the catalog
        List<Product> allProducts = catalogProductRepository.findAll();
        List<HomeProductDto> featured = allProducts.stream()
            .limit(12)
            .map(p -> toDto(p, locale, currency))
            .toList();
        model.addAttribute("productslist", featured);

        // Top categories
        List<CategoryDto> topCategories = catalogService.getTopCategories().stream()
            .map(c -> new CategoryDto(
                c.getId(),
                textService.getText(c.getNameTextId(), locale),
                c.getImagePath()
            ))
            .toList();
        model.addAttribute("categoryTiles", topCategories);

        return "index";
    }

    private HomeProductDto toDto(Product p, String locale, String currency)
    {
        return new HomeProductDto(
            p.getId(),
            textService.getText(p.getNameTextId(), locale),
            textService.getText(p.getDescriptionOverviewTextId(), locale),
            p.getMediumImageUrl(),
            p.getLargeImageUrl(),
            getMinimumPrice(p, currency)
        );
    }

    private BigDecimal getMinimumPrice(Product product, String currency)
    {
        try
        {
            List<BigDecimal> prices = em.createQuery(
                "SELECT p.price FROM Price p WHERE p.sku IN " +
                "(SELECT CONCAT(pr.sku, '-', LPAD(CAST(v.variantNumber AS string), 4, '0')) " +
                " FROM Variant v JOIN v.product pr WHERE pr.id = :productId) " +
                "AND p.priceTable.id IN (SELECT s.priceTable.id FROM Site s WHERE s.currency = :currency)",
                BigDecimal.class)
                .setParameter("productId", product.getId())
                .setParameter("currency", currency)
                .getResultList();
            if (!prices.isEmpty())
            {
                return prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            }
        }
        catch (Exception e)
        {
            // ignore
        }
        return BigDecimal.ZERO;
    }

    private String getCurrencyForLocale(String locale)
    {
        try
        {
            List<String> results = em.createQuery(
                "SELECT s.currency FROM Site s WHERE s.mainLocale.locale = :locale", String.class)
                .setParameter("locale", locale)
                .getResultList();
            if (!results.isEmpty()) return results.get(0);
        }
        catch (Exception e)
        {
            // ignore
        }
        if (locale.startsWith("de")) return "EUR";
        if (locale.equals("en-GB")) return "GBP";
        if (locale.equals("sv-SE")) return "SEK";
        return "USD";
    }
}
