. BoundingBoxFilter should reset the box if the cursor is up
. Exception
18:03:44,932 - ERROR - AWT-EventQueue-0 - gestureengine.base.input.controller.TuioController # Could not connect to TUIO server
java.net.BindException: Address already in use
	at java.net.PlainDatagramSocketImpl.bind0(Native Method)
	at java.net.PlainDatagramSocketImpl.bind(PlainDatagramSocketImpl.java:91)
	at java.net.DatagramSocket.bind(DatagramSocket.java:372)
	at java.net.DatagramSocket.<init>(DatagramSocket.java:211)
	at java.net.DatagramSocket.<init>(DatagramSocket.java:262)
	at java.net.DatagramSocket.<init>(DatagramSocket.java:235)
	at com.illposed.osc.OSCPortIn.<init>(OSCPortIn.java:56)
	at com.mlawrie.yajtl.TUIOReceiver.init(TUIOReceiver.java:56)
	at com.mlawrie.yajtl.TUIOReceiver.<init>(TUIOReceiver.java:46)
	at com.github.gestureengine.base.input.controller.TuioController.start(TuioController.java:161)
	at com.github.gestureengine.demo.DemoApp.initGestureProfile(DemoApp.java:212)
	at com.github.gestureengine.demo.DemoApp.<init>(DemoApp.java:132)
	at com.github.gestureengine.demo.DemoApp$2.run(DemoApp.java:236)
	at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
	at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:702)
	at java.awt.EventQueue.access$400(EventQueue.java:82)
	at java.awt.EventQueue$2.run(EventQueue.java:663)
	at java.awt.EventQueue$2.run(EventQueue.java:661)
	at java.security.AccessController.doPrivileged(Native Method)
	at java.security.AccessControlContext$1.doIntersectionPrivilege(AccessControlContext.java:87)
	at java.awt.EventQueue.dispatchEvent(EventQueue.java:672)
	at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)
	at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:211)
	at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:201)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:196)
	at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:188)
	at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)


. Better packaging: swing, basic gestures, windows 7 touch, macbook trackpad, javafx, etc.
. Windows Touch events
. MacBook trackpad / magic mouse events
. Mouse/keyboard simulated touch events
. Handle unintended finger up
. Plugin for NASA World Wind?
. Android support?

