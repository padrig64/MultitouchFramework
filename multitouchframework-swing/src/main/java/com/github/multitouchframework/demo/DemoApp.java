/*
 * Copyright (c) 2012, Patrick Moawad
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

import com.github.multitouchframework.api.filter.InputFilter;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.base.dispatch.SimpleCursorToTouchTargetDispatcher;
import com.github.multitouchframework.base.filter.BoundingBoxFilter;
import com.github.multitouchframework.base.filter.NoChangeFilter;
import com.github.multitouchframework.base.gesture.drag.DragRecognizer;
import com.github.multitouchframework.base.gesture.pinchspread.PinchSpreadRecognizer;
import com.github.multitouchframework.base.gesture.tap.TapEvent;
import com.github.multitouchframework.base.gesture.tap.TapRecognizer;
import com.github.multitouchframework.base.source.TuioSource;
import com.github.multitouchframework.base.touch.ScreenTouchTarget;
import com.github.multitouchframework.demo.support.BoundingBoxFilterOutputLayer;
import com.github.multitouchframework.demo.support.Canvas;
import com.github.multitouchframework.demo.support.CursorsLayer;
import com.github.multitouchframework.demo.support.DummyTouchTarget;
import com.github.multitouchframework.demo.support.Layer;
import com.github.multitouchframework.demo.support.MeanCursorLayer;
import com.github.multitouchframework.demo.support.MeanLinesLayer;
import com.github.multitouchframework.demo.support.TouchTargetsLayer;
import com.github.multitouchframework.swing.dispatch.CursorToComponentDispatcher;
import com.github.multitouchframework.swing.flow.EDTScheduler;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DemoApp extends JFrame {

    private class LayerControlAdapter implements ItemListener {

        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            // Search layer by name
            final JCheckBox layerControlCheckBox = (JCheckBox) itemEvent.getSource();
            final String layerName = layerControlCheckBox.getText();
            for (final LayerProcessor layerProcessor : LayerProcessor.values()) {
                if (layerProcessor.toString().equals(layerName)) {
                    canvas.setLayerVisible(layerProcessor.getLayer(), layerControlCheckBox.isSelected());
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApp.class);

    private static final Canvas canvas = new Canvas();

    private enum GestureProcessor {

        PAN("Drag"),
        PINCH_SPREAD("Pinch/Spread");

        private final String presentationName;

        GestureProcessor(final String presentationName) {
            this.presentationName = presentationName;
        }

        @Override
        public String toString() {
            return presentationName;
        }
    }

    private enum LayerProcessor {

        FILTERED_MEAN_CURSOR("Filtered mean cursor", new MeanCursorLayer(canvas)),
        RAW_CURSORS("Raw cursors", new CursorsLayer(canvas)),
        FILTERED_CURSORS("Filtered cursors", new BoundingBoxFilterOutputLayer(canvas)),
        FILTERED_MEAN_LINES("Filtered mean lines", new MeanLinesLayer(canvas)),
        TOUCH_TARGETS("Touch targets", new TouchTargetsLayer(canvas));

        private final String presentationName;
        private final Layer layer;
        private final Object processor;

        LayerProcessor(final String presentationName, final Object layer) {
            this.presentationName = presentationName;
            this.layer = (Layer) layer;
            this.processor = layer;
        }

        public Layer getLayer() {
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

    private final LayerControlAdapter layerControlAdapter = new LayerControlAdapter();

    public DemoApp() {
        setTitle("Touch Framework Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initContentPane();
        initGestureProfile();

        // Set window size and location
        setSize(1024, 768);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);
    }

    private void initContentPane() {
        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setName("ContentPane");

        // Create layer list
        final JPanel controlPanel = new JPanel(new MigLayout("wrap 1", "[]", "[]unrelated[]"));
        controlPanel.setName("ControlPanel");
        controlPanel.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("nimbusBorder")));
        final JScrollPane controlScrollPane = new JScrollPane(controlPanel);
        controlScrollPane.setName("ControlScrollPane");
        controlScrollPane.setBorder(null);
        contentPane.add(controlScrollPane, BorderLayout.WEST);

        controlPanel.add(createGestureListPanel());
        controlPanel.add(createLayerListPanel());

        // Configure canvas
        contentPane.add(canvas, BorderLayout.CENTER);
        setContentPane(contentPane);

        // Add layers to canvas
        final LayerProcessor[] layerProcessors = LayerProcessor.values();
        for (int i = layerProcessors.length - 1; i >= 0; i--) {
            canvas.addLayer(layerProcessors[i].getLayer());
        }
    }

    private Component createGestureListPanel() {
        final JPanel gestureListPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));
        gestureListPanel.setName("GestureListPanel");

        final JLabel titleLabel = new JLabel("Gestures");
        titleLabel.setName("GesturesTitleLabel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        gestureListPanel.add(titleLabel);

        // Add gestures to the list
        final GestureProcessor[] gestureProcessors = GestureProcessor.values();
        for (final GestureProcessor gestureProcessor : gestureProcessors) {
            final JCheckBox gestureControlCheckBox = new JCheckBox(gestureProcessor.toString());
            gestureControlCheckBox.setName("GestureControlCheckBox");
            // TODO
            gestureControlCheckBox.setSelected(true);
            gestureListPanel.add(gestureControlCheckBox, "gap 10");
        }

        return gestureListPanel;
    }

    private Component createLayerListPanel() {
        final JPanel layerListPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));
        layerListPanel.setName("LayerListPanel");

        final JLabel titleLabel = new JLabel("Layers");
        titleLabel.setName("LayersTitleLabel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        layerListPanel.add(titleLabel);

        // Add layers to the list
        final LayerProcessor[] layerProcessors = LayerProcessor.values();
        for (final LayerProcessor layerProcessor : layerProcessors) {
            final JCheckBox layerControlCheckBox = new JCheckBox(layerProcessor.toString());
            layerControlCheckBox.setName("LayerControlCheckBox");
            layerControlCheckBox.addItemListener(layerControlAdapter);
            layerControlCheckBox.setSelected(true);
            layerListPanel.add(layerControlCheckBox, "gap 10");
        }

        return layerListPanel;
    }

    private void initGestureProfile() {
        // Create input source
        final TuioSource inputController = new TuioSource(new ScreenTouchTarget());

        // Configure layers for raw cursors
        final EDTScheduler<CursorUpdateEvent> edtRawCursorProcessorBlock = new EDTScheduler<CursorUpdateEvent>();
        inputController.queue(edtRawCursorProcessorBlock);
        edtRawCursorProcessorBlock.queue((TouchListener<CursorUpdateEvent>) LayerProcessor.RAW_CURSORS.getProcessor());

        // Configure cursor filtering
        final InputFilter boundingBoxFilter = new BoundingBoxFilter();
        inputController.queue(boundingBoxFilter);
        final NoChangeFilter noChangeFilter = new NoChangeFilter();
        boundingBoxFilter.queue(noChangeFilter);

        // Configure layers for filtered cursors
        final EDTScheduler<CursorUpdateEvent> edtFilteredCursorProcessorBlock = new EDTScheduler<CursorUpdateEvent>();
        noChangeFilter.queue(edtFilteredCursorProcessorBlock);
        edtFilteredCursorProcessorBlock.queue((TouchListener<CursorUpdateEvent>) LayerProcessor.FILTERED_CURSORS
                .getProcessor());
        edtFilteredCursorProcessorBlock.queue((TouchListener<CursorUpdateEvent>) LayerProcessor.FILTERED_MEAN_CURSOR
                .getProcessor());
        edtFilteredCursorProcessorBlock.queue((TouchListener<CursorUpdateEvent>) LayerProcessor.FILTERED_MEAN_LINES
                .getProcessor());

        // Configure cursor-to-target dispatcher
        final SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher = new SimpleCursorToTouchTargetDispatcher();
        cursorToTargetDispatcher.addTouchTargetOnTop(new DummyTouchTarget("TopLeft", 10, 10, 500, 500));
        cursorToTargetDispatcher.addTouchTargetOnTop(new DummyTouchTarget("SomewhereElse", 700, 200, 100, 100));
        noChangeFilter.queue(cursorToTargetDispatcher);

        final CursorToComponentDispatcher componentDispatcher = new CursorToComponentDispatcher();
        componentDispatcher.queue(new TouchListener<CursorUpdateEvent>() {
            @Override
            public void processTouchEvent(final CursorUpdateEvent event) {
                Object baseObject = event.getTouchTarget().getBaseObject();
                if (baseObject instanceof Component) {
                    baseObject = ((Component) baseObject).getName();
                }
                System.out.println(baseObject);
            }
        });
        noChangeFilter.queue(componentDispatcher);

        // Configure layer for touch targets
        final EDTScheduler<CursorUpdateEvent> edtCursorProcessorBlock = new EDTScheduler<CursorUpdateEvent>();
        cursorToTargetDispatcher.queue(edtCursorProcessorBlock);
        ((TouchTargetsLayer) LayerProcessor.TOUCH_TARGETS.getLayer()).setTouchTargetProvider(cursorToTargetDispatcher);
        edtCursorProcessorBlock.queue((TouchListener<CursorUpdateEvent>) LayerProcessor.TOUCH_TARGETS.getProcessor());

        // Configure gestures
        final DragRecognizer dragRecognizer = new DragRecognizer();
        cursorToTargetDispatcher.queue(dragRecognizer);
        final PinchSpreadRecognizer pinchSpreadRecognizer = new PinchSpreadRecognizer();
        cursorToTargetDispatcher.queue(pinchSpreadRecognizer);
        final TapRecognizer tapRecognizer = new TapRecognizer();
        cursorToTargetDispatcher.queue(tapRecognizer);

        // Configure gesture listeners
//		dragRecognizer.queue(new TouchListener<DragEvent>() {
//
//			@Override
//			public void processTouchEvent(final DragEvent event) {
//				System.out.println(event);
//			}
//		});
//        pinchSpreadRecognizer.queue(new TouchListener<PinchSpreadEvent>() {
//
//            @Override
//            public void processTouchEvent(final PinchSpreadEvent event) {
//                System.out.println(event);
//            }
//        });
        tapRecognizer.queue(new TouchListener<TapEvent>() {
            @Override
            public void processTouchEvent(final TapEvent event) {
                System.out.println(event);
            }
        });

        // Activate input controller
        inputController.start();
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        try {
                            UIManager.setLookAndFeel(info.getClassName());
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

                final JFrame frame = new DemoApp();
                frame.setVisible(true);
            }
        });
    }
}
