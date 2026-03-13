package com.xceptance.posters.controller;

import com.xceptance.posters.entity.Role;
import com.xceptance.posters.service.RoleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for the Admin > Roles submodule.
 * Supports full CRUD for custom roles; built-in roles are read-only.
 */
@Controller
@RequestMapping("/backoffice/security/roles")
public class RoleController extends AbstractBackofficeController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String list(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        model.addAttribute("userCounts", roleService.countUsersForAllRoles());
        model.addAttribute("moduleDisplayNames", roleService.getModuleDisplayNames());
        return "backoffice/admin/roles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
        return "backoffice/admin/roles/form";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(name = "moduleIds", required = false) Set<String> moduleIds,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        // Build the role object to preserve form data on error
        Role role = new Role();
        role.setName(name != null ? name.trim() : "");
        role.setDescription(description != null ? description.trim() : "");
        if (moduleIds != null) {
            role.setModuleIds(new HashSet<>(moduleIds));
        }

        if (role.getName().isBlank()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        if (moduleIds == null || moduleIds.isEmpty()) {
            model.addAttribute("error", "At least one module must be selected");
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        try {
            roleService.save(role);
            redirectAttributes.addFlashAttribute("success", "Role created successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        return "redirect:/backoffice/security/roles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Role role = roleService.findById(id).orElse(null);
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Role not found");
            return "redirect:/backoffice/security/roles";
        }
        if (role.isBuiltIn()) {
            redirectAttributes.addFlashAttribute("error", "Built-in roles cannot be edited");
            return "redirect:/backoffice/security/roles";
        }
        model.addAttribute("role", role);
        model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
        return "backoffice/admin/roles/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam(name = "moduleIds", required = false) Set<String> moduleIds,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Role role = roleService.findById(id).orElse(null);
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Role not found");
            return "redirect:/backoffice/security/roles";
        }

        // Update values to preserve form data on error
        role.setName(name != null ? name.trim() : "");
        role.setDescription(description != null ? description.trim() : "");
        if (moduleIds != null) {
            role.setModuleIds(new HashSet<>(moduleIds));
        }

        if (role.getName().isBlank()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        if (moduleIds == null || moduleIds.isEmpty()) {
            model.addAttribute("error", "At least one module must be selected");
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        try {
            roleService.save(role);
            redirectAttributes.addFlashAttribute("success", "Role updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("role", role);
            model.addAttribute("allModules", roleService.getTopLevelModuleDisplayNames());
            return "backoffice/admin/roles/form";
        }
        return "redirect:/backoffice/security/roles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Role deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backoffice/security/roles";
    }
}
