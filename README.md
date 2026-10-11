# OrynLauncher

**OrynLauncher** is a community-built Android launcher for Minecraft: Java Edition, focused on a clean mobile experience, useful customization, and making Java Edition easier to manage on Android.

> **Project status:** OrynLauncher is community-developed software. Features and compatibility may change between versions. Back up your worlds and important launcher data before installing a new build.

## Features

- **Minecraft: Java Edition on Android** — launch and manage supported game versions.
- **Account management** — manage supported account types.
- **Instances and game settings** — organize installations and configure launch options.
- **Mods and downloads** — use the launcher’s available content-management tools.
- **Skin and cape tools** — where supported by the account, server, and current build.
- **Custom launcher experience** — an evolving OrynLauncher interface and feature set.

Availability may vary by release, device, Android version, Minecraft version, and account type.

## Get OrynLauncher

Download releases and build artifacts only from the official OrynLauncher project pages:

- **Repository:** [OrynLauncher2](https://github.com/Orynlauncher-Official/OrynLauncher2)
- **Releases:** [GitHub Releases](https://github.com/Orynlauncher-Official/OrynLauncher2/releases)
- **Builds:** [GitHub Actions](https://github.com/Orynlauncher-Official/OrynLauncher2/actions)

If you install a test or debug build, expect unfinished features and possible bugs. Do not share account credentials or private authentication files when reporting problems.

## Build from Source

### Requirements

- Android Studio with a compatible Android SDK
- JDK version required by the project’s Gradle configuration
- Git

### Steps

```bash
git clone https://github.com/Orynlauncher-Official/OrynLauncher2.git
cd OrynLauncher2
```

Open the project in Android Studio, allow Gradle to sync, then build the appropriate app variant. You can also use the Gradle wrapper from a terminal:

```bash
./gradlew tasks
```

Use the tasks listed by Gradle for the available build variants. Build requirements may change as the project is updated.

## Contributing

Contributions are welcome! Before opening a pull request, please read [CONTRIBUTING.md](./CONTRIBUTING.md).

- Report bugs and request features through the repository’s GitHub Issues.
- Keep pull requests focused and describe what changed and how you tested it.
- Never commit passwords, tokens, private keys, or account data.
- Preserve existing package names and project structure unless a change is specifically required and discussed.

## License and Attribution

This project is distributed under the terms of the **GNU General Public License v3.0 (GPL-3.0)**; see [LICENSE](./LICENSE) for the applicable terms.

OrynLauncher includes and builds on open-source software and libraries. Their respective licenses and notices remain applicable. Please retain required copyright notices and license information when modifying or redistributing the project.

## Support

For OrynLauncher-specific bugs or feature requests, use the [Issues page](https://github.com/Orynlauncher-Official/OrynLauncher2/issues). Include the launcher version, Android version, device details, steps to reproduce, and relevant logs—after removing personal information and secrets.

---

**OrynLauncher** — Minecraft Java Edition on Android, shaped by the community.
