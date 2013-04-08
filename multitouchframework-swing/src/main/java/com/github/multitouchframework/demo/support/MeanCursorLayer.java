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

import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Collection;

public class MeanCursorLayer extends JComponent implements TouchListener<CursorUpdateEvent> {

    /**
     * Generated serial UID.
     */
    private static final long serialVersionUID = -1866299857263220891L;

    private static final Color MEAN_CURSOR_COLOR = UIManager.getColor("text");

    private static final int MEAN_CURSOR_SIZE = 6;

    private Cursor meanCursor = null;

    @Override
    public void processTouchEvent(final CursorUpdateEvent event) {
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
        getParent().repaint();
    }

    @Override
    public void paintComponent(final Graphics graphics) {
        ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (meanCursor != null) {
            // Prepare for painting
            final Point canvasMeanPoint = convertCursorToComponent(meanCursor);

            // Paint mean cursor
            graphics.setColor(MEAN_CURSOR_COLOR);
            graphics.fillOval(canvasMeanPoint.x - MEAN_CURSOR_SIZE / 2, canvasMeanPoint.y - MEAN_CURSOR_SIZE / 2,
                    MEAN_CURSOR_SIZE, MEAN_CURSOR_SIZE);
        }
    }

    private Point convertCursorToComponent(final Cursor screenCursor) {
        final Point point = new Point(screenCursor.getX(), screenCursor.getY());
        SwingUtilities.convertPointFromScreen(point, this);
        return point;
    }
}
