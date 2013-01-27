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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Collection;

public class MeanCursorLayer implements Layer, CursorProcessor {

    private static final Color MEAN_CURSOR_COLOR = UIManager.getColor("text");

    private static final int MEAN_CURSOR_SIZE = 6;

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private final Canvas canvas;

    private Cursor meanCursor = null;

    public MeanCursorLayer(final Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void processTouchEvent(final CursorEvent event) {
        final Collection<Cursor> cursors = event.getCursors();
        if (cursors.isEmpty()) {
            meanCursor = null;
        } else {
            // Calculate mean cursor
            int meanX = 0;
            int meanY = 0;
            for (final Cursor cursor : cursors) {
                meanX += cursor.getX();
                meanY += cursor.getY();
            }
            meanCursor = new Cursor(0, meanX / cursors.size(), meanY / cursors.size());
        }

        // Trigger repaint
        canvas.repaint();
    }

    @Override
    public void paint(final Graphics2D g2d) {
        if (meanCursor != null) {
            // Prepare for painting
            final Point canvasMeanPoint = convertCursorToCanvas(meanCursor);

            // Paint mean cursor
            g2d.setColor(MEAN_CURSOR_COLOR);
            g2d.fillOval(canvasMeanPoint.x - MEAN_CURSOR_SIZE / 2, canvasMeanPoint.y - MEAN_CURSOR_SIZE / 2,
                    MEAN_CURSOR_SIZE, MEAN_CURSOR_SIZE);
        }
    }

    private Point convertCursorToCanvas(final Cursor screenCursor) {
        final int canvasX = screenCursor.getX() * canvas.getWidth() / SCREEN_SIZE.width;
        final int canvasY = screenCursor.getY() * canvas.getHeight() / SCREEN_SIZE.height;

        return new Point(canvasX, canvasY);
    }
}
