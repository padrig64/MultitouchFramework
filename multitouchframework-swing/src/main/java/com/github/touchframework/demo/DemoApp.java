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

package com.github.touchframework.demo;

import com.github.touchframework.api.gesture.recognition.GestureListener;
import com.github.touchframework.api.input.CursorProcessor;
import com.github.touchframework.api.input.filter.InputFilter;
import com.github.touchframework.api.region.CursorPerRegionProcessor;
import com.github.touchframework.base.gesture.recognition.drag.DragRecognizer;
import com.github.touchframework.base.gesture.recognition.pinchspread.PinchSpreadRecognizer;
import com.github.touchframework.base.gesture.recognition.tap.TapEvent;
import com.github.touchframework.base.gesture.recognition.tap.TapRecognizer;
import com.github.touchframework.base.input.filter.BoundingBoxFilter;
import com.github.touchframework.base.input.filter.NoChangeFilter;
import com.github.touchframework.base.input.source.TuioSource;
import com.github.touchframework.base.region.dispatch.DefaultCursorToRegionDispatcher;
import com.github.touchframework.demo.support.BoundingBoxFilterOutputLayer;
import com.github.touchframework.demo.support.Canvas;
import com.github.touchframework.demo.support.CursorsLayer;
import com.github.touchframework.demo.support.DummyRegion;
import com.github.touchframework.demo.support.Layer;
import com.github.touchframework.demo.support.MeanCursorLayer;
import com.github.touchframework.demo.support.MeanLinesLayer;
import com.github.touchframework.demo.support.RegionsLayer;
import com.github.touchframework.swing.input.EDTSchedulerCursorProcessor;
import com.github.touchframework.swing.region.EDTSchedulerCursorPerRegionProcessor;
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
        REGIONS("Regions", new RegionsLayer(canvas));

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

        // Create layer list
        final JPanel controlPanel = new JPanel(new MigLayout("wrap 1", "[]", "[]unrelated[]"));
        controlPanel.setBorder(new MatteBorder(0, 0, 0, 1, UIManager.getColor("nimbusBorder")));
        final JScrollPane controlScrollPane = new JScrollPane(controlPanel);
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

        final JLabel titleLabel = new JLabel("Gestures");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        gestureListPanel.add(titleLabel);

        // Add gestures to the list
        final GestureProcessor[] gestureProcessors = GestureProcessor.values();
        for (final GestureProcessor gestureProcessor : gestureProcessors) {
            final JCheckBox gestureControlCheckBox = new JCheckBox(gestureProcessor.toString());
            // TODO
            gestureControlCheckBox.setSelected(true);
            gestureListPanel.add(gestureControlCheckBox, "gap 10");
        }

        return gestureListPanel;
    }

    private Component createLayerListPanel() {
        final JPanel layerListPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[]", "[]unrelated[]related[]"));

        final JLabel titleLabel = new JLabel("Layers");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        layerListPanel.add(titleLabel);

        // Add layers to the list
        final LayerProcessor[] layerProcessors = LayerProcessor.values();
        for (final LayerProcessor layerProcessor : layerProcessors) {
            final JCheckBox layerControlCheckBox = new JCheckBox(layerProcessor.toString());
            layerControlCheckBox.addItemListener(layerControlAdapter);
            layerControlCheckBox.setSelected(true);
            layerListPanel.add(layerControlCheckBox, "gap 10");
        }

        return layerListPanel;
    }

    private void initGestureProfile() {
        // Create input source
        final TuioSource inputController = new TuioSource();

        // Configure layers for raw cursors
        final EDTSchedulerCursorProcessor edtRawCursorProcessorBlock = new EDTSchedulerCursorProcessor();
        inputController.queue(edtRawCursorProcessorBlock);
        edtRawCursorProcessorBlock.queue((CursorProcessor) LayerProcessor.RAW_CURSORS.getProcessor());

        // Configure cursor filtering
        final InputFilter boundingBoxFilter = new BoundingBoxFilter();
        inputController.queue(boundingBoxFilter);
        final NoChangeFilter noChangeFilter = new NoChangeFilter();
        boundingBoxFilter.queue(noChangeFilter);

        // Configure layers for filtered cursors
        final EDTSchedulerCursorProcessor edtFilteredCursorProcessorBlock = new EDTSchedulerCursorProcessor();
        noChangeFilter.queue(edtFilteredCursorProcessorBlock);
        edtFilteredCursorProcessorBlock.queue((CursorProcessor) LayerProcessor.FILTERED_CURSORS.getProcessor());
        edtFilteredCursorProcessorBlock.queue((CursorProcessor) LayerProcessor.FILTERED_MEAN_CURSOR.getProcessor());
        edtFilteredCursorProcessorBlock.queue((CursorProcessor) LayerProcessor.FILTERED_MEAN_LINES.getProcessor());

        // Configure cursor to region dispatcher
        final DefaultCursorToRegionDispatcher cursorToRegionDispatcher = new DefaultCursorToRegionDispatcher();
        cursorToRegionDispatcher.addRegionOnTop(new DummyRegion("TopLeft", 10, 10, 500, 500));
        cursorToRegionDispatcher.addRegionOnTop(new DummyRegion("SomewhereElse", 700, 200, 100, 100));
        noChangeFilter.queue(cursorToRegionDispatcher);

        // Configure layer for regions
        final EDTSchedulerCursorPerRegionProcessor edtCursorPerRegionProcessorBlock = new
                EDTSchedulerCursorPerRegionProcessor();
        cursorToRegionDispatcher.queue(edtCursorPerRegionProcessorBlock);
        ((RegionsLayer) LayerProcessor.REGIONS.getLayer()).setRegionProvider(cursorToRegionDispatcher);
        edtCursorPerRegionProcessorBlock.queue((CursorPerRegionProcessor) LayerProcessor.REGIONS.getProcessor());

        // Configure gestures
        final DragRecognizer dragRecognizer = new DragRecognizer();
        cursorToRegionDispatcher.queue(dragRecognizer);
        final PinchSpreadRecognizer pinchSpreadRecognizer = new PinchSpreadRecognizer();
        cursorToRegionDispatcher.queue(pinchSpreadRecognizer);
        final TapRecognizer tapRecognizer = new TapRecognizer();
        cursorToRegionDispatcher.queue(tapRecognizer);

        // Configure gesture listeners
//		dragRecognizer.queue(new GestureListener<DragEvent>() {
//
//			@Override
//			public void processGestureEvent(final DragEvent event) {
//				System.out.println(event);
//			}
//		});
//        pinchSpreadRecognizer.queue(new GestureListener<PinchSpreadEvent>() {
//
//            @Override
//            public void processGestureEvent(final PinchSpreadEvent event) {
//                System.out.println(event);
//            }
//        });
        tapRecognizer.queue(new GestureListener<TapEvent>() {
            @Override
            public void processGestureEvent(TapEvent event) {
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
