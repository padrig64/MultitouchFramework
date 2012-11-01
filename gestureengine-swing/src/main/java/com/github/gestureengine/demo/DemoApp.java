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

import com.github.gestureengine.api.area.CursorToAreaDispatcher;
import com.github.gestureengine.api.area.TouchableArea;
import com.github.gestureengine.api.flow.Cursor;
import com.github.gestureengine.api.flow.CursorAreaProcessor;
import com.github.gestureengine.api.flow.CursorProcessor;
import com.github.gestureengine.api.input.filter.InputFilter;
import com.github.gestureengine.base.area.CursorToScreenDispatcher;
import com.github.gestureengine.base.input.controller.TuioController;
import com.github.gestureengine.base.input.filter.BoundingBoxFilter;
import com.github.gestureengine.base.input.filter.NoChangeFilter;
import com.github.gestureengine.demo.support.BoundingBoxFilterOutputLayer;
import com.github.gestureengine.demo.support.Canvas;
import com.github.gestureengine.demo.support.CursorsLayer;
import com.github.gestureengine.demo.support.Layer;
import com.github.gestureengine.demo.support.MeanCursorLayer;
import com.github.gestureengine.demo.support.MeanLinesLayer;
import com.github.gestureengine.swing.flow.EDTCursorProcessorBlock;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.swing.MigLayout;
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

		FILTERED_MEAN_CURSOR("Filtered mean cursor", new MeanCursorLayer(canvas)),
		RAW_CURSORS("Raw cursors", new CursorsLayer(canvas)),
		FILTERED_CURSORS("Filtered cursors", new BoundingBoxFilterOutputLayer(canvas)),
		FILTERED_MEAN_LINES("Filtered mean lines", new MeanLinesLayer(canvas));

		private final String presentationName;
		private final Layer layer;
		private final CursorProcessor cursorProcessor;

		LayerProcessor(final String presentationName, final Object layer) {
			this.presentationName = presentationName;
			this.layer = (Layer) layer;
			this.cursorProcessor = (CursorProcessor) layer;
		}

		public Layer getLayer() {
			return layer;
		}

		public CursorProcessor getCursorProcessor() {
			return cursorProcessor;
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
		contentPane.setBackground(Color.WHITE);
		setContentPane(canvas);

		// Create layer list
		final JPanel layerListPanel = new JPanel(new MigLayout("wrap 1", "[]", "[]unrelated[]related[]"));
		contentPane.add(new JScrollPane(layerListPanel), BorderLayout.WEST);

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

		// Configure canvas
		for (int i = layerProcessors.length - 1; i >= 0; i--) {
			canvas.addLayer(layerProcessors[i].getLayer());
		}
		contentPane.add(canvas, BorderLayout.CENTER);
		setContentPane(contentPane);
	}

	private void initGestureProfile() {
		// Create input source
		final TuioController inputController = new TuioController();

		// Configure layers for raw cursors
		final EDTCursorProcessorBlock edtRawCursorProcessorBlock = new EDTCursorProcessorBlock();
		edtRawCursorProcessorBlock.queue(LayerProcessor.RAW_CURSORS.getCursorProcessor());
		inputController.queue(edtRawCursorProcessorBlock);

		// Configure cursor filtering
		final InputFilter boundingBoxFilter = new BoundingBoxFilter();
		inputController.queue(boundingBoxFilter);
		final NoChangeFilter noChangeFilter = new NoChangeFilter();
		boundingBoxFilter.queue(noChangeFilter);

		// Configure layers for filtered cursors
		final EDTCursorProcessorBlock edtFilteredCursorProcessorBlock = new EDTCursorProcessorBlock();
		edtFilteredCursorProcessorBlock.queue(LayerProcessor.FILTERED_CURSORS.getCursorProcessor());
		edtFilteredCursorProcessorBlock.queue(LayerProcessor.FILTERED_MEAN_CURSOR.getCursorProcessor());
		edtFilteredCursorProcessorBlock.queue(LayerProcessor.FILTERED_MEAN_LINES.getCursorProcessor());
		noChangeFilter.queue(edtFilteredCursorProcessorBlock);

		// Configure cursor to area dispatching
		final CursorToAreaDispatcher screenProcessor = new CursorToScreenDispatcher();
		screenProcessor.queue(new CursorAreaProcessor() {
			@Override
			public void process(Collection<Cursor> cursors, Collection<TouchableArea> touchableAreas) {
				System.out.println(
						"DemoApp.process: " + cursors + " " + touchableAreas.iterator().next().getTouchableBounds());
			}
		});
		noChangeFilter.queue(screenProcessor);

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
