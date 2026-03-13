package com.xceptance.posters.controller;

import com.xceptance.posters.config.BackofficeModule;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.entity.RoleRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Admin > Roles submodule. Read-only — roles are seeded and immutable.
 */
@Controller
@RequestMapping("/backoffice/admin/roles")
public class RoleController extends AbstractBackofficeController {

    private final RoleRepository roleRepository;

    /** Map from module ID string to display name, built once from the enum. */
    private static final Map<String, String> MODULE_DISPLAY_NAMES;
    static {
        MODULE_DISPLAY_NAMES = new LinkedHashMap<>();
        Arrays.stream(BackofficeModule.values())
              .forEach(m -> MODULE_DISPLAY_NAMES.put(m.getId(), m.getDisplayName()));
    }

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("moduleDisplayNames", MODULE_DISPLAY_NAMES);
        return "backoffice/admin/roles/list";
    }
}
