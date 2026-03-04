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
 * Handles product search queries and HTMX search suggestions.
 */
@Controller
public class SearchController
{
    private static final int SUGGEST_LIMIT = 5;

    private final ProductRepository productRepository;

    public SearchController(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @GetMapping("/{locale}/search")
    public String search(@PathVariable("locale") String locale,
                         @RequestParam(value = "q", required = false) String searchText,
                         Model model)
    {
        if (searchText == null || searchText.isBlank())
        {
            model.addAttribute("products", List.of());
            model.addAttribute("searchText", "");
            model.addAttribute("totalCount", 0);
            return "search/searchResult";
        }

        List<Product> results = findByName(searchText);
        model.addAttribute("products", results);
        model.addAttribute("searchText", searchText);
        model.addAttribute("totalCount", results.size());
        return "search/searchResult";
    }

    /**
     * HTMX search suggestions — returns a dropdown fragment with top 5 matches.
     */
    @GetMapping("/{locale}/searchSuggest")
    public String searchSuggest(@PathVariable("locale") String locale,
                                @RequestParam(value = "q", required = false) String searchText,
                                Model model)
    {
        if (searchText == null || searchText.isBlank())
        {
            model.addAttribute("suggestions", List.of());
            model.addAttribute("searchText", "");
            return "fragments/searchSuggestFragment";
        }

        List<Product> results = findByName(searchText);
        List<Product> suggestions = results.stream().limit(SUGGEST_LIMIT).toList();

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("totalCount", results.size());
        model.addAttribute("searchText", searchText);
        return "fragments/searchSuggestFragment";
    }

    private List<Product> findByName(String searchText)
    {
        return productRepository.findAll().stream()
                .filter(p -> p.getDefaultName() != null &&
                        p.getDefaultName().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
    }
}
