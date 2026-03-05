package com.xceptance.posters.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.model.Product;
import com.xceptance.posters.repository.ProductRepository;
import com.xceptance.posters.service.LuceneSearchService;

/**
 * Handles product search queries and HTMX search suggestions.
 * Uses Lucene for full-text search with language-specific stemming.
 */
@Controller
public class SearchController
{
    private static final int SUGGEST_LIMIT = 5;

    private final ProductRepository productRepository;
    private final LuceneSearchService luceneSearchService;

    public SearchController(ProductRepository productRepository,
                            LuceneSearchService luceneSearchService)
    {
        this.productRepository = productRepository;
        this.luceneSearchService = luceneSearchService;
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

        List<Product> results = findByLucene(searchText, locale);
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

        List<Product> results = findByLucene(searchText, locale);
        List<Product> suggestions = results.stream().limit(SUGGEST_LIMIT).toList();

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("totalCount", results.size());
        model.addAttribute("searchText", searchText);
        return "fragments/searchSuggestFragment";
    }

    /**
     * Searches the Lucene index for the given locale and returns
     * Product entities in score order.
     */
    private List<Product> findByLucene(String searchText, String locale)
    {
        List<Integer> productIds = luceneSearchService.search(searchText, locale, 100);
        if (productIds.isEmpty())
        {
            return List.of();
        }

        // Fetch products and preserve Lucene score order
        List<Product> products = new ArrayList<>(productIds.size());
        for (int id : productIds)
        {
            productRepository.findById(id).ifPresent(products::add);
        }
        return products;
    }
}
