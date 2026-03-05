package com.xceptance.posters.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom error controller that redirects to the default locale homepage
 * instead of showing Spring's default error page. This prevents "/error"
 * from appearing as a locale segment in the URL.
 */
@Controller
public class CustomErrorController implements ErrorController
{
    @RequestMapping("/error")
    public String handleError()
    {
        return "redirect:/en-US/";
    }
}
