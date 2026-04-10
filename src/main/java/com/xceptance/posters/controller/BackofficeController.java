package com.xceptance.posters.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for the backoffice — login page, dashboard, and error pages.
 * Extends AbstractBackofficeController to inherit adminDisplayName + currentUri population.
 */
@Controller
@RequestMapping("/backoffice")
public class BackofficeController extends AbstractBackofficeController {

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

    /**
     * Catch-all: any unmapped /backoffice/** URL returns 404 within the backoffice layout.
     * Without this, unmapped URLs fall through to the storefront filter chain.
     */
    @GetMapping("/**")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(HttpServletRequest request, Model model) {
        model.addAttribute("statusCode", 404);
        model.addAttribute("statusText", "Not Found");
        model.addAttribute("errorMessage", "The requested page does not exist.");
        model.addAttribute("requestUri", request.getRequestURI());
        return "backoffice/error/error";
    }
}
