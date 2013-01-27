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

package com.github.multitouchframework.demo.support;

import com.github.multitouchframework.api.Cursor;
import com.github.multitouchframework.api.gesture.cursor.CursorEvent;
import com.github.multitouchframework.api.gesture.cursor.CursorProcessor;

import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoundingBoxFilterOutputLayer implements Layer, CursorProcessor {

    private static final Color BOUNDING_BOX_COLOR = UIManager.getColor("nimbusOrange");

    private static final int BOUNDING_BOX_SIZE = 20;

    private static final Color FILTERED_CURSOR_COLOR = UIManager.getColor("control");

    private static final int FILTERED_CURSOR_SIZE = 6;

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private final Canvas canvas;

    private Collection<Cursor> cursors = null;

    public BoundingBoxFilterOutputLayer(final Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void processTouchEvent(final CursorEvent event) {
        this.cursors = event.getCursors();
        canvas.repaint();
    }

    @Override
    public void paint(final Graphics2D g2d) {
        if ((cursors != null) && !cursors.isEmpty()) {
            // Prepare for painting
            final List<Point> canvasPoints = new ArrayList<Point>();
            for (final Cursor cursor : cursors) {
                final Point canvasPoint = convertCursorToCanvas(cursor);
                canvasPoints.add(canvasPoint);
            }

            // Paint bounding boxes
            g2d.setStroke(new BasicStroke());
            for (final Point canvasPoint : canvasPoints) {
                g2d.setColor(BOUNDING_BOX_COLOR);
                g2d.drawRect(canvasPoint.x - BOUNDING_BOX_SIZE / 2, canvasPoint.y - BOUNDING_BOX_SIZE / 2,
                        BOUNDING_BOX_SIZE - 1, BOUNDING_BOX_SIZE - 1);

                g2d.setColor(FILTERED_CURSOR_COLOR);
                g2d.fillOval(canvasPoint.x - FILTERED_CURSOR_SIZE / 2, canvasPoint.y - FILTERED_CURSOR_SIZE / 2,
                        FILTERED_CURSOR_SIZE, FILTERED_CURSOR_SIZE);
            }
        }
    }

    private Point convertCursorToCanvas(final Cursor screenCursor) {
        final int canvasX = screenCursor.getX() * canvas.getWidth() / SCREEN_SIZE.width;
        final int canvasY = screenCursor.getY() * canvas.getHeight() / SCREEN_SIZE.height;

        return new Point(canvasX, canvasY);
    }
}
