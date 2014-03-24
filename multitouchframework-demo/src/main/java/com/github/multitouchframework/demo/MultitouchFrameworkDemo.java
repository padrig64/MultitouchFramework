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
import com.github.multitouchframework.experimental.gesture.drag.DragInertia;
import com.github.multitouchframework.swing.processing.dispatch.CursorToComponentDispatcher;
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

    private static enum GestureProcessor {

        PAN("Drag"),
        PINCH_SPREAD("Pinch/Spread");

        private final String presentationName;

        GestureProcessor(String presentationName) {
            this.presentationName = presentationName;
        }

        @Override
        public String toString() {
            return presentationName;
        }
    }

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

    private static enum CanvasPresentationLayer {

        TOUCH_TARGETS("Touch targets", new TouchTargetsLayer());

        private final String presentationName;
        private final CanvasLayer layer;
        private final TouchListener<?> processor;

        CanvasPresentationLayer(String presentationName, TouchListener<?> layer) {
            this.presentationName = presentationName;
            this.layer = (CanvasLayer) layer;
            this.processor = layer;
        }

        public CanvasLayer getLayer() {
            return layer;
        }

        public Object getProcessor() {
            return processor;
        }

        @Override
        public String toString() {
            return presentationName;
        }
    }

    private class LayerControlAdapter implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            // Search layer by name
            JCheckBox layerControlCheckBox = (JCheckBox) itemEvent.getSource();
            String layerName = layerControlCheckBox.getText();
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

    private static final TouchTarget[] TOUCH_TARGETS = new TouchTarget[]{ //
            new DemoTouchTarget("TopLeft", new Color(255, 145, 0), new Rectangle(10, 10, 100, 200)), //
            new DemoTouchTarget("SomewhereElse", new Color(145, 255, 145), new Rectangle(500, 300, 100, 100)) //
    };

    private final Canvas canvas = new Canvas();

    private final LayerControlAdapter layerControlAdapter = new LayerControlAdapter();

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

        controlPanel.add(createGestureListPanel());
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

    private Component createGestureListPanel() {
        JPanel listPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));
        listPanel.setName("GestureListPanel");

        JLabel titleLabel = new JLabel("Gestures");
        titleLabel.setName("GesturesTitleLabel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        listPanel.add(titleLabel);

        // Add gestures to the list
        GestureProcessor[] gestureProcessors = GestureProcessor.values();
        for (GestureProcessor gestureProcessor : gestureProcessors) {
            JCheckBox gestureControlCheckBox = new JCheckBox(gestureProcessor.toString());
            gestureControlCheckBox.setName("GestureControlCheckBox");
            // TODO
            gestureControlCheckBox.setSelected(true);
            listPanel.add(gestureControlCheckBox, "gap 10");
        }

        return listPanel;
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

    private void initChain() {
        // Create input source
        TuioSource sourceNode = new TuioSource(new ScreenTouchTarget());
        queue(sourceNode) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(FeedbackPresentationLayer.RAW_CURSORS.getFeedbackLayer());

        // Configure cursor filtering and layers for filtered cursors
        NoChangeCursorFilter noChangeFilterNode = new NoChangeCursorFilter();
        queue(sourceNode) //
                .queue(new BoundingBoxCursorFilter()) //
                .queue(noChangeFilterNode) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(FeedbackPresentationLayer.FILTERED_CURSORS.getFeedbackLayer(), //
                        FeedbackPresentationLayer.FILTERED_MEAN_CURSOR.getFeedbackLayer(), //
                        FeedbackPresentationLayer.FILTERED_MEAN_LINES.getFeedbackLayer());

        // Configure cursor-to-component dispatcher
        queue(noChangeFilterNode) //
                .queue(new CursorToComponentDispatcher());

        // Convert cursors to canvas
        SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcherNode = new SimpleCursorToTouchTargetDispatcher();
        queue(noChangeFilterNode) //
                .queue(new ScreenToComponentConverter(canvas)) //
                .queue(cursorToTargetDispatcherNode);

        // Configure cursor-to-target dispatcher
        for (TouchTarget touchTarget : TOUCH_TARGETS) {
            cursorToTargetDispatcherNode.addTouchTargetOnTop(touchTarget);
        }

        // Configure layer for touch targets
        ((TouchTargetsLayer) CanvasPresentationLayer.TOUCH_TARGETS.getLayer()).setTouchTargetProvider
                (cursorToTargetDispatcherNode);
        queue(cursorToTargetDispatcherNode) //
                .queue(new EDTScheduler<CursorUpdateEvent>()) //
                .queue(CanvasPresentationLayer.TOUCH_TARGETS.getProcessor());

        // Configure touch-target filters
        IncludeTouchTargetFilter<CursorUpdateEvent> touchTargetFilterNode = new
                IncludeTouchTargetFilter<CursorUpdateEvent>(TOUCH_TARGETS);
        queue(cursorToTargetDispatcherNode) //
                .queue(touchTargetFilterNode);

        // Configure gestures on touch targets
        queue(touchTargetFilterNode) //
                .queue(new DragRecognizer()) //
                .queue(new TouchListener<DragEvent>() {

                    @Override
                    public void processTouchEvent(DragEvent event) {
                        Object touchTarget = event.getTouchTarget().getBaseObject();
                        if (touchTarget instanceof DemoTouchTarget) {
                            Rectangle bounds = ((DemoTouchTarget) touchTarget).getBounds();
                            bounds.translate(event.getDiffX(), event.getDiffY());
                            ((DemoTouchTarget) touchTarget).setBounds(bounds);
                        }
                    }
                }, new DragInertia());
        queue(touchTargetFilterNode) //
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
        queue(touchTargetFilterNode) //
                .queue(new TapRecognizer());

        // Activate input controller
        sourceNode.start();
    }

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
