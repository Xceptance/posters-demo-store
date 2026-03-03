package com.xceptance.posters.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/{locale}/topCategory/{categoryId}")
    public String topCategory(@PathVariable String locale,
                              @PathVariable int categoryId,
                              @RequestParam(defaultValue = "0") int page,
                              Model model)
    {
        TopCategory category = topCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> products = productRepository.findByTopCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getDefaultName());
        return "catalog/categoryOverview";
    }

    @GetMapping("/{locale}/subCategory/{categoryId}")
    public String subCategory(@PathVariable String locale,
                              @PathVariable int categoryId,
                              Model model)
    {
        SubCategory category = subCategoryRepository.findById(categoryId).orElse(null);
        if (category == null)
        {
            return "redirect:/" + locale + "/";
        }
        List<Product> products = productRepository.findBySubCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("categoryName", category.getDefaultName());
        return "catalog/categoryOverview";
    }

    @GetMapping("/{locale}/productDetail/{productId}")
    public String productDetail(@PathVariable String locale,
                                @PathVariable int productId,
                                Model model)
    {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null)
        {
            return "redirect:/" + locale + "/";
        }
        model.addAttribute("product", product);
        return "catalog/productDetail";
    }
}
