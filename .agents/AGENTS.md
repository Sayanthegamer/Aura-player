# Project Agent Rules

## Documentation Lookup Rule for Critical Code (Context7)
- **Mandatory Context7 Lookup:** Whenever implementing, designing, or refactoring critical components of code (e.g., core system architecture, key framework APIs, third-party SDK integrations, or major feature engines), you MUST first use the `context7` MCP server tools (`resolve-library-id` followed by `query-docs`) to retrieve the latest authoritative documentation and verified code snippets. Do not rely solely on internal memory or legacy patterns for active framework APIs.
