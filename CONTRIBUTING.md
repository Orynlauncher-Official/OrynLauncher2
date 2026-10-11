# Contributing to OrynLauncher

Thank you for considering a contribution to OrynLauncher! Contributions of bug fixes, features, documentation, testing, and thoughtful feedback are welcome.

## Before You Start

1. Search existing issues and pull requests to avoid duplicating work.
2. For a major feature or a change that affects architecture, open an issue first to discuss the approach.
3. Keep contributions respectful, focused, and consistent with the project’s goals.
4. Read the repository’s [LICENSE](./LICENSE) and preserve required copyright and license notices.

## Development Setup

### Requirements

- Git
- Android Studio and an Android SDK compatible with the project
- The JDK version required by the Gradle configuration

### Get the Code

Fork the repository on GitHub, then clone your fork:

```bash
git clone https://github.com/YOUR_USERNAME/OrynLauncher2.git
cd OrynLauncher2
git remote add upstream https://github.com/Orynlauncher-Official/OrynLauncher2.git
```

Sync the project in Android Studio and allow Gradle to finish syncing before making changes.

## Branching

Create a descriptive branch for each change:

```bash
git checkout -b fix/short-description
# or
git checkout -b feature/short-description
# or
git checkout -b docs/short-description
```

Avoid doing unrelated work in the same branch.

## Development Guidelines

- Follow the existing Kotlin, Android, and Jetpack Compose patterns used in the relevant module.
- Prefer clear names, small focused functions, and immutable values where practical.
- Keep UI states, loading states, error handling, and lifecycle behavior in mind.
- Perform file and network I/O off the main thread; interact with Android UI and WebView APIs on the appropriate thread.
- Avoid unrelated refactors and dependency changes in feature or bug-fix pull requests.
- **Do not rename existing Java/Kotlin package names or broadly rename project files unless the change is explicitly discussed and necessary.**
- Never commit credentials, tokens, private keys, personal account data, or generated secrets.
- Preserve applicable license headers and third-party notices.

## Commit Messages

Use concise messages that explain the change. These prefixes are recommended:

- `FIX:` bug fix
- `FEAT:` new feature
- `UI:` user-interface change
- `DOCS:` documentation change
- `REFACTOR:` code cleanup
- `TEST:` tests
- `CI:` build or automation change

Examples:

```text
FIX: prevent skin preview WebView thread errors
FEAT: add instance backup options
DOCS: update setup instructions
```

## Test Your Changes

Before opening a pull request:

1. Run the relevant Gradle build or test tasks.
2. Check the GitHub Actions results for your branch.
3. Test the affected flow on a device or emulator when possible.
4. Check logs for crashes and regressions.
5. For UI changes, verify different screen sizes and both expected loading and error states.
6. State honestly which checks you ran and which you could not run.

Useful commands:

```bash
./gradlew tasks
./gradlew test
```

The exact available tasks depend on the project configuration. Use `./gradlew tasks` to see what is supported.

## Pull Request Checklist

- [ ] The change addresses a clear issue or has been discussed.
- [ ] The pull request is focused and avoids unrelated changes.
- [ ] Code follows the surrounding project conventions.
- [ ] Relevant builds/tests were run, and results are described.
- [ ] No credentials, private data, or unnecessary generated files are included.
- [ ] Required license and copyright notices are preserved.
- [ ] UI changes include screenshots or a short recording when useful.
- [ ] Known limitations and compatibility considerations are documented.

### Pull Request Description

Please include:

```markdown
## Summary
What changed and why?

## Testing
What build, tests, or device checks were performed?

## Screenshots / Recording
Add these for UI changes when useful.

## Notes
List limitations, risks, or follow-up work.
```

## Reporting Bugs

When filing an issue, include as much of the following as you can safely share:

- OrynLauncher version or build
- Device model and Android version
- Minecraft version and relevant account/instance type (never include credentials)
- Steps to reproduce
- Expected behavior and actual behavior
- Relevant logs or screenshots with personal information removed

For feature requests, explain the problem being solved and the expected user experience. Do not include passwords, access tokens, session files, or other secrets in public issues.

## Review and Community Conduct

Maintainers may ask for changes, additional testing, or clarification before merging. Be patient and constructive, and treat contributors respectfully. Not every proposal will be accepted; decisions may consider stability, maintainability, compatibility, and project scope.

## License

Contributions are accepted under the project’s [GPL-3.0 license](./LICENSE), subject to the applicable license terms. By submitting a contribution, ensure you have the right to submit it and that third-party code or assets are appropriately licensed and attributed.

---

Thank you for helping improve OrynLauncher!
