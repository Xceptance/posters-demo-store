package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Stub controller for the Catalog module and all its submodules.
 * Full implementations TBD in future changes.
 */
@Controller
@RequestMapping("/backoffice/catalog")
public class CatalogModuleController extends AbstractBackofficeController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("moduleTitle", "Catalog");
        model.addAttribute("moduleIcon", "inventory_2");
        return "backoffice/placeholder";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("moduleTitle", "Catalog Dashboard");
        model.addAttribute("moduleIcon", "dashboard");
        return "backoffice/placeholder";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("moduleTitle", "Categories");
        model.addAttribute("moduleIcon", "category");
        return "backoffice/placeholder";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("moduleTitle", "Products");
        model.addAttribute("moduleIcon", "deployed_code");
        return "backoffice/placeholder";
    }

    @GetMapping("/variations")
    public String variations(Model model) {
        model.addAttribute("moduleTitle", "Variations & Attributes");
        model.addAttribute("moduleIcon", "tune");
        return "backoffice/placeholder";
    }

    @GetMapping("/pricing")
    public String pricing(Model model) {
        model.addAttribute("moduleTitle", "Pricing");
        model.addAttribute("moduleIcon", "sell");
        return "backoffice/placeholder";
    }

    @GetMapping("/import-export")
    public String importExport(Model model) {
        model.addAttribute("moduleTitle", "Catalog Import / Export");
        model.addAttribute("moduleIcon", "sync_alt");
        return "backoffice/placeholder";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("moduleTitle", "Catalog Settings");
        model.addAttribute("moduleIcon", "settings");
        return "backoffice/placeholder";
    }
}
