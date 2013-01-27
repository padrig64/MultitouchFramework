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
import com.github.multitouchframework.api.Region;
import com.github.multitouchframework.api.gesture.cursor.CursorEvent;
import com.github.multitouchframework.api.gesture.cursor.CursorProcessor;
import com.github.multitouchframework.base.dispatch.DefaultCursorToRegionDispatcher;

import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegionsLayer implements Layer, CursorProcessor {

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private final Canvas canvas;

    private DefaultCursorToRegionDispatcher cursorToRegionDispatcher = null;

    private final Map<Region, Collection<Cursor>> cursorsForRegions = new HashMap<Region, Collection<Cursor>>();

    public RegionsLayer(final Canvas canvas) {
        this.canvas = canvas;
    }

    public DefaultCursorToRegionDispatcher getRegionProvider() {
        return cursorToRegionDispatcher;
    }

    public void setRegionProvider(final DefaultCursorToRegionDispatcher cursorToRegionDispatcher) {
        this.cursorToRegionDispatcher = cursorToRegionDispatcher;
    }

    @Override
    public void processTouchEvent(final CursorEvent event) {
        cursorsForRegions.put(event.getRegion(), event.getCursors());
        canvas.repaint();
    }

    @Override
    public void paint(final Graphics2D g2d) {
        g2d.setColor(UIManager.getColor("nimbusGreen"));

        // Draw all regions
        if (cursorToRegionDispatcher != null) {
            for (final Region region : cursorToRegionDispatcher.getRegions()) {
                final Rectangle bounds = convertScreenBoundsToCanvas(((DummyRegion) region).getTouchableBounds());
                g2d.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
            }
        }

        // Fill all touched regions
        for (final Map.Entry<Region, Collection<Cursor>> entry : cursorsForRegions.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                if (!DefaultCursorToRegionDispatcher.SCREEN_REGION.equals(entry.getKey())) {
                    final Rectangle bounds = convertScreenBoundsToCanvas(((DummyRegion) entry.getKey())
                            .getTouchableBounds());
                    g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
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
