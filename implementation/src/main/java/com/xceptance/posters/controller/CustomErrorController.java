package com.xceptance.posters.controller;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Custom error controller that handles application errors with user-friendly messages.
 * Provides specific handling for CSRF validation failures (session timeouts).
 *
 * @author AI Assistant (Claude 3.7 Sonnet)
 */
@Controller
public class CustomErrorController implements ErrorController
{
    /**
     * Handles error requests with optional reason parameter for specific error types.
     *
     * @param request The HTTP request
     * @param reason Optional reason code for specific error handling (e.g., "session-expired")
     * @param model The model for passing data to the view
     * @return View name or redirect
     */
    @RequestMapping("/error")
    public String handleError(final HttpServletRequest request,
                             @RequestParam(value = "reason", required = false) final String reason,
                             final Model model)
    {
        // Handle CSRF-specific errors (session timeout)
        if ("session-expired".equals(reason))
        {
            model.addAttribute("errorTitle", "Session Expired");
            model.addAttribute("errorMessage",
                "Your session has expired for security reasons. Please refresh the page and try again.");
            model.addAttribute("showRefresh", true);
            return "error/sessionExpired";
        }
        
        // For all other errors, redirect to homepage
        return "redirect:/en-US/";
    }
}
