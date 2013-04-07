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
import com.github.multitouchframework.api.touch.TouchTarget;
import com.github.multitouchframework.base.dispatch.SimpleCursorToTouchTargetDispatcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TouchTargetsLayer implements Layer, TouchListener<CursorUpdateEvent> {

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private final Canvas canvas;

    private SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher = null;

    private final Map<TouchTarget, Collection<Cursor>> cursorsForTargets = new HashMap<TouchTarget,
            Collection<Cursor>>();

    public TouchTargetsLayer(final Canvas canvas) {
        this.canvas = canvas;
    }

    public SimpleCursorToTouchTargetDispatcher getTouchTargetProvider() {
        return cursorToTargetDispatcher;
    }

    public void setTouchTargetProvider(final SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher) {
        this.cursorToTargetDispatcher = cursorToTargetDispatcher;
    }

    @Override
    public void processTouchEvent(final CursorUpdateEvent event) {
        cursorsForTargets.put(event.getTouchTarget(), event.getCursors());
        canvas.repaint();
    }

    @Override
    public void paint(final Graphics2D g2d) {
        g2d.setColor(new Color(255, 145, 0));

        // Draw all touch targets
        if (cursorToTargetDispatcher != null) {
            for (final TouchTarget target : cursorToTargetDispatcher.getTouchTargets()) {
                final Rectangle bounds = convertScreenBoundsToCanvas(((DummyTouchTarget) target).getTouchableBounds());
                g2d.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 20, 20);
            }
        }

        // Fill all touched touch targets
        for (final Map.Entry<TouchTarget, Collection<Cursor>> entry : cursorsForTargets.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (!SimpleCursorToTouchTargetDispatcher.SCREEN_TOUCH_TARGET.equals(entry.getKey())) {
                    final Rectangle bounds = convertScreenBoundsToCanvas(((DummyTouchTarget) entry.getKey())
                            .getTouchableBounds());
                    g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
                }
            }
        }
    }

    private Rectangle convertScreenBoundsToCanvas(final Rectangle screenBounds) {
        final int canvasX = screenBounds.x * canvas.getWidth() / SCREEN_SIZE.width;
        final int canvasY = screenBounds.y * canvas.getHeight() / SCREEN_SIZE.height;
        final int canvasWidth = screenBounds.width * canvas.getWidth() / SCREEN_SIZE.width;
        final int canvasHeight = screenBounds.height * canvas.getHeight() / SCREEN_SIZE.height;

        return new Rectangle(canvasX, canvasY, canvasWidth, canvasHeight);
    }
}
