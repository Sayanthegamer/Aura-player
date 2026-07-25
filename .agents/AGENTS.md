# Project Agent Rules

## Documentation Lookup Rule for Critical Code (Context7)
- **Mandatory Context7 Lookup:** Whenever implementing, designing, or refactoring critical components of code (e.g., core system architecture, key framework APIs, third-party SDK integrations, or major feature engines), you MUST first use the `context7` MCP server tools (`resolve-library-id` followed by `query-docs`) to retrieve the latest authoritative documentation and verified code snippets. Do not rely solely on internal memory or legacy patterns for active framework APIs.

## Mandatory Material 3 Expressive Design Lookup Rule
- **Mandatory M3 Expressive Consultation:** Whenever designing, implementing, or refactoring ANY user interface (UI) components, layouts, color schemes, animations, shapes, typography, or audio player controls, you MUST first view and consult the Material 3 Expressive skill suite in [`.agents/skills/m3-expressive/SKILL.md`](file:///c:/Users/Anon/Desktop/Player/.agents/skills/m3-expressive/SKILL.md) and its modular reference guides in `references/` (`components.md`, `color.md`, `motion.md`, `shape.md`, `typography.md`, `spacing.md`, `icons.md`, `elevation.md`, `foundations.md`, `expressive.md`). Ensure all tokens, padding, elevations, touch targets (48dp), and Compose implementation patterns strictly comply with official Google Material 3 specs before writing UI code.

