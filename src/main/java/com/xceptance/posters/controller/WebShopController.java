package com.xceptance.posters.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.xceptance.posters.model.Product;
import com.xceptance.posters.repository.ProductRepository;

/**
 * Handles the storefront homepage.
 */
@Controller
public class WebShopController
{
    private final ProductRepository productRepository;

    public WebShopController(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String indexRedirect()
    {
        return "redirect:/en-US/";
    }

    @GetMapping("/{locale}/")
    public String index(@PathVariable String locale, Model model)
    {
        // Carousel products
        List<Product> carousel = productRepository.findByShowInCarouselTrue();
        model.addAttribute("carousel", carousel);

        // Featured products
        List<Product> productsList = new ArrayList<>();
        for (int id : new int[]{6, 15, 33, 27, 44, 54, 65, 76, 81, 90, 103, 115})
        {
            productRepository.findById(id).ifPresent(productsList::add);
        }
        model.addAttribute("productslist", productsList);

        // Category representative products
        List<Product> productsListCat = new ArrayList<>();
        for (int id : new int[]{3, 44, 76, 111})
        {
            productRepository.findById(id).ifPresent(productsListCat::add);
        }
        model.addAttribute("productslistcat", productsListCat);

        return "index";
    }
}
