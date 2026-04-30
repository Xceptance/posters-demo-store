package com.xceptance.posters.config;

import java.util.List;

/**
 * Holds sidebar model record types for backoffice templates.
 * The actual model population is done by each backoffice controller.
 */
public class SidebarModelAdvice {

    /**
     * Sidebar model for a top-level module.
     */
    public record SidebarModule(
            BackofficeModule module,
            boolean active,
            boolean expanded,
            List<SidebarSubmodule> submodules
    ) {
        public boolean hasSubmodules() { return !submodules.isEmpty(); }
        public String getId() { return module.getId(); }
        public String getDisplayName() { return module.getDisplayName(); }
        public String getIcon() { return module.getIcon(); }
        public String getUrlPrefix() { return module.getUrlPrefix(); }
    }

    /**
     * Sidebar model for a submodule.
     */
    public record SidebarSubmodule(
            BackofficeModule module,
            boolean active
    ) {
        public String getId() { return module.getId(); }
        public String getDisplayName() { return module.getDisplayName(); }
        public String getIcon() { return module.getIcon(); }
        public String getUrlPrefix() { return module.getUrlPrefix(); }
    }
}
