package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Stub controller for the Orders module and all its submodules.
 * Full implementations TBD in future changes.
 */
@Controller
@RequestMapping("/backoffice/orders")
public class OrdersModuleController extends AbstractBackofficeController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("moduleTitle", "Orders");
        model.addAttribute("moduleIcon", "receipt_long");
        return "backoffice/placeholder";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("moduleTitle", "Orders Dashboard");
        model.addAttribute("moduleIcon", "dashboard");
        return "backoffice/placeholder";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("moduleTitle", "Orders");
        model.addAttribute("moduleIcon", "list_alt");
        return "backoffice/placeholder";
    }

    @GetMapping("/export")
    public String export(Model model) {
        model.addAttribute("moduleTitle", "Orders Export");
        model.addAttribute("moduleIcon", "download");
        return "backoffice/placeholder";
    }
}
