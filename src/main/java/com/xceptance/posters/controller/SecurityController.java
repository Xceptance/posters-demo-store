package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Stub controller for the Security module and its submodules.
 * Users, Roles, Audit Log are handled by their dedicated controllers.
 * Import/Export and Settings are stubs.
 */
@Controller
@RequestMapping("/backoffice/security")
public class SecurityController extends AbstractBackofficeController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("moduleTitle", "Security");
        model.addAttribute("moduleIcon", "admin_panel_settings");
        return "backoffice/placeholder";
    }

    @GetMapping("/import-export")
    public String importExport(Model model) {
        model.addAttribute("moduleTitle", "Security Import / Export");
        model.addAttribute("moduleIcon", "sync_alt");
        return "backoffice/placeholder";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("moduleTitle", "Security Settings");
        model.addAttribute("moduleIcon", "settings");
        return "backoffice/placeholder";
    }
}
