package com.xceptance.posters.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.model.Product;
import com.xceptance.posters.repository.ProductRepository;

/**
 * Handles product search queries.
 */
@Controller
public class SearchController
{
    private final ProductRepository productRepository;

    public SearchController(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @GetMapping("/{locale}/search")
    public String search(@PathVariable String locale,
                         @RequestParam(required = false) String searchText,
                         Model model)
    {
        if (searchText == null || searchText.isBlank())
        {
            model.addAttribute("products", List.of());
            model.addAttribute("searchText", "");
            model.addAttribute("totalCount", 0);
            return "search/searchResult";
        }

        // Simple search: find products whose name contains the search text
        // TODO: Replace with Lucene search in Phase 7
        List<Product> allProducts = productRepository.findAll();
        List<Product> results = allProducts.stream()
                .filter(p -> p.getDefaultName() != null &&
                        p.getDefaultName().toLowerCase().contains(searchText.toLowerCase()))
                .toList();

        model.addAttribute("products", results);
        model.addAttribute("searchText", searchText);
        model.addAttribute("totalCount", results.size());
        return "search/searchResult";
    }
}
