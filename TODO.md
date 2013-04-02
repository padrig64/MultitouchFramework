# FIXME

* BoundingBoxFilter should reset the box if the cursor is up

# NEW STUFF

* Tap gesture with min/max tap count (including delay to make sure there is no more)
* Fire gesture events only for wanted regions
* "DouleTapAndSlideToZoom" gesture
* Can dispatch cursors to the REST_REGION instead of the SCREEN_REGION
* Make input cursors already on a region => remove CursorProcessor interface & repackage everything
* Swing-based cursor-to-region dispatcher
* JavaFX-based cursor-to-region-dispatcher
* Generic cursor-to-region-dispatcher
* Gesture lock
* Better packaging: Swing, basic gestures, Windows, MacBook trackpad, JavaFX, etc.
* Windows 7/8 touch events
* JavaFX touch events
* MacBook trackpad / magic mouse events
* Mouse/keyboard simulated touch events
* Handle unintended finger up
* Keyboard modifiers in Cursor
* Screen resolution changes at runtime

# THINK ABOUT IT

* Block state ON/OFF/BYPASS
* Decouple Surface / Screen / Canvas
* Application-specific: change color while drawing
* Application-specific: auto-pan while drawing near the screen edge (different than drag)
* Android support?
* Plugin for NASA World Wind?
* TUIO input for JavaFX: TUIO events to JavaFX touch events
