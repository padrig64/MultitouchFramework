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

import com.github.gestureengine.base.input.controller.TuioController;
import com.github.gestureengine.demo.support.Canvas;
import com.github.gestureengine.demo.support.TouchPointLayer;
import com.github.gestureengine.swing.flow.EDTTouchPointProcessorBlock;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DemoApp extends JFrame {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 5317328427520423914L;

	private final Canvas touchCanvas = new Canvas();

	public DemoApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(touchCanvas);

		initGestureProfile();

		// Set window size and location
		setSize(640, 480);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);

	}

	private void initGestureProfile() {
		final TuioController inputController = new TuioController();

		final TouchPointLayer touchPointLayer = new TouchPointLayer(touchCanvas);
		touchCanvas.addLayer(touchPointLayer);

		final EDTTouchPointProcessorBlock edtProcessorBlock = new EDTTouchPointProcessorBlock();
		edtProcessorBlock.connect(touchPointLayer);
		inputController.connect(edtProcessorBlock);

		inputController.start();
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new DemoApp();
				frame.setVisible(true);
			}
		});
	}
}
