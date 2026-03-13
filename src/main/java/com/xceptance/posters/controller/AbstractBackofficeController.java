package com.xceptance.posters.controller;

import com.xceptance.posters.config.AdminUserPrincipal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Base controller for backoffice pages.
 * Populates adminDisplayName for all handler methods.
 */
public abstract class AbstractBackofficeController {

    @ModelAttribute
    public void populateBackofficeModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminUserPrincipal principal) {
            model.addAttribute("adminDisplayName", principal.getDisplayName());
        } else {
            model.addAttribute("adminDisplayName", "Admin");
        }
    }
}
