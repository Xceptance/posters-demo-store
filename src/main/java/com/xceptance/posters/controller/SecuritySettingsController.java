package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Admin > Security Settings submodule. Placeholder for now.
 */
@Controller
@RequestMapping("/backoffice/admin/security")
public class SecuritySettingsController extends AbstractBackofficeController {

    @GetMapping
    public String index() {
        return "backoffice/admin/security/index";
    }
}
