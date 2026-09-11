# idx2d — Effects Lab

A modern **Java 17 / Swing** revival of *idx2d*, a real-time software-rendering effects library
originally written in 2001 as a set of Java Applets.

All ten original effects have been ported into a single executable desktop application with a dark,
tech-themed split view: pick a demo on the left, watch it render on the right.

![idx2d Effects Lab](docs/screenshot.png)

## Overview

- **10 classic effects** — texture distortion, ripples, feedback, rotozoom, tunnels and more.
- **Pure software rendering** — every frame is computed into an `int[]` ARGB buffer, no GPU or external
  dependency.
- **Single executable uber jar** — effects and textures are bundled, so it runs with just a JDK.
- **Modular** — the rendering library, the application shell and the demos are separate Maven modules;
  demos are discovered at runtime via a `ServiceLoader` SPI.
- **Modern dark UI** — custom-painted sidebar, hover/selected states, slim scrollbar, typographic
  hierarchy and status bar.
- **Clean lifecycle** — each demo owns a daemon animation thread that starts when selected and stops when
  you switch away (no `Thread.stop()`, no leaked threads).

## Ported with OpenCode | DeepSeek V4 Flash

This project was ported from the original sources (Java source files, Applets, HTML pages with `<applet>`)
with **OpenCode** and the **DeepSeek V4 Flash** model.

Total cost: **226,340** tokens, **$0.18** spent.

_Migration prompt:_
```text
This folder contains idx3d, one of the first Java 3d engines.
It consists of a library and several examples which were implemented as Java Applets back in the day.

I want you to do the following:
- initialize as GIT project
- set up a Maven build and a README
- reorganize the code and migrate it to Java 17
- migrate the Applets to other Swing classes

Eventually I want this project to build an executable Java application (uber jar), which includes all the previous applets, with a split view:
- left side: allow to chose a demo (on startup, demo1 is selected)
- right side: run the selected demo

For the GUI, I want to have a very modern, dark tech themed look, with a nice design (work with font sizes and styles and color and proper paddings to make it look very nice).

Work out a plan on how to refactor this, and then start working on it.
```

## Requirements

- JDK 17 or newer (built and tested on JDK 25)
- Apache Maven 3.8+

## Build

From the repository root:

```bash
mvn clean package
```

This builds all three modules, runs the tests and produces a self-contained executable jar at
`idx2d-demos/target/idx2d.jar` (app + library + effects + textures bundled). The individual modules
also produce their own jars under `<module>/target/`.

## Run

```bash
java -jar idx2d-demos/target/idx2d.jar
```

## Using the application

- The **left sidebar** lists the demos. **Distorter** is selected on startup.
- Select with the mouse, or navigate with `↑`/`↓` and `←`/`→`.
- The **right pane** shows the demo name, a short description and the running effect.
- The **status bar** shows the current status and the canvas resolution.

Interactive demos:

| Demo | Interaction |
|---|---|
| Liquid | Move the mouse over the canvas, click/drag to drop, press `M` to toggle random droplets |
| Tunnel | Click the canvas for hyperspeed |

## Demos

| # | Demo | Description |
|---|------|-------------|
| 01 | **Distorter** | A warping sine-field that folds a texture through a rotating displacement grid |
| 02 | **Distorter 2** | A radial twirl over a Gaussian-blurred source, with an oscillating focal point |
| 03 | **Drop** | Concentric ripples spreading across a reflective surface |
| 04 | **DropBlender** | Two textures cross-faded through animated water ripples |
| 05 | **Feedback** | A recursive zoom / rotate / blur feedback loop that never settles |
| 06 | **Lake** | A photograph sitting above its own animated water reflection |
| 07 | **Liquid** | Droplets falling onto a chrome-mapped liquid surface |
| 08 | **RotoZoomer** | Endless rotation and zoom while sampling a texture |
| 09 | **SinDistorter** | A sinusoidal shear distortion swept by a rotating field |
| 10 | **Tunnel** | A classic perspective texture tunnel |

## Project structure

Three Maven modules, with the runnable uber jar assembled by the top module:

```
pom.xml                         parent / reactor
idx2d-core/                     library — rendering primitives, no UI
  src/main/java/idx2d/          pixel buffer, colour maths, grids, distorters, filters, physics
  src/main/java/idx2d/tools/    ImageIO-based bitmap helper
  src/test/java/                JUnit 5 tests
idx2d-app/                      base application — Swing shell
  src/main/java/idx2d/app/      DemoView, Params, Demo, DemoProvider, DemoRegistry, MainFrame, Idx2dApp
  src/main/java/idx2d/app/ui/   dark theme and Swing components
idx2d-demos/                    the ten effects (plugged into the app)
  src/main/java/idx2d/app/demo/ the effects + DemoCatalog (a DemoProvider)
  src/main/resources/textures/  textures bundled into the jar
  src/main/resources/META-INF/services/idx2d.app.DemoProvider
docs/screenshot.png
refactoring-idx2d-modernization.md   migration plan and work log
```

Dependency direction: `idx2d-demos` → `idx2d-app` → `idx2d-core`. The app shell never references the
demos directly; it discovers them through the `DemoProvider` service interface.

## Architecture notes

- **`Texture`** is a mutable ARGB `int[]` pixel buffer. `getImage()` returns a `BufferedImage` that
  *shares* the same array, so a demo only mutates pixels and repaints — no per-frame copies.
- **`DemoView`** replaces the old `ThreadApplet`: the effect runs on a daemon animation thread, while all
  painting happens on the Swing EDT via `paintComponent`.
- **Textures are classpath resources** loaded with `ImageIO` (`TextureLoader`), replacing applet
  `getDocumentBase()`.
- **Plug-in demos** — `DemoRegistry` loads every `DemoProvider` via `ServiceLoader`; adding a demo means
  registering it in `DemoCatalog` (or adding another provider), with no change to the app module.
- **No runtime dependencies.** JUnit 5 is test-scope only.

## History

The original sources were Java Applets (`.html` launchers, `idx2d.jar`, AWT event overrides,
`Thread.stop()`). They were reorganised into a standard Maven layout, migrated to Java 17 and ported to
Swing. The full plan, decisions and work log live in
[`refactoring-idx2d-modernization.md`](refactoring-idx2d-modernization.md).

## Credits

- Original **idx2d** library, effects and demo applets — © 2001 Peter Walser (`proxima@active.ch`).
- Java 17 / Swing port and application shell.
