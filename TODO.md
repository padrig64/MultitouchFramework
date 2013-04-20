# FIXME

* BoundingBoxFilter should reset the box if the cursor is up

# NEW STUFF

* RotateEvent
* TiltEvent
* Cursor-to-mouse converter (with setMinCursorCount(), setMaxCursotCount(), reference to Window, etc.)
* Tap gesture with min/max tap count (including delay to make sure there is no more)
* Fire gesture events only for wanted touch targets
* "DoubleTapAndSlideToZoom" gesture
* Dispatchers should also translate cursors
* UserDispatcher
* Can dispatch cursors to the REST_TOUCH_TARGET instead of the SCREEN_TOUCH_TARGET
* Make input cursors already on a touch target => remove CursorProcessor interface & repackage everything
* Swing-based cursor-to-target dispatcher
* JavaFX-based cursor-to-target dispatcher
* Generic cursor-to-target dispatcher
* Gesture lock and priority
* Better packaging: Swing, basic gestures, Windows, MacBook trackpad, JavaFX, etc.
* Windows 7/8 touch events
* JavaFX touch events
* MacBook trackpad / magic mouse events
* Mouse/keyboard simulated touch events
* Handle unintended finger up
* Keyboard modifiers in Cursor
* Screen resolution changes at runtime

# THINK ABOUT IT

* SimpleProfile: setSource(), addFilters(), addGestures(), addInertia(), addGestureListenersPerRegion()
* CursorEvent vs. CursorProcessor vs. CursorCountChange
* Cursors inertia vs. mean cursor inertia vs. gesture inertia
* 2 paradigms: up/down changes vs. current state, CursorUpdateEvent#getAdded/Removed/Updated/Stationary/CurrentCursors()
* Block state ON/OFF/BYPASS
* Decouple Surface / Screen / Canvas
* Application-specific: change color while drawing
* Application-specific: auto-pan while drawing near the screen edge (different than drag)
* Android support?
* Plugin for NASA World Wind?
* TUIO input for JavaFX: TUIO events to JavaFX touch events
* Multi-user gesture
* Multi-touch-target gesture
* DSL
