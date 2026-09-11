# Refactoring: idx2d modernization (Java 17 + Swing application)

## Definition

*Written and owned by the developer. Claude pre-fills from context; the developer refines before G1.*

- **Purpose (why):** idx2d is a 2001-era Java Applet demo effects library. Applets are dead (removed
  from the JDK, no browser support), the sources predate generics/Java 17, there is no build, no tests and
  no README. The goal is to preserve the effects while turning the collection into a single modern,
  runnable Java 17 desktop application.
- **Scope (what should be done):**
  - Initialize the folder as its own Git repository (nested under the existing workspace repo).
  - Add a Maven build targeting Java 17 that produces a single executable uber jar.
  - Reorganize sources into a standard Maven layout (`src/main/java`, `src/main/resources`, `src/test/java`).
  - Migrate every `java.applet.Applet`/AWT `ThreadApplet` animation to a Swing (`JPanel`) component.
  - Replace applet parameter/`getDocumentBase()`/`showStatus`/AWT event idioms with Swing equivalents and
    classpath resource loading.
  - Build a modern dark "tech" themed GUI: a split view with a demo chooser on the left (first demo
    selected on startup) and the running demo on the right.
  - Include all 10 original effects as selectable demos: Distorter, Distorter 2, Drop, DropBlender,
    Feedback, Lake, Liquid, RotoZoomer, SinDistorter, Tunnel.
  - Write a README documenting build, run and structure.
- **Constraints:**
  - Java 17 language/bytecode level; Maven build.
  - Preserve the visual behaviour of each effect (this is a port, not a redesign of the effects).
  - No runtime dependency beyond the JDK (Swing/AWT + `javax.imageio`); JUnit only for tests.
  - Legacy Applet sources, HTML pages, old jars/zips and unused images are preserved (not deleted) under
    `legacy/` and excluded from the build.
- **Non-goals (out of scope):**
  - Rewriting the pixel math / effect algorithms or improving render quality.
  - Adding new effects, animation controls or per-demo parameter editing UI.
  - Cross-platform packaging beyond the uber jar (no jpackage/installers).
  - Automated UI/visual tests.

## Analysis

*Written by Claude in phase 1. See `analysis-and-planning.md`.*

- **Current state:** 37 Java files (~3,100 LOC) in two groups:
  - **Core library** under `idx2d/`: `Texture`, `Color24`, `TextureBlender`, `FastImageProducer`,
    `BitmapCodec`, `ThreadApplet`; sub-packages `grid/` (grid + distorters), `filter/` (`FastBlur`,
    `Convolution`), `physics/Oscillator`, `struct/` (`BumpMap`, `SphereMap`).
  - **Applets** at the repo root: `DistorterApplet`, `Distorter2Applet`, `DropApplet`,
    `DropBlenderApplet`, `FeedbackApplet`, `LakeApplet`, `LiquidApplet`, `RotoZoomerApplet`,
    `SinDistorterApplet`, `TunnelApplet`, plus test/utility classes `RotationTest`, `Screenshot`,
    `BitmapCodec.main`.
  - No build file; the only artifacts are `idx2d.jar`, `idx2d.zip`, `.html` launch pages and image assets.
  - `ThreadApplet extends Applet implements Runnable` provides the animation thread, `blitTexture`,
    `getTexture` and typed parameter getters; every effect subclasses it.
- **Problems:**
  - `java.applet.*` and `ThreadApplet` cannot run on modern JVMs/browsers; `Thread.stop()` (used in
    `ThreadApplet.stop()`) throws `UnsupportedOperationException` on Java 20+.
  - Rendering relies on `Applet.getGraphics()` + `Toolkit.createImage(ImageProducer)` off the EDT — not
    safe or idiomatic for Swing.
  - Assets are loaded via `getImage(getDocumentBase(), ...)`, unavailable outside applets.
  - AWT event overrides (`mouseEnter/Exit/Down/Drag`, `keyDown`) and `showStatus` have no Swing equivalent.
  - `Color24` uses static mutable scratch fields (`pixel`, `overflow`, `r/g/b`, `hsb`) — not thread-safe,
    though only one animation runs at a time in the new app.
  - `Texture.resizeHighQuality` omits the alpha channel (`(r<<16)|(g<<8)|b`) — pre-existing minor bug;
    preserve unless trivially safe to fix.
  - No tests and no build, so there is no safety net for the port.
- **Affected code:** all 37 `.java` files (moved/reorganised), plus all `.html`, `idx2d.jar/zip`,
  `idx2d.prj`, `files.txt` (relocated to `legacy/`) and the referenced images (relocated to
  `src/main/resources/`).
- **Dependencies & blast radius:** self-contained; no external consumers. The only coupling is the applet
  classes → `ThreadApplet`/core, and core's `Texture.getImage()` → `FastImageProducer` +
  `Toolkit.createImage`. Changing `Texture` touches every effect, so it must keep the same pixel contract.
- **Test coverage:** none. Characterization tests will be added for the pure, headless core
  (`Color24`, `Texture` resample/rotate, `Grid8x8`, `Oscillator`, `Convolution`, `TextureBlender`).
- **Risks:**
  - Rendering path change (Applet `getGraphics` → `paintComponent` + shared `BufferedImage`) could shift
    colours or blank frames. Mitigation: keep `Texture` pixel arrays as the source of truth and back the
    `BufferedImage` with the same `int[]`.
  - Per-demo timing differs (`time += 50` vs wall-clock); keep each effect's original loop semantics.
  - Some effects are heavy; running a demo on a non-EDT thread must only mutate pixels and `repaint()`,
    never touch Swing state off the EDT.
  - No display in the build environment, so the GUI can only be verified by compile/package and headless
    construction smoke tests.

## Plan

*Written by Claude in phase 2. Ordered phases, each ending in a verification step. The checkboxes are the
progress state. See `analysis-and-planning.md`.*

### Phase A — Project scaffold, git and source layout
- [x] `git init` the folder, add `.gitignore` (target/, IDE, OS junk).
- [x] Create `pom.xml`: Java 17, JUnit 5, `maven-shade-plugin` uber jar with main class
      `idx2d.app.Idx2dApp`.
- [x] Move Applet-era sources, HTML, jars/zips, `.prj`, `files.txt` and unused images to `legacy/`.
- [x] Move core sources to `src/main/java/idx2d/**`; `ThreadApplet` removed from the build
      (preserved in `legacy/`); `FastImageProducer` kept until Phase B modernizes `Texture`.
- [x] Copy the images used by the demos into `src/main/resources/textures/`.
- [x] **Verify:** `mvn -q compile` succeeds for the core library.

### Phase B — Core library modernized to Java 17
- [x] `Texture`: `BufferedImage`-backed `getImage()` sharing the `int[]`; add `BufferedImage` constructor;
      remove `Toolkit.createImage(ImageProducer)`/`FastImageProducer` dependency; drop commented dead code.
- [x] Add `TextureLoader` helper to load `Texture` from classpath resources via `ImageIO`.
- [x] Clean up `Color24`, `TextureBlender`, `filter/*`, `grid/*`, `physics/*`, `struct/*` (imports,
      remove dead locals), preserving arithmetic exactly.
- [x] Replace `BitmapCodec` with a `javax.imageio`-based facade in `idx2d.tools`.
- [x] Add JUnit 5 characterization tests for `Color24`, `Texture`, `Grid8x8`, `Oscillator`, `Convolution`,
      `TextureBlender`.
- [x] **Verify:** `mvn -q test` green (21 tests).

### Phase C — Swing demo framework and port of the 10 effects
- [x] Replace `ThreadApplet` with `DemoView extends JPanel implements Runnable`: managed animation thread,
      `render(Texture)` + `paintComponent`, texture loading, and mouse/key hooks.
- [x] Add `Params` typed configuration holder and `Demo` descriptor.
- [x] Add `DemoCatalog` listing the 10 demos with name, description, canvas size, texture(s) and
      parameters (from the original HTML pages).
- [x] Port Distorter, Distorter 2, Drop, DropBlender, Feedback, Lake, Liquid, RotoZoomer, SinDistorter,
      Tunnel to `DemoView` subclasses, preserving each loop's timing and math.
- [x] **Verify:** `mvn -B test` green — 23 tests, including a smoke test constructing every demo
      headlessly. (`Theme` is deferred to Phase D where it is first needed.)

### Phase D — Modern dark GUI (split view application)
- [x] Add `Theme` (dark tech palette, font resolution, spacing) and `ModernScrollBarUI`.
- [x] `Idx2dApp` entry point + `MainFrame`: dark shell, sidebar + content split.
- [x] Sidebar: brand panel, demo list with hover/selected states (custom renderer), first demo selected on
      startup.
- [x] Content: header (demo name + description) and a centered canvas host that starts/stops demos on
      selection change; status bar.
- [x] Wire keyboard navigation (LEFT/RIGHT on the list) and a clean start/stop lifecycle (no thread leaks
      on switch/close).
- [x] **Verify:** `mvn -B package` produces `target/idx2d.jar`; launched it on the X display, captured
      screenshots and confirmed startup (Distorter selected), rendering, and switching to Feedback.

### Phase E — Documentation and finalization
- [x] Write `README.md` (overview, build, run, project structure, demo list, credits).
- [x] Add `docs/screenshot.png` captured from the running application.
- [x] Final full build + tests; tick all tasks; update Work Log.
- [x] Initial Git commit.
- [x] **Verify:** `mvn -B clean package` green (23 tests); `target/idx2d.jar` is executable.

## Work Log

*Appended by Claude as work happens — what changed and, for any real choice, why. Append-only. Records the
baseline, gate approvals, and each phase's result.*

### 2026-09-11 — Intake (G1)
- Definition pre-filled from the repo and the developer's request.
- Decisions confirmed by the developer: one demo per effect (10 demos); legacy assets moved to `legacy/`;
  `git init` + initial commit.
- G1/G2/G3 treated as pre-approved by the instruction "work out a plan … and then start working on it".

### 2026-09-11 — Baseline (G3)
- No build system, no tests. The tree is Applet-era sources that do not compile on Java 17 (`java.applet`,
  `Thread.stop()`). Baseline recorded as **red/no safety net**; characterization tests are added in Phase B
  before the risky `Texture` change, and the ported effects are pinned by construction smoke tests.

### 2026-09-11 — Phase A: Project scaffold, git and source layout
- Did: `git init` (branch renamed `main`), added `.gitignore` and `pom.xml` (Java 17, JUnit 5,
  shade uber jar). Moved all Applet sources, HTML, `idx2d.jar/zip`, `.prj`, `files.txt` and unused images
  to `legacy/` (including a full copy of the original core under `legacy/idx2d/`). Moved core sources to
  `src/main/java/idx2d/**`, copied the 12 demo images to `src/main/resources/textures/`.
- Decisions: kept `FastImageProducer` in the build for now (deleting it immediately would break
  `Texture`, so it is removed in Phase B where `Texture.getImage()` is rewritten). Legacy core source is
  duplicated under `legacy/idx2d/` so the original is preserved even after the new code is committed.
- Verify: `mvn -q compile` → green.
- Commit: pending (initial commit is deferred to Phase E per the Definition).

### 2026-09-11 — Phase B: Core library modernized to Java 17
- Did: rewrote `Texture` to back `getImage()` with a `BufferedImage` that shares the `int[] pixel`
  (rebuilt when the array reference changes) and added a `BufferedImage` constructor; added
  `TextureLoader` (classpath + `ImageIO`); deleted `FastImageProducer` and the hand-written
  `BitmapCodec`, replacing the latter with `idx2d.tools.BitmapCodec` (ImageIO facade); removed dead
  commented code; added 21 JUnit 5 characterization tests.
- Decisions: kept `Color24.getColorModel()` (24-bit `DirectColorModel`) and the RGB-only
  `SinglePixelPackedSampleModel` so the existing alpha-in-int convention is ignored by the colour model
  but the pixel array stays shared. `resizeHighQuality` now writes an opaque alpha byte (0xFF) — a safe
  correctness fix, no visual change. Characterization tests pin existing truncation behaviour (e.g.
  `getBilinearPixel` → `0x7F`, Gaussian convolution of a uniform field → `0x39`) rather than an idealised
  result, so the port is guarded against accidental drift.
- Verify: `mvn -B test` → 21 tests, 0 failures.
- Commit: pending (initial commit deferred to Phase E).

### 2026-09-11 — Phase C: Swing demo framework and port of the 10 effects
- Did: replaced `ThreadApplet` with `DemoView` (daemon animation thread, `render(Texture)` + EDT
  `paintComponent`, classpath texture loading, mouse/key hooks, `start`/`stop` lifecycle); added `Params`,
  `Demo` and `DemoCatalog`; ported all ten applets into `idx2d.app.demo.*` preserving their math and
  timing (including Liquid's mouse/key interaction and Tunnel's click-to-hyperspeed); added a headless
  construction smoke test for every demo.
- Decisions: each demo's canvas size is fixed (chosen from the original HTML pages, capped to fit the
  window); `Lake` computes its size as `imageWidth x imageHeight*2` from the source image. The applet
  `while (true)` loops became `while (isRunning())` so the thread stops cleanly without `Thread.stop()`.
  `Theme` was moved to Phase D (it is only needed by the GUI).
- Verify: `mvn -B test` → 23 tests, 0 failures.
- Commit: pending (initial commit deferred to Phase E).

### 2026-09-11 — Phase D: Modern dark GUI (split view application)
- Did: added `Theme` (palette + font resolution + spacing), `ModernScrollBarUI`, `BrandPanel`,
  `Sidebar` (custom hover/selected demo cell renderer, footer), `ContentPanel` (header, centered canvas
  host, status bar), `MainFrame` (selection wiring, shutdown, LEFT/RIGHT shortcuts) and `Idx2dApp`.
- Decisions: no third-party look-and-feel — everything is painted with the custom theme to keep the build
  dependency-free. The demo canvas is fixed-size and centered rather than stretched. LEFT/RIGHT are bound
  on the list's `WHEN_FOCUSED` input map because the focused `JList` otherwise swallows those keys before
  the window-level binding. Cross-platform L&F is used for predictable widget behaviour.
- Verify: `mvn -B package` green; launched `java -jar target/idx2d.jar` on display `:1`, captured
  screenshots: startup shows **Distorter** selected and animating; clicking **Feedback** switches the
  header, selection and rendered effect. No exceptions in the run log.
- Commit: pending (initial commit deferred to Phase E).

### 2026-09-11 — Phase E: Documentation and finalization
- Did: wrote `README.md` (overview, requirements, build/run, usage, demo table, structure, architecture
  notes, history and credits); captured `docs/screenshot.png` from the running application; final
  `mvn -B clean package`.
- Decisions: no `LICENSE` file added — the original 2001 sources carried no licence, so authorship is
  credited factually in the README rather than asserting terms.
- Verify: `mvn -B clean package` → BUILD SUCCESS, 23 tests, 0 failures; `target/idx2d.jar` (354 KB)
  contains `idx2d/app/Idx2dApp.class`, the textures and the `Main-Class` manifest entry.
- Commit: initial commit created (see `git log`).

### 2026-09-11 — Completion
- All five phases complete. The project is a Git repository with a Maven Java 17 build that produces an
  executable uber jar containing all ten ported effects in a dark split-view Swing application.
- Behaviour preserved relative to the original effects: the pixel math and per-effect timing are
  unchanged; the port only replaced the applet host (threading, rendering surface, resource loading and
  events). The 23 characterization/port tests pin the core behaviour.
- Out of scope (per Definition): effect-algorithm rewrites, new effects, parameter-editing UI,
  jpackage/installers, and automated UI tests.
