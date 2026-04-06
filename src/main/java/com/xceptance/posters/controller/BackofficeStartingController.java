package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Serves the "Starting up..." holding page shown while AdminDataLoader is seeding.
 * This endpoint is always accessible (excluded from the readiness check).
 */
@Controller
@RequestMapping("/backoffice/starting")
public class BackofficeStartingController {

    @GetMapping
    public String starting() {
        return "backoffice/starting";
    }
}
