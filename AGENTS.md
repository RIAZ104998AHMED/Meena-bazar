# Repository Guidelines

## Project Structure & Module Organization
This repository is an Android app project with a single active module: `:app`.

- `app/src/main/java/com/example/meenabazar/`: Kotlin source (`MainActivity`, `SplashActivity`, `LoginScreen`, theme files).
- `app/src/main/res/`: Android resources (`drawable/`, `mipmap-*/`, `values/`, `xml/`).
- `app/src/test/`: local JVM unit tests.
- `app/src/androidTest/`: instrumentation/device tests.
- `gradle/libs.versions.toml`: dependency and plugin versions.

Note: `MyFirstAndroidProject/` and `RiazApplication/` exist in the workspace but are not included by `settings.gradle.kts`.

## Build, Test, and Development Commands
Run from repository root:

- `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows): build debug APK.
- `./gradlew testDebugUnitTest`: run local unit tests in `app/src/test`.
- `./gradlew connectedDebugAndroidTest`: run device/emulator instrumentation tests.
- `./gradlew lintDebug`: run Android lint checks.
- `./gradlew clean`: remove build outputs.

## Coding Style & Naming Conventions
- Language: Kotlin + Jetpack Compose (JVM target 11).
- Indentation: 4 spaces; keep line breaks readable for Compose modifier chains.
- Naming:
  - Classes/composables: `PascalCase` (e.g., `LoginScreen`, `SplashActivity`).
  - Functions/variables: `lowerCamelCase`.
  - Resource files/IDs: `snake_case` (e.g., `login_bg.png`).
- Keep UI theme tokens in `ui/theme/` and avoid hardcoding repeated colors/typography across screens.

## Testing Guidelines
- Frameworks: JUnit4 (`test`), AndroidX JUnit Runner + Espresso/Compose test libs (`androidTest`).
- Test file names should end with `Test` and mirror package paths.
- For UI changes, add/update at least one relevant unit or instrumentation test when behavior changes.

## Commit & Pull Request Guidelines
Git metadata is not available in this workspace snapshot, so no project-specific commit pattern can be inferred. Use this convention going forward:

- Commit format: `type(scope): short summary` (example: `feat(login): add social sign-in buttons`).
- Keep commits focused and buildable.
- PRs should include:
  - What changed and why.
  - Test evidence (commands run and results).
  - Screenshots/video for UI changes (Compose screens).
  - Linked issue/task when applicable.

## Security & Configuration Tips
- Never commit secrets, keystores, or machine-specific config.
- Keep `local.properties` local only (already ignored).
- Validate min/target SDK or dependency upgrades in a dedicated PR with regression testing.
