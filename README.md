# Ski Addict Projector App

Android controller for Ski Addict with a separate HDMI/projector animation output.

## Responsive controller UI

The controller now switches automatically by Android window width:

- **Compact (< 600dp):** phone / narrow window, vertical scrolling layout.
- **Medium (600–899dp):** tablet portrait / medium window, two-column content where useful.
- **Expanded (>= 900dp):** tablet landscape / large Android desktop window, navigation + dashboard + settings columns.

The Activity is resizable and no longer locked to landscape, so rotation and desktop-style window resizing can rebuild the correct layout.

> PC support here means the Android app running in an Android-compatible desktop/window environment. A native Windows/macOS executable would be a separate target.

## Modular course architecture

Each course is its own class under:

`app/src/main/java/fun/skiaddict/projector/courses/`

Current modules:

- `AlpineBeginnerCourse.java`
- `SCurveCourse.java`
- `StraightRunCourse.java`
- `KidsAdventureCourse.java`
- `ObstaclesCourse.java`

Shared rendering helpers live in `CourseRenderContext.java`.
The app discovers selectable courses through `CourseRegistry.java`.

### Adding a new course

1. Create a class that implements `CourseModule`.
2. Give it an `id()`, `title()`, `subtitle()`, `icon()`, and `render(...)`.
3. Add one line in `CourseRegistry`.

No switch statement in the main projection engine needs to be changed.

### Example: add wind to Alpine

Wind-specific movement can be implemented only inside `AlpineBeginnerCourse.render(...)`.
If wind later needs a user-adjustable slider, add a shared value to `AppState` and expose it in the controller UI; the Alpine module can read that value through `CourseRenderContext.state`.

## Current controls

- Speed: 5–30 km/h
- Object size: 10–50
- Obstacle probability: 30–70%
- Longitudinal spacing: 20–80
- Lateral spacing: 20–80
- Timer
- Start / pause / stop
- Saved controller values
- HDMI secondary-display presentation
