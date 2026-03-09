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
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.entity.CatalogProductRepository;
import com.xceptance.posters.entity.LocalizedTextService;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.service.LuceneSearchService;

/**
 * Handles product search queries and HTMX search suggestions.
 * Uses Lucene for full-text search with language-specific stemming.
 * Returns pre-resolved DTOs to templates.
 */
@Controller
public class SearchController
{
    private static final int SUGGEST_LIMIT = 5;

    private final CatalogProductRepository catalogProductRepository;
    private final LocalizedTextService textService;
    private final LuceneSearchService luceneSearchService;

    @PersistenceContext
    private EntityManager em;

    public SearchController(CatalogProductRepository catalogProductRepository,
                            LocalizedTextService textService,
                            LuceneSearchService luceneSearchService)
    {
        this.catalogProductRepository = catalogProductRepository;
        this.textService = textService;
        this.luceneSearchService = luceneSearchService;
    }

    /**
     * Simple DTO for search result product tiles.
     */
    public record SearchProductDto(int id, String name, String descriptionOverview,
                                    String imageURL, BigDecimal minimumPrice) {}

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

        String currency = getCurrencyForLocale(locale);
        List<SearchProductDto> results = findByLucene(searchText, locale, currency, 100);
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

        String currency = getCurrencyForLocale(locale);
        List<SearchProductDto> allResults = findByLucene(searchText, locale, currency, 100);
        List<SearchProductDto> suggestions = allResults.stream().limit(SUGGEST_LIMIT).toList();

        model.addAttribute("suggestions", suggestions);
        model.addAttribute("totalCount", allResults.size());
        model.addAttribute("searchText", searchText);
        return "fragments/searchSuggestFragment";
    }

    /**
     * Searches the Lucene index and returns resolved DTOs in score order.
     */
    private List<SearchProductDto> findByLucene(String searchText, String locale, String currency, int max)
    {
        List<Integer> productIds = luceneSearchService.search(searchText, locale, max);
        if (productIds.isEmpty())
        {
            return List.of();
        }

        List<SearchProductDto> results = new ArrayList<>(productIds.size());
        for (int id : productIds)
        {
            catalogProductRepository.findById(id).ifPresent(p -> {
                results.add(toDto(p, locale, currency));
            });
        }
        return results;
    }

    private SearchProductDto toDto(Product p, String locale, String currency)
    {
        return new SearchProductDto(
            p.getId(),
            textService.getText(p.getNameTextId(), locale),
            textService.getText(p.getDescriptionOverviewTextId(), locale),
            p.getMediumImageUrl(),
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
