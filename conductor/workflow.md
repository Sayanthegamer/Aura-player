# Project Workflow: Aura Player

## Guiding Principles
1. **The Plan is the Source of Truth:** All work must be tracked in `plan.md`
2. **The Tech Stack is Deliberate:** Changes to the tech stack must be documented in `tech-stack.md` *before* implementation
3. **Test-Driven Development:** Write unit tests before implementing functionality
4. **High Code Coverage:** Aim for >80% code coverage for all modules
5. **User Experience First:** Material 3 Expressive UI, clean Progressive Disclosure, no visual clutter
6. **CLI & Device Deployment Workflow:** No heavy IDE requirement. Lightweight VS Code + Gradle CLI (`./gradlew installDebug`) + physical device debugging via ADB

## Task Workflow
1. **Select Task:** Choose next available task from `plan.md`
2. **Mark In Progress:** Change task status to `[~]`
3. **Red Phase (TDD):** Write failing unit/instrumented tests
4. **Green Phase (TDD):** Write minimal implementation code to make tests pass
5. **Refactor:** Clean up code without breaking tests
6. **Quality Gate:** Run `./gradlew test` and lint checks
7. **Commit & Record:** Commit changes and update `plan.md` with commit SHA

## Development Commands
```bash
# Compile and build debug APK
./gradlew assembleDebug

# Build & install directly to connected physical device via ADB
./gradlew installDebug

# Run unit tests
./gradlew test

# Run Android lint
./gradlew lint
```
