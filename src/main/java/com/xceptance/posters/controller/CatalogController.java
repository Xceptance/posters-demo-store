package com.xceptance.posters.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.model.Product;
import com.xceptance.posters.model.SubCategory;
import com.xceptance.posters.model.TopCategory;
import com.xceptance.posters.repository.ProductRepository;
import com.xceptance.posters.repository.SubCategoryRepository;
import com.xceptance.posters.repository.TopCategoryRepository;

/**
 * Handles product catalog browsing: category listings and product detail pages.
 */
@Controller
public class CatalogController
{
    private static final int PAGE_SIZE = 12;

    private final ProductRepository productRepository;
    private final TopCategoryRepository topCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PostersProperties props;

    public CatalogController(ProductRepository productRepository,
                             TopCategoryRepository topCategoryRepository,
                             SubCategoryRepository subCategoryRepository,
                             PostersProperties props)
    {
        this.productRepository = productRepository;
        this.topCategoryRepository = topCategoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.props = props;
    }

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
        TopCategory category = topCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> allProducts = productRepository.findByTopCategory(category);
        
        applyFiltersAndModelAttributes(allProducts, finishes, sizes, minPrice, maxPrice, page, model, locale);

        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getName().getText(locale));
        model.addAttribute("categoryPath", "topCategory");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categorySlug", name);
        
        return isHtmxRequest(request) ? "fragments/productGridFragment" : "catalog/categoryOverview";
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
        SubCategory category = subCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> allProducts = productRepository.findBySubCategory(category);
        
        applyFiltersAndModelAttributes(allProducts, finishes, sizes, minPrice, maxPrice, page, model, locale);

        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getName().getText(locale));
        model.addAttribute("categoryPath", "category");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categorySlug", name);
        
        return isHtmxRequest(request) ? "fragments/productGridFragment" : "catalog/categoryOverview";
    }

    private void applyFiltersAndModelAttributes(List<Product> allProducts, List<String> finishes, List<String> sizes, Double minPrice, Double maxPrice, int page, Model model, String locale)
    {
        java.util.Set<String> availableFinishes = new java.util.TreeSet<>();
        java.util.Set<String> availableSizes = new java.util.TreeSet<>();
        double absoluteMinPrice = Double.MAX_VALUE;
        double absoluteMaxPrice = 0.0;

        for (Product p : allProducts) {
            availableFinishes.addAll(p.getAvailableFinishesList());
            for (com.xceptance.posters.model.ProductPosterSize pps : p.getAvailableSizes()) {
                availableSizes.add(pps.getSize().getWidth() + " x " + pps.getSize().getHeight());
            }
            double price = p.getMinimumPrice(locale);
            if (price < absoluteMinPrice) {
                absoluteMinPrice = price;
            }
            if (price > absoluteMaxPrice) {
                absoluteMaxPrice = price;
            }
        }

        if (allProducts.isEmpty()) {
            absoluteMinPrice = 0.0;
            absoluteMaxPrice = 0.0;
        }

        // Use Math.floor and Math.ceil for nice bounds
        absoluteMinPrice = Math.floor(absoluteMinPrice);
        absoluteMaxPrice = Math.ceil(absoluteMaxPrice);

        List<Product> filteredProducts = allProducts;
        if ((finishes != null && !finishes.isEmpty()) || (sizes != null && !sizes.isEmpty()) || minPrice != null || maxPrice != null) {
            filteredProducts = allProducts.stream().filter(p -> {
                boolean matchFinish = true;
                if (finishes != null && !finishes.isEmpty()) {
                    matchFinish = p.getAvailableFinishesList().stream().anyMatch(finishes::contains);
                }
                boolean matchSize = true;
                if (sizes != null && !sizes.isEmpty()) {
                    matchSize = p.getAvailableSizes().stream().anyMatch(s -> sizes.contains(s.getSize().getWidth() + " x " + s.getSize().getHeight()));
                }
                boolean matchPrice = true;
                double pPrice = p.getMinimumPrice(locale);
                if (minPrice != null && pPrice < minPrice) {
                    matchPrice = false;
                }
                if (maxPrice != null && pPrice > maxPrice) {
                    matchPrice = false;
                }
                return matchFinish && matchSize && matchPrice;
            }).collect(java.util.stream.Collectors.toList());
        }

        addPaginatedProducts(filteredProducts, page, model);
        
        model.addAttribute("availableFinishes", availableFinishes);
        model.addAttribute("availableSizes", availableSizes);
        model.addAttribute("selectedFinishes", finishes != null ? finishes : List.of());
        model.addAttribute("selectedSizes", sizes != null ? sizes : List.of());
        model.addAttribute("absoluteMinPrice", absoluteMinPrice);
        model.addAttribute("absoluteMaxPrice", absoluteMaxPrice);
        model.addAttribute("selectedMinPrice", minPrice != null ? minPrice : absoluteMinPrice);
        model.addAttribute("selectedMaxPrice", maxPrice != null ? maxPrice : absoluteMaxPrice);
    }

    @GetMapping("/{locale}/product/{name}/{productId}")
    public String productDetail(@PathVariable("locale") String locale,
                                @PathVariable("name") String name,
                                @PathVariable("productId") int productId,
                                Model model)
    {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null)
        {
            return "redirect:/" + locale + "/";
        }
        model.addAttribute("product", product);
        model.addAttribute("unitLength", props.getUnitOfLength());
        return "catalog/product";
    }

    /**
     * Paginate a product list and add pagination metadata to the model.
     */
    private void addPaginatedProducts(List<Product> allProducts, int page, Model model)
    {
        int totalProducts = allProducts.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalProducts / PAGE_SIZE));

        // Clamp page to valid range
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int fromIndex = (page - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalProducts);
        List<Product> pageProducts = allProducts.subList(fromIndex, toIndex);

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
