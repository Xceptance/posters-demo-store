package com.xceptance.posters.controller;

import java.util.List;

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
                              Model model)
    {
        TopCategory category = topCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> allProducts = productRepository.findByTopCategory(category);
        addPaginatedProducts(allProducts, page, model);
        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getName().getText(locale));
        model.addAttribute("categoryPath", "topCategory");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categorySlug", name);
        return "catalog/categoryOverview";
    }

    @GetMapping("/{locale}/category/{name}")
    public String subCategory(@PathVariable("locale") String locale,
                              @PathVariable("name") String name,
                              @RequestParam("categoryId") int categoryId,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              Model model)
    {
        SubCategory category = subCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> allProducts = productRepository.findBySubCategory(category);
        addPaginatedProducts(allProducts, page, model);
        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getName().getText(locale));
        model.addAttribute("categoryPath", "category");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categorySlug", name);
        return "catalog/categoryOverview";
    }

    @GetMapping("/{locale}/product/{name}")
    public String productDetail(@PathVariable("locale") String locale,
                                @PathVariable("name") String name,
                                @RequestParam("productId") int productId,
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
}
