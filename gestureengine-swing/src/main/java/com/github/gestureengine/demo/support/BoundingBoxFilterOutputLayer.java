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

package com.github.gestureengine.demo.support;

import com.github.gestureengine.api.flow.TouchPoint;
import com.github.gestureengine.api.flow.TouchPointProcessor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.UIManager;

public class BoundingBoxFilterOutputLayer implements Layer, TouchPointProcessor {

	private static final Color BOUDING_BOX_COLOR = UIManager.getColor("nimbusOrange");

	private static final int BOUNDING_BOX_SIZE = 20;

	private static final Color FILTERED_TOUCH_POINT_COLOR = UIManager.getColor("control");

	private static final int FILTERED_TOUCH_POINT_SIZE = 6;

	private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	private final Canvas canvas;

	private Collection<TouchPoint> touchPoints = null;

	public BoundingBoxFilterOutputLayer(final Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void process(final Collection<TouchPoint> touchPoints) {
		this.touchPoints = touchPoints;
		canvas.repaint();
	}

	@Override
	public void paint(final Graphics2D g2d) {
		if ((touchPoints != null) && !touchPoints.isEmpty()) {
			// Prepare for painting
			final List<Point> canvasPoints = new ArrayList<Point>();
			for (final TouchPoint touchPoint : touchPoints) {
				final Point canvasPoint = convertTouchPointToCanvas(touchPoint);
				canvasPoints.add(canvasPoint);
			}

			// Paint bounding boxes
			g2d.setStroke(new BasicStroke());
			for (final Point canvasPoint : canvasPoints) {
				g2d.setColor(BOUDING_BOX_COLOR);
				g2d.drawRect(canvasPoint.x - BOUNDING_BOX_SIZE / 2, canvasPoint.y - BOUNDING_BOX_SIZE / 2,
						BOUNDING_BOX_SIZE - 1, BOUNDING_BOX_SIZE - 1);

				g2d.setColor(FILTERED_TOUCH_POINT_COLOR);
				g2d.fillOval(canvasPoint.x - FILTERED_TOUCH_POINT_SIZE / 2,
						canvasPoint.y - FILTERED_TOUCH_POINT_SIZE / 2, FILTERED_TOUCH_POINT_SIZE,
						FILTERED_TOUCH_POINT_SIZE);
			}
		}
	}

	private Point convertTouchPointToCanvas(final TouchPoint screenTouchPoint) {
		final int canvasX = screenTouchPoint.getX() * canvas.getWidth() / SCREEN_SIZE.width;
		final int canvasY = screenTouchPoint.getY() * canvas.getHeight() / SCREEN_SIZE.height;

		return new Point(canvasX, canvasY);
	}
}
