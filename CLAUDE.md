# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Overview

Consulo port of **Perl5 support (Camelcade/Perl5-IDEA)**. Upstream sources are kept in the upstream layout
(`plugin/`, `tt2/`, `mojo/`, `embedded/`, `mason/`) and compiled by Maven aggregate modules in `consulo/`.
Upstream changes are merged from https://github.com/Camelcade/Perl5-IDEA.

## Build

```bash
# full build (the only build command to use)
mvn package -T 1C -Dmaven.test.skip=true

# run Consulo with the plugins in consulo/plugin/sandbox (web: http://localhost:8080)
mvn package -T 1C -Dmaven.test.skip=true -Prun-web
mvn package -T 1C -Dmaven.test.skip=true -Prun-desktop
```

Upstream tests (`*/src/test`, `plugin/testFixtures`) are not built.

## Layout

- `consulo/core` — plugin `com.perl5`: aggregate of `plugin/common`, `plugin/backend`, `plugin/frontend` and the
  sub-projects (`carton`, `cpan`, `cpanminus`, `perlbrew`, `plenv`, `asdf`, `berrybrew`, `perlInstall`, `makeMaker`,
  `moduleBuild`, `structureView`, `debugger`, `profiler`, `coverage`, `copyright`, `intelliLang`). They share packages,
  so they form one JPMS module.
- `consulo/tt2`, `consulo/mojo`, `consulo/embedded`, `consulo/mason` — templating plugins (depend on `com.perl5`).
- `consulo/plugin` — packaging of `com.perl5` and the run profiles; loads the templating plugins into the sandbox.
- `consulo/<module>/src/main/java` — Consulo-only sources (registrations, unified UI, helpers).
- Not ported (IntelliJ-only APIs): `plugin/docker`, `plugin/wsl`, `plugin/terminal`, `plugin/idea`.
- Lexers/parsers are generated at build time from `*/grammar/*.flex` and `*.bnf` (maven-consulo-plugin).

## Conventions

- Use the real Consulo API (`consulo.*`); `consulo.ide.impl` only when no API is exported. Never import
  `consulo.*.internal.*` packages exported only to platform modules — they fail at runtime.
- Extensions/services are registered with annotations (`@ExtensionImpl`, `@ServiceAPI`/`@ServiceImpl`, `@ActionImpl`,
  `@TopicAPI`); annotated types must be top-level classes. Plugin-owned extension points only may use
  `order = "first"/"last"`.
- UI is Consulo unified UI (`consulo.ui.*`, `LocalizeValue`, `@RequiredUIAccess`); it must work in Consulo web.
  No Swing dialogs (`Messages`, `DialogWrapper`): use `MessageBoxes`, `InputBoxBuilder`, `DialogService`
  (`consulo.perl5.core.ui.PerlDialogUtil`). No `Task.Modal` where a dialog is needed (web deadlock).
  Module extension panels are unified too (`MutableModuleExtension#createConfigurationComponent`); lists and tables use
  `ToolbarDecoratorBuilderFactory` (`consulo.ui.ex.toolbar`), see `consulo.perl5.core.ui.PerlUnifiedConfigurationUtil`.
- Perl interpreter = SDK of the module's Perl module extension (`perl5`); templating plugins are child extensions
  (`perl5-tt2`, `perl5-mojo`, `perl5-embedded`, `perl5-htmlmason`, `perl5-mason2`) and keep their settings per module
  in the extension state.
- "New Project/Module" templates: the Perl5 group is shared — plugins add items with `PerlNewModuleBuilder.addGroup(context)`
  and `PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT`; `PerlNewModuleBuilderProcessor` sets up the Perl part (interpreter,
  `lib`, `t`), `PerlFrameworkNewModuleBuilderProcessor` adds a framework extension and its root directory. Mojolicious
  templates run `mojo generate` after the project is opened (`consulo/mojo/.../newProject`).
- No Kotlin. Mark Consulo-specific changes in upstream files with `// consulo:` / `// TODO consulo:` comments.
