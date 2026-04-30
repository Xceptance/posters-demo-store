package com.xceptance.posters.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Handles exceptions thrown by backoffice controllers and renders
 * them within the backoffice layout instead of forwarding to /error.
 */
@ControllerAdvice(basePackages = "com.xceptance.posters.controller")
public class BackofficeExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BackofficeExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        log.error("Backoffice error: {}", ex.getMessage(), ex);

        model.addAttribute("statusCode", 500);
        model.addAttribute("statusText", "Internal Server Error");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.");
        model.addAttribute("exceptionMessage", ex.getClass().getSimpleName());
        model.addAttribute("requestUri", "");

        return "backoffice/error/error";
    }
}
