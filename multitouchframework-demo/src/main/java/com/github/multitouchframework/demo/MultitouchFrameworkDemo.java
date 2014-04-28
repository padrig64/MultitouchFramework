/*
 * Copyright (c) 2013, Patrick Moawad
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.multitouchframework.demo;

import com.github.multitouchframework.api.TouchListener;
import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;
import com.github.multitouchframework.base.processing.filter.BoundingBoxCursorFilter;
import com.github.multitouchframework.base.processing.filter.IncludeTouchTargetFilter;
import com.github.multitouchframework.base.processing.filter.NoChangeCursorFilter;
import com.github.multitouchframework.base.processing.gesture.drag.DragEvent;
import com.github.multitouchframework.base.processing.gesture.drag.DragRecognizer;
import com.github.multitouchframework.base.processing.gesture.pinchspread.PinchSpreadEvent;
import com.github.multitouchframework.base.processing.gesture.pinchspread.PinchSpreadRecognizer;
import com.github.multitouchframework.base.processing.gesture.tap.TapEvent;
import com.github.multitouchframework.base.processing.gesture.tap.TapRecognizer;
import com.github.multitouchframework.base.processing.source.TuioSource;
import com.github.multitouchframework.base.target.ScreenTouchTarget;
import com.github.multitouchframework.demo.canvas.Canvas;
import com.github.multitouchframework.demo.canvas.CanvasLayer;
import com.github.multitouchframework.demo.canvas.TouchTargetsLayer;
import com.github.multitouchframework.demo.feedback.AbstractFeedbackLayer;
import com.github.multitouchframework.demo.feedback.BoundingBoxFilterOutputLayer;
import com.github.multitouchframework.demo.feedback.CursorsLayer;
import com.github.multitouchframework.demo.feedback.MeanCursorLayer;
import com.github.multitouchframework.demo.feedback.MeanLinesLayer;
import com.github.multitouchframework.demo.model.DemoTouchTarget;
import com.github.multitouchframework.demo.support.ScreenToComponentConverter;
import com.github.multitouchframework.experimental.dispatch.SimpleCursorToTouchTargetDispatcher;
import com.github.multitouchframework.swing.processing.scheduling.EDTScheduler;
import com.github.multitouchframework.swingcomplements.LeanScrollBarUI;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static com.github.multitouchframework.base.ChainBuilder.queue;

public class MultitouchFrameworkDemo extends JFrame {

    /**
     * Layers showing the feedback on touch events.
     * <p/>
     * They are painted in the layered pane of the demo window and are used only for demonstration of the different
     * levels of filtering.
     */
    private static enum FeedbackPresentationLayer {

        RAW_CURSORS("Raw cursors", new CursorsLayer()),
        FILTERED_MEAN_CURSOR("Filtered mean cursor", new MeanCursorLayer()),
        FILTERED_CURSORS("Filtered cursors", new BoundingBoxFilterOutputLayer()),
        FILTERED_MEAN_LINES("Filtered mean lines", new MeanLinesLayer());

        private final String presentationName;
        private final AbstractFeedbackLayer<?> layer;

        FeedbackPresentationLayer(String presentationName, AbstractFeedbackLayer<?> layer) {
            this.presentationName = presentationName;
            this.layer = layer;
        }

        public AbstractFeedbackLayer<?> getFeedbackLayer() {
            return layer;
        }

        @Override
        public String toString() {
            return presentationName;
        }
    }

    /**
     * Layers showing the touch targets on the canvas.
     * <p/>
     * They are painted in the component and are used to demonstrate the dispatching of touch events to touch targets.
     */
    private static enum CanvasPresentationLayer {

        TOUCH_TARGETS("Touch targets", new TouchTargetsLayer());

        private final String presentationName;
        private final CanvasLayer<?> layer;

        CanvasPresentationLayer(String presentationName, CanvasLayer<?> layer) {
            this.presentationName = presentationName;
            this.layer = layer;
        }

        public CanvasLayer getLayer() {
            return layer;
        }

        @Override
        public String toString() {
            return presentationName;
        }
    }

    /**
     * Entity responsible of making the layers visible/invisible depending on the selected state of the checkboxes.
     */
    private class LayerControlAdapter implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            // Search layer by name
            JCheckBox layerControlCheckBox = (JCheckBox) itemEvent.getSource();
            String layerName = layerControlCheckBox.getText();
            for (FeedbackPresentationLayer feedbackLayer : FeedbackPresentationLayer.values()) {
                if (feedbackLayer.toString().equals(layerName)) {
                    feedbackLayer.getFeedbackLayer().setVisible(layerControlCheckBox.isSelected());
                }
            }
            for (CanvasPresentationLayer canvasLayer : CanvasPresentationLayer.values()) {
                if (canvasLayer.toString().equals(layerName)) {
                    canvas.setLayerVisible(canvasLayer.getLayer(), layerControlCheckBox.isSelected());
                }
            }
        }
    }

    /**
     * Generated serial UID.
     */
    private static final long serialVersionUID = 5317328427520423914L;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultitouchFrameworkDemo.class);

    /**
     * Demo touch targets to be displayed on the canvas and reacting to touch events.
     */
    private static final TouchTarget[] TOUCH_TARGETS = new TouchTarget[]{ //
            new DemoTouchTarget("TopLeft", new Color(255, 145, 0), new Rectangle(10, 10, 100, 200)), //
            new DemoTouchTarget("SomewhereElse", new Color(145, 255, 145), new Rectangle(500, 300, 100, 100)) //
    };

    /**
     * Demo canvas component holding the touch targets.
     */
    private final Canvas canvas = new Canvas();

    private final ItemListener layerControlAdapter = new LayerControlAdapter();

    /**
     * Default constructor.
     */
    public MultitouchFrameworkDemo() {
        setTitle("MultitouchFramework Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initContentPane();
        initChain();

        // Set window size and location
        setSize(1024, 768);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);
    }

    private void initContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setName("ContentPane");

        // Create layer list
        JPanel controlPanel = new JPanel(new MigLayout("wrap 1", "[]", "[]unrelated[]"));
        controlPanel.setName("ControlPanel");
        controlPanel.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("nimbusBorder")));
        JScrollPane controlScrollPane = new JScrollPane(controlPanel);
        controlScrollPane.setName("ControlScrollPane");
        controlScrollPane.setBorder(null);
        contentPane.add(controlScrollPane, BorderLayout.WEST);

        controlPanel.add(createCanvasLayerListPanel());
        controlPanel.add(createLayeredPaneLayerListPanel(getLayeredPane()));

        // Configure canvas
        contentPane.add(canvas, BorderLayout.CENTER);
        setContentPane(contentPane);

        // Add layers to canvas
        CanvasPresentationLayer[] canvasLayers = CanvasPresentationLayer.values();
        for (int i = canvasLayers.length - 1; i >= 0; i--) {
            canvas.addLayer(canvasLayers[i].getLayer());
        }
    }

    private Component createCanvasLayerListPanel() {
        JPanel listPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));
        listPanel.setName("CanvasLayerListPanel");

        JLabel titleLabel = new JLabel("Canvas Layers");
        titleLabel.setName("CanvasLayersTitleLabel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        listPanel.add(titleLabel);

        // Add layers to the list
        CanvasPresentationLayer[] layers = CanvasPresentationLayer.values();
        for (CanvasPresentationLayer layer : layers) {
            JCheckBox layerControlCheckBox = new JCheckBox(layer.toString());
            layerControlCheckBox.setName("CanvasLayerControlCheckBox");
            layerControlCheckBox.addItemListener(layerControlAdapter);
            layerControlCheckBox.setSelected(true);
            listPanel.add(layerControlCheckBox, "gap 10");
        }

        return listPanel;
    }

    private Component createLayeredPaneLayerListPanel(JLayeredPane layeredPane) {
        JPanel listPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));
        listPanel.setName("LayeredPaneLayerListPanel");

        JLabel titleLabel = new JLabel("Layered Pane Layers");
        titleLabel.setName("LayeredPaneLayersTitleLabel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        listPanel.add(titleLabel);

        // Add layers to the list
        FeedbackPresentationLayer[] layers = FeedbackPresentationLayer.values();
        for (int i = 0; i < layers.length; i++) {
            JCheckBox layerControlCheckBox = new JCheckBox(layers[i].toString());
            layerControlCheckBox.setName("LayeredPaneLayerControlCheckBox");
            layerControlCheckBox.addItemListener(layerControlAdapter);
            layerControlCheckBox.setSelected(true);
            listPanel.add(layerControlCheckBox, "gap 10");

            layers[i].getFeedbackLayer().setOpaque(true);
            layeredPane.add(layers[i].getFeedbackLayer(), i + 3000);
            layers[i].getFeedbackLayer().setBounds(0, 0, 500, 500);
        }

        return listPanel;
    }

    /*
     * TuioSource                                => Produces CursorUpdateEvents for the touch target "screen"
     *   |
     *   |_ EDTScheduler                         => Schedules further processing on the EDT
     *   |    |_ RAW_CURSORS layer               => Displays the unfiltered cursors with blue dots (lots of events)
     *   |
     * BoundingBoxCursorFilter                   => Filters the cursors of the CursorUpdateEvents
     *   |
     * NoChangeFilter                            => Removes redundant CursorUpdateEvents
     *   |
     *   |_ EDTScheduler                         => Schedules further processing on the EDT
     *   |    |_ FILTERED_CURSORS layer          => Displays the (now filtered) cursors with gray dots
     *   |    |_ FILTERED_MEAN_CURSOR layer      => Displays the center of all cursors with a black dot
     *   |    |_ FILTERED_MEAN_LINES layer       => Displays dashed lines between the filtered cursors and their center
     *   |
     * ScreenToComponentConverter                => Converts cursors from screen coordinates to canvas coordinates
     *   |
     * SimpleCursorToTouchTargetDispatcher       => Associates cursors to touch targets (here rounded rectangles)
     *   |
     *   |_ EDTScheduler                         => Schedules further processing on the EDT
     *   |    |_ TOUCH_TARGETS layer             => Displays the touch targets as filled if touched, hollowed if not
     *   |
     * IncludeTouchTargetFilter                  => Removes CursorUpdateEvents that are not assigned to specific targets
     *   |
     *   |_ DragRecognizer                       => Recognizes the drag gesture and produces DragEvents
     *   |    |_ TouchListener<DragEvent>        => Processes the DragEvents by updating the touch targets' location
     *   |
     *   |_ PinchSpreadRecognizer                => Recognizes the pinch/spread gesture and produces PinchSpreadEvents
     *   |    |_ TouchListener<PinchSpreadEvent> => Processes the PinchSpreadEvents by updating the touch targets' size
     *   |
     *   |_ TapRecognizer                        => Recognizes the tap gesture and produces TapEvents
     *        |_ TouchListener<TapEvent>         => Processes the TapEvents by just printing them out
     */
    private void initChain() {
        // Create input source
        TuioSource source = new TuioSource(new ScreenTouchTarget());
        queue(source) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(FeedbackPresentationLayer.RAW_CURSORS.getFeedbackLayer());

        // Configure cursor filtering and layers for filtered cursors
        NoChangeCursorFilter noChangeFilter = new NoChangeCursorFilter();
        queue(source) //
                .queue(new BoundingBoxCursorFilter()) //
                .queue(noChangeFilter) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(FeedbackPresentationLayer.FILTERED_CURSORS.getFeedbackLayer(), //
                        FeedbackPresentationLayer.FILTERED_MEAN_CURSOR.getFeedbackLayer(), //
                        FeedbackPresentationLayer.FILTERED_MEAN_LINES.getFeedbackLayer());

        // Configure cursor-to-component dispatcher
//        queue(noChangeFilter) //
//                .queue(new CursorToComponentDispatcher());

        // Convert cursors to canvas
        SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher = new SimpleCursorToTouchTargetDispatcher();
        queue(noChangeFilter) //
                .queue(new ScreenToComponentConverter(canvas)) //
                .queue(cursorToTargetDispatcher);

        // Configure cursor-to-target dispatcher
        for (TouchTarget touchTarget : TOUCH_TARGETS) {
            cursorToTargetDispatcher.addTouchTargetOnTop(touchTarget);
        }

        // Configure layer for touch targets
        ((TouchTargetsLayer) CanvasPresentationLayer.TOUCH_TARGETS.getLayer()).setTouchTargetProvider
                (cursorToTargetDispatcher);
        queue(cursorToTargetDispatcher) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(CanvasPresentationLayer.TOUCH_TARGETS.getLayer());

        // Configure touch-target filters
        IncludeTouchTargetFilter<CursorUpdateEvent> touchTargetFilter = new
                IncludeTouchTargetFilter<CursorUpdateEvent>(TOUCH_TARGETS);
        queue(cursorToTargetDispatcher) //
                .queue(touchTargetFilter);

        // Configure gestures on touch targets
        queue(touchTargetFilter) //
                .queue(new DragRecognizer()) //
                        //.queue(new DragInertia()) // TODO Inertia would typically go here
                .queue(new TouchListener<DragEvent>() {

                    @Override
                    public void processTouchEvent(DragEvent event) {
                        // Move the touch target
                        Object touchTarget = event.getTouchTarget().getBaseObject();
                        if (touchTarget instanceof DemoTouchTarget) {
                            Rectangle bounds = ((DemoTouchTarget) touchTarget).getBounds();
                            bounds.translate(event.getDiffX(), event.getDiffY());
                            ((DemoTouchTarget) touchTarget).setBounds(bounds);
                        }
                    }
                });
        queue(touchTargetFilter) //
                .queue(new PinchSpreadRecognizer()) //
                .queue(new TouchListener<PinchSpreadEvent>() {

                    private Rectangle originalBounds = null;

                    @Override
                    public void processTouchEvent(PinchSpreadEvent event) {
                        switch (event.getState()) {
                            case ARMED:
                                originalBounds = ((DemoTouchTarget) event.getTouchTarget()).getBounds();
                                break;
                            case PERFORMED:
                                Object touchTarget = event.getTouchTarget().getBaseObject();
                                if (touchTarget instanceof DemoTouchTarget) {
                                    Rectangle bounds = new Rectangle(((DemoTouchTarget) event.getTouchTarget())
                                            .getBounds());
                                    bounds.setSize((int) (originalBounds.width * event.getTotalDiffScale()),
                                            (int) (originalBounds.height * event.getTotalDiffScale()));
                                    ((DemoTouchTarget) touchTarget).setBounds(bounds);
                                }
                                break;
                            case UNARMED:
                                originalBounds = null;
                                break;
                        }
                    }
                });
        queue(touchTargetFilter) //
                .queue(new TapRecognizer()) //
                .queue(new TouchListener<TapEvent>() {
                    @Override
                    public void processTouchEvent(TapEvent event) {
                        System.out.println(event);
                    }
                });

        // Activate input controller
        source.start();
    }

    /**
     * Sets the look-and-feel and shows the main frame.
     *
     * @param args Ignored.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        try {
                            UIManager.setLookAndFeel(info.getClassName());
                            UIManager.put("ScrollBarUI", LeanScrollBarUI.class.getCanonicalName());
                        } catch (ClassNotFoundException e) {
                            LOGGER.warn("Cannot set Nimbus look-and-feel", e);
                        } catch (InstantiationException e) {
                            LOGGER.warn("Cannot set Nimbus look-and-feel", e);
                        } catch (IllegalAccessException e) {
                            LOGGER.warn("Cannot set Nimbus look-and-feel", e);
                        } catch (UnsupportedLookAndFeelException e) {
                            LOGGER.warn("Cannot set Nimbus look-and-feel", e);
                        }
                        break;
                    }
                }

                JFrame frame = new MultitouchFrameworkDemo();
                frame.setVisible(true);
            }
        });
    }
}
