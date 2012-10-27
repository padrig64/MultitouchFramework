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

package com.github.gestureengine.demo;

import com.github.gestureengine.api.flow.TouchPointProcessor;
import com.github.gestureengine.api.input.filter.InputFilter;
import com.github.gestureengine.base.input.controller.TuioController;
import com.github.gestureengine.base.input.filter.BoundingBoxFilter;
import com.github.gestureengine.base.input.filter.NoChangeFilter;
import com.github.gestureengine.demo.support.BoundingBoxFilterOutputLayer;
import com.github.gestureengine.demo.support.Canvas;
import com.github.gestureengine.demo.support.Layer;
import com.github.gestureengine.demo.support.MeanLinesLayer;
import com.github.gestureengine.demo.support.MeanPointLayer;
import com.github.gestureengine.demo.support.TouchPointsLayer;
import com.github.gestureengine.swing.flow.EDTTouchPointProcessorBlock;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private enum LayerProcessor {

		MEAN_POINT("Mean point", new MeanPointLayer(canvas)),
		TOUCH_POINTS("Touch points", new TouchPointsLayer(canvas)),
		BOUNDING_BOX_FILTER_OUTPUT("Bounding box filter output", new BoundingBoxFilterOutputLayer(canvas)),
		MEAN_LINES("Mean lines", new MeanLinesLayer(canvas));

		private final String presentationName;
		private final Layer layer;
		private final TouchPointProcessor touchPointProcessor;

		LayerProcessor(final String presentationName, final Object layer) {
			this.presentationName = presentationName;
			this.layer = (Layer) layer;
			this.touchPointProcessor = (TouchPointProcessor) layer;
		}

		public Layer getLayer() {
			return layer;
		}

		public TouchPointProcessor getTouchPointProcessor() {
			return touchPointProcessor;
		}

		@Override
		public String toString() {
			return presentationName;
		}
	}

	private final LayerControlAdapter layerControlAdapter = new LayerControlAdapter();

	public DemoApp() {
		setTitle("GestureEngine Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		initComponents();
		initGestureProfile();

		// Set window size and location
		setSize(1024, 768);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);
	}

	private void initComponents() {
		final JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(canvas);

		// Create layer list
		final JPanel layerListPanel = new JPanel();
		layerListPanel.setLayout(new BoxLayout(layerListPanel, BoxLayout.PAGE_AXIS));
		contentPane.add(new JScrollPane(layerListPanel), BorderLayout.WEST);

		// Add layers to the list
		final LayerProcessor[] layerProcessors = LayerProcessor.values();
		for (final LayerProcessor layerProcessor : layerProcessors) {
			final JCheckBox layerControlCheckBox = new JCheckBox(layerProcessor.toString());
			layerControlCheckBox.addItemListener(layerControlAdapter);
			layerControlCheckBox.setSelected(true);
			layerListPanel.add(layerControlCheckBox);
		}

		// Configure canvas
		for (int i = layerProcessors.length - 1; i >= 0; i--) {
			canvas.addLayer(layerProcessors[i].getLayer());
		}
		contentPane.add(canvas, BorderLayout.CENTER);
		setContentPane(contentPane);
	}

	private void initGestureProfile() {
		final TuioController inputController = new TuioController();

		// Configure raw touch point processing
		final EDTTouchPointProcessorBlock edtRawTouchPointProcessorBlock = new EDTTouchPointProcessorBlock();
		edtRawTouchPointProcessorBlock.queue(LayerProcessor.TOUCH_POINTS.getTouchPointProcessor());
		inputController.queue(edtRawTouchPointProcessorBlock);

		// Configure touch point filtering
		final InputFilter boundingBoxFilter = new BoundingBoxFilter();
		inputController.queue(boundingBoxFilter);

		final NoChangeFilter noChangeFilter = new NoChangeFilter();
		boundingBoxFilter.queue(noChangeFilter);

		final EDTTouchPointProcessorBlock edtFilteredTouchPointProcessBlock = new EDTTouchPointProcessorBlock();
		edtFilteredTouchPointProcessBlock.queue(LayerProcessor.BOUNDING_BOX_FILTER_OUTPUT.getTouchPointProcessor());
		edtFilteredTouchPointProcessBlock.queue(LayerProcessor.MEAN_POINT.getTouchPointProcessor());
		edtFilteredTouchPointProcessBlock.queue(LayerProcessor.MEAN_LINES.getTouchPointProcessor());
		noChangeFilter.queue(edtFilteredTouchPointProcessBlock);

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
