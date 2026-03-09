package com.xceptance.posters.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CatalogService;
import com.xceptance.posters.entity.Category;
import com.xceptance.posters.entity.LocalizedTextService;
import com.xceptance.posters.entity.Price;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.entity.Variant;
import com.xceptance.posters.entity.VariationAttributeValue;

/**
 * Handles product catalog browsing: category listings and product detail pages.
 * Uses the new entity model (Category, Product, Variant, Price).
 */
@Controller
public class CatalogController
{
    private static final int PAGE_SIZE = 12;

    private final CatalogService catalogService;
    private final LocalizedTextService textService;
    private final PostersProperties props;

    @PersistenceContext
    private EntityManager em;

    public CatalogController(CatalogService catalogService,
                             LocalizedTextService textService,
                             PostersProperties props)
    {
        this.catalogService = catalogService;
        this.textService = textService;
        this.props = props;
    }

    // ─── DTOs to carry resolved data to templates ───

    /**
     * Pre-resolved product data for listing templates.
     */
    public record ProductDto(int id, String name, String descriptionOverview,
                             String imageURL, BigDecimal minimumPrice) {}

    /**
     * Pre-resolved product data for detail page.
     */
    public record ProductDetailDto(int id, String name, String descriptionOverview,
                                    String descriptionDetail, String largeImageURL,
                                    BigDecimal minimumPrice,
                                    List<VariantDto> variants,
                                    List<String> availableFinishes,
                                    List<SizeDto> distinctSizes) {}

    public record VariantDto(int id, String sku, String size, String finish, BigDecimal price) {}
    public record SizeDto(String label) {}

    // ─── Category browsing ───

    @GetMapping("/{locale}/topCategory/{name}")
    public String topCategory(@PathVariable("locale") String locale,
                              @PathVariable("name") String name,
                              @RequestParam("categoryId") int categoryId,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "finish", required = false) List<String> finishes,
                              @RequestParam(value = "size", required = false) List<String> sizes,
                              @RequestParam(value = "minPrice", required = false) Double minPrice,
                              @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                              HttpServletRequest request,
                              Model model)
    {
        return handleCategory(locale, name, categoryId, "topCategory", page, finishes, sizes, minPrice, maxPrice, request, model);
    }

    @GetMapping("/{locale}/category/{name}")
    public String subCategory(@PathVariable("locale") String locale,
                              @PathVariable("name") String name,
                              @RequestParam("categoryId") int categoryId,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "finish", required = false) List<String> finishes,
                              @RequestParam(value = "size", required = false) List<String> sizes,
                              @RequestParam(value = "minPrice", required = false) Double minPrice,
                              @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                              HttpServletRequest request,
                              Model model)
    {
        return handleCategory(locale, name, categoryId, "category", page, finishes, sizes, minPrice, maxPrice, request, model);
    }

    private String handleCategory(String locale, String name, int categoryId, String categoryPath,
                                   int page, List<String> finishes, List<String> sizes,
                                   Double minPrice, Double maxPrice,
                                   HttpServletRequest request, Model model)
    {
        Category category = catalogService.getCategoryById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }

        String currency = getCurrencyForLocale(locale);
        List<Product> entityProducts = catalogService.getProductsByCategory(categoryId);

        // Also include products from child categories (sub-categories)
        if ("topCategory".equals(categoryPath))
        {
            List<Category> subCategories = catalogService.getSubCategories(categoryId);
            for (Category sub : subCategories)
            {
                entityProducts.addAll(catalogService.getProductsByCategory(sub.getId()));
            }
            // Remove duplicates (same product might be in both top and sub)
            entityProducts = entityProducts.stream().distinct().collect(Collectors.toList());
        }

        // Convert to DTOs with resolved names and prices
        List<ProductDto> allProducts = new ArrayList<>();
        Set<String> availableFinishes = new TreeSet<>();
        Set<String> availableSizes = new TreeSet<>();
        BigDecimal absoluteMinPrice = null;
        BigDecimal absoluteMaxPrice = null;

        for (Product p : entityProducts)
        {
            String productName = textService.getText(p.getNameTextId(), locale);
            String productDesc = textService.getText(p.getDescriptionOverviewTextId(), locale);
            String imageUrl = p.getMediumImageUrl();

            // Get minimum price from variants
            BigDecimal productMinPrice = getMinimumPrice(p, currency);

            allProducts.add(new ProductDto(p.getId(), productName, productDesc, imageUrl, productMinPrice));

            // Collect filter values from variants
            for (Variant v : p.getVariants())
            {
                for (VariationAttributeValue vav : v.getAttributeValues())
                {
                    String attrName = vav.getAttribute().getName();
                    if ("finish".equalsIgnoreCase(attrName))
                    {
                        availableFinishes.add(vav.getValue());
                    }
                    else if ("size".equalsIgnoreCase(attrName))
                    {
                        availableSizes.add(vav.getValue());
                    }
                }
            }

            // Track absolute price range for filter
            if (productMinPrice != null)
            {
                if (absoluteMinPrice == null || productMinPrice.compareTo(absoluteMinPrice) < 0)
                {
                    absoluteMinPrice = productMinPrice;
                }
                if (absoluteMaxPrice == null || productMinPrice.compareTo(absoluteMaxPrice) > 0)
                {
                    absoluteMaxPrice = productMinPrice;
                }
            }
        }

        if (absoluteMinPrice == null) absoluteMinPrice = BigDecimal.ZERO;
        if (absoluteMaxPrice == null) absoluteMaxPrice = BigDecimal.ZERO;
        absoluteMinPrice = absoluteMinPrice.setScale(0, RoundingMode.FLOOR);
        absoluteMaxPrice = absoluteMaxPrice.setScale(0, RoundingMode.CEILING);

        // Apply filters
        final BigDecimal filterMin = absoluteMinPrice;
        final BigDecimal filterMax = absoluteMaxPrice;
        List<ProductDto> filteredProducts = allProducts;

        if ((finishes != null && !finishes.isEmpty()) || (sizes != null && !sizes.isEmpty())
            || minPrice != null || maxPrice != null)
        {
            // For finish/size filtering we need to check the entity products
            // Build a map of productId -> entity product for filter checks
            var entityMap = entityProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

            filteredProducts = allProducts.stream().filter(dto -> {
                Product entity = entityMap.get(dto.id());
                if (entity == null) return false;

                boolean matchFinish = true;
                if (finishes != null && !finishes.isEmpty())
                {
                    Set<String> productFinishes = entity.getVariants().stream()
                        .flatMap(v -> v.getAttributeValues().stream())
                        .filter(vav -> "finish".equalsIgnoreCase(vav.getAttribute().getName()))
                        .map(VariationAttributeValue::getValue)
                        .collect(Collectors.toSet());
                    matchFinish = productFinishes.stream().anyMatch(finishes::contains);
                }

                boolean matchSize = true;
                if (sizes != null && !sizes.isEmpty())
                {
                    Set<String> productSizes = entity.getVariants().stream()
                        .flatMap(v -> v.getAttributeValues().stream())
                        .filter(vav -> "size".equalsIgnoreCase(vav.getAttribute().getName()))
                        .map(VariationAttributeValue::getValue)
                        .collect(Collectors.toSet());
                    matchSize = productSizes.stream().anyMatch(sizes::contains);
                }

                boolean matchPrice = true;
                if (dto.minimumPrice() != null)
                {
                    if (minPrice != null && dto.minimumPrice().compareTo(BigDecimal.valueOf(minPrice)) < 0)
                        matchPrice = false;
                    if (maxPrice != null && dto.minimumPrice().compareTo(BigDecimal.valueOf(maxPrice)) > 0)
                        matchPrice = false;
                }
                return matchFinish && matchSize && matchPrice;
            }).collect(Collectors.toList());
        }

        // Pagination
        addPaginatedProducts(filteredProducts, page, model);

        // Filter model attributes
        model.addAttribute("availableFinishes", availableFinishes);
        model.addAttribute("availableSizes", availableSizes);
        model.addAttribute("selectedFinishes", finishes != null ? finishes : List.of());
        model.addAttribute("selectedSizes", sizes != null ? sizes : List.of());
        model.addAttribute("absoluteMinPrice", absoluteMinPrice);
        model.addAttribute("absoluteMaxPrice", absoluteMaxPrice);
        model.addAttribute("selectedMinPrice", minPrice != null ? BigDecimal.valueOf(minPrice) : filterMin);
        model.addAttribute("selectedMaxPrice", maxPrice != null ? BigDecimal.valueOf(maxPrice) : filterMax);

        // Category info
        String categoryName = textService.getText(category.getNameTextId(), locale);
        model.addAttribute("category", category);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("categoryPath", categoryPath);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categorySlug", name);

        return isHtmxRequest(request) ? "fragments/productGridFragment" : "catalog/categoryOverview";
    }

    // ─── Product detail ───

    @GetMapping("/{locale}/product/{name}/{productId}")
    public String productDetail(@PathVariable("locale") String locale,
                                @PathVariable("name") String name,
                                @PathVariable("productId") int productId,
                                Model model)
    {
        Product entity = catalogService.getProductById(productId).orElse(null);
        if (entity == null)
        {
            return "redirect:/" + locale + "/";
        }

        String currency = getCurrencyForLocale(locale);
        String unitLength = (locale.startsWith("de") || locale.equals("en-GB") || locale.equals("sv-SE"))
            ? "cm" : props.getUnitOfLength();

        // Build variant DTOs
        List<VariantDto> variantDtos = new ArrayList<>();
        Set<String> finishSet = new TreeSet<>();
        Set<String> sizeSet = new TreeSet<>();

        for (Variant v : entity.getVariants())
        {
            BigDecimal varPrice = getVariantPrice(v, currency);
            String finish = "";
            String size = "";
            for (VariationAttributeValue vav : v.getAttributeValues())
            {
                String attrName = vav.getAttribute().getName();
                if ("finish".equalsIgnoreCase(attrName))
                {
                    finish = vav.getValue();
                    finishSet.add(finish);
                }
                else if ("size".equalsIgnoreCase(attrName))
                {
                    size = vav.getValue();
                    sizeSet.add(size);
                }
            }
            variantDtos.add(new VariantDto(v.getId(), v.getFullSku(),
                size.isEmpty() ? null : size + " " + unitLength,
                finish, varPrice));
        }

        // Build distinct size list for the dropdown
        List<SizeDto> distinctSizes = sizeSet.stream()
            .map(s -> new SizeDto(s + " " + unitLength))
            .collect(Collectors.toList());

        ProductDetailDto dto = new ProductDetailDto(
            entity.getId(),
            textService.getText(entity.getNameTextId(), locale),
            textService.getText(entity.getDescriptionOverviewTextId(), locale),
            textService.getText(entity.getDescriptionDetailTextId(), locale),
            entity.getLargeImageUrl(),
            getMinimumPrice(entity, currency),
            variantDtos,
            new ArrayList<>(finishSet),
            distinctSizes
        );

        model.addAttribute("product", dto);
        model.addAttribute("unitLength", unitLength);
        return "catalog/product";
    }

    // ─── Helpers ───

    /**
     * Map a locale code (e.g. "en-US") to its site currency.
     */
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
        // Fallback defaults
        if (locale.startsWith("de")) return "EUR";
        if (locale.equals("en-GB")) return "GBP";
        if (locale.equals("sv-SE")) return "SEK";
        return "USD";
    }

    /**
     * Get the minimum price across all variants of a product for the given currency.
     */
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
            // Fall through to simple query
        }

        // Simpler approach: query all prices for variants of this product
        BigDecimal min = null;
        for (Variant v : product.getVariants())
        {
            BigDecimal p = getVariantPrice(v, currency);
            if (p != null && (min == null || p.compareTo(min) < 0))
            {
                min = p;
            }
        }
        return min != null ? min : BigDecimal.ZERO;
    }

    /**
     * Get the price for a specific variant in the given currency.
     */
    private BigDecimal getVariantPrice(Variant variant, String currency)
    {
        String variantSku = variant.getFullSku();
        if (variantSku == null) return BigDecimal.ZERO;

        try
        {
            List<BigDecimal> results = em.createQuery(
                "SELECT p.price FROM Price p WHERE p.sku = :sku " +
                "AND p.priceTable.id IN (SELECT s.priceTable.id FROM Site s WHERE s.currency = :currency)",
                BigDecimal.class)
                .setParameter("sku", variantSku)
                .setParameter("currency", currency)
                .getResultList();
            if (!results.isEmpty())
            {
                return results.get(0);
            }
        }
        catch (Exception e)
        {
            // ignore
        }
        return BigDecimal.ZERO;
    }

    private void addPaginatedProducts(List<ProductDto> allProducts, int page, Model model)
    {
        int totalProducts = allProducts.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalProducts / PAGE_SIZE));

        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalProducts);
        List<ProductDto> pageProducts = allProducts.subList(fromIndex, toIndex);

        model.addAttribute("products", pageProducts);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", PAGE_SIZE);
    }

    private boolean isHtmxRequest(HttpServletRequest request)
    {
        return "true".equals(request.getHeader("HX-Request"));
    }
}
