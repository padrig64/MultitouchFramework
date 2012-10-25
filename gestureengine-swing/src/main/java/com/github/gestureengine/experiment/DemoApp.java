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

package com.github.gestureengine.experiment;

import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.gestureengine.api.flow.TouchPointProcessor;
import com.github.gestureengine.api.input.controller.TouchPoint;
import com.github.gestureengine.base.input.controller.TuioController;
import com.github.gestureengine.experiment.support.Canvas;
import com.github.gestureengine.experiment.support.TouchPointLayer;

public class DemoApp extends JFrame {

	private static class EDTTouchPointProcessor implements TouchPointProcessor {

		private final TouchPointProcessor wrappedProcessor;

		public EDTTouchPointProcessor(final TouchPointProcessor wrappedProcessor) {
			this.wrappedProcessor = wrappedProcessor;
		}

		@Override
		public void process(final Collection<TouchPoint> data) {
			System.out.println("DemoApp$EDTTouchPointProcessor.process: " + data.size());

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					wrappedProcessor.process(data);
				}
			});
		}
	}

	private final Canvas touchCanvas = new Canvas();

	public DemoApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(touchCanvas);

		initGestureProfile();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new DemoApp();
				frame.setVisible(true);
			}
		});
	}

	private void initGestureProfile() {
		final TuioController inputController = new TuioController();

		final TouchPointLayer touchPointLayer = new TouchPointLayer(touchCanvas);
		touchCanvas.addLayer(touchPointLayer);
		inputController.connectNextBlock(new EDTTouchPointProcessor(touchPointLayer));

		inputController.start();
	}
}
