package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Stub controller for the Customers module and all its submodules.
 * Full implementations TBD in future changes.
 */
@Controller
@RequestMapping("/backoffice/customers")
public class CustomersModuleController extends AbstractBackofficeController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("moduleTitle", "Customers");
        model.addAttribute("moduleIcon", "group");
        return "backoffice/placeholder";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("moduleTitle", "Customers Dashboard");
        model.addAttribute("moduleIcon", "dashboard");
        return "backoffice/placeholder";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("moduleTitle", "Customers");
        model.addAttribute("moduleIcon", "manage_accounts");
        return "backoffice/placeholder";
    }

    @GetMapping("/import-export")
    public String importExport(Model model) {
        model.addAttribute("moduleTitle", "Customers Import / Export");
        model.addAttribute("moduleIcon", "sync_alt");
        return "backoffice/placeholder";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("moduleTitle", "Customers Settings");
        model.addAttribute("moduleIcon", "settings");
        return "backoffice/placeholder";
    }
}
