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

package com.github.gestureengine.experiment.support;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.Collection;

import com.github.gestureengine.api.flow.TouchPointProcessor;
import com.github.gestureengine.api.input.controller.TouchPoint;

public class TouchPointLayer implements Layer, TouchPointProcessor {

	private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	private final Canvas canvas;

	private Collection<TouchPoint> touchPoints = null;

	public TouchPointLayer(final Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void process(Collection<TouchPoint> data) {
		touchPoints = data;
		canvas.repaint();
	}

	@Override
	public void paint(Graphics2D g2d) {
		// Paint touch points
		for (final TouchPoint touchPoint : touchPoints) {
			g2d.setColor(new Color(114, 144, 180));

			TouchPoint convertedTouchPoint = convertTouchPointToCanvas(touchPoint);
			g2d.fillOval(convertedTouchPoint.getX() - 5, convertedTouchPoint.getY() - 5, 10, 10);
		}
	}

	private TouchPoint convertTouchPointToCanvas(final TouchPoint screenTouchPoint) {
		final int canvasX = screenTouchPoint.getX() * canvas.getWidth() / SCREEN_SIZE.width;
		final int canvasY = screenTouchPoint.getY() * canvas.getHeight() / SCREEN_SIZE.height;

		return new TouchPoint(screenTouchPoint.getId(), canvasX, canvasY);
	}
}
