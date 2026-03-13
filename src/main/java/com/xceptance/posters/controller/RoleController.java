package com.xceptance.posters.controller;

import com.xceptance.posters.entity.RoleRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Admin > Roles submodule. Read-only — roles are seeded and immutable.
 */
@Controller
@RequestMapping("/backoffice/admin/roles")
public class RoleController extends AbstractBackofficeController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "backoffice/admin/roles/list";
    }
}
