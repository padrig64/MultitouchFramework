/*
 * Copyright (c) 2013, Patrick Moawad
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

package com.github.multitouchframework.demo.canvas;

import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.api.touch.TouchTarget;
import com.github.multitouchframework.base.dispatch.SimpleCursorToTouchTargetDispatcher;
import com.github.multitouchframework.demo.model.DemoTouchTarget;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TouchTargetsLayer implements CanvasLayer, TouchListener<CursorUpdateEvent> {

    private SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher = null;

    private final Map<TouchTarget, Collection<Cursor>> cursorsForTargets = new HashMap<TouchTarget,
            Collection<Cursor>>();

    public SimpleCursorToTouchTargetDispatcher getTouchTargetProvider() {
        return cursorToTargetDispatcher;
    }

    public void setTouchTargetProvider(final SimpleCursorToTouchTargetDispatcher cursorToTargetDispatcher) {
        this.cursorToTargetDispatcher = cursorToTargetDispatcher;
    }

    @Override
    public void processTouchEvent(final CursorUpdateEvent event) {
        cursorsForTargets.put(event.getTouchTarget(), event.getCursors());
//        canvas.repaint();
    }

    @Override
    public void paint(final Graphics2D g2d) {
        // Draw all touch targets
        if (cursorToTargetDispatcher != null) {
            for (final TouchTarget target : cursorToTargetDispatcher.getTouchTargets()) {
                g2d.setColor(((DemoTouchTarget) target).getColor());
                final Rectangle bounds = ((DemoTouchTarget) target).getBounds();
                g2d.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 20, 20);
            }
        }

        // Fill all touched touch targets
        for (final Map.Entry<TouchTarget, Collection<Cursor>> entry : cursorsForTargets.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (!SimpleCursorToTouchTargetDispatcher.SCREEN_TOUCH_TARGET.equals(entry.getKey())) {
                    g2d.setColor(((DemoTouchTarget) entry.getKey()).getColor());
                    final Rectangle bounds = ((DemoTouchTarget) entry.getKey()).getBounds();
                    g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
                }
            }
        }
    }
}
