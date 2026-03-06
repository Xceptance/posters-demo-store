## Context

The backoffice assets are remnants from a previous AdminLTE-based admin panel that was never completed or has been superseded. Three directories remain:

- `static/js/backoffice/` — 1 legacy JS file (`dashmin_main.js`)
- `static/js/backofficeNew/` — AdminLTE framework + 16 plugin directories
- `static/css/backofficeNew/` — AdminLTE CSS + FontAwesome 4 webfonts

No Java controller, Thymeleaf template, or storefront JavaScript references any of these files.

## Goals / Non-Goals

**Goals:**
- Remove all legacy backoffice static assets
- Reduce repository and build artifact size by ~27MB

**Non-Goals:**
- Building a new backoffice (future work, tracked in feature-ideas.md)
- Removing any assets used by the active storefront

## Decisions

### Decision 1: Full directory deletion

Delete the three directories entirely rather than selectively pruning. Since zero references exist, there's no reason to keep any subset.

### Decision 2: No git history rewriting

We delete the files in a normal commit. We don't rewrite git history to purge them from old commits — that would be disruptive for a minor size gain on the working tree.
