package com.xceptance.posters.controller;

import com.xceptance.posters.config.AdminUserPrincipal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the backoffice — login page, dashboard, and error pages.
 */
@Controller
@RequestMapping("/backoffice")
public class BackofficeController {

    @ModelAttribute
    public void populateCommonModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminUserPrincipal principal) {
            model.addAttribute("adminDisplayName", principal.getDisplayName());
        } else {
            model.addAttribute("adminDisplayName", "Admin");
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "backoffice/login";
    }

    @GetMapping("/")
    public String dashboard() {
        return "backoffice/dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "backoffice/error/access-denied";
    }
}
