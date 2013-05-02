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

package com.github.multitouchframework.demo.feedback;

import com.github.multitouchframework.base.cursor.Cursor;
import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Container;
import java.awt.Point;

public abstract class AbstractFeedbackLayer<T extends TouchEvent> extends JComponent implements TouchListener<T> {

    private class LayerBoundsAdapter implements AncestorListener {

        @Override
        public void ancestorAdded(final AncestorEvent ancestorEvent) {
            updateLayerBounds();
        }

        @Override
        public void ancestorRemoved(final AncestorEvent ancestorEvent) {
            updateLayerBounds();
        }

        @Override
        public void ancestorMoved(final AncestorEvent ancestorEvent) {
            updateLayerBounds();
        }

        private void updateLayerBounds() {
            final Container parent = getParent();
            if (parent != null) {
                setBounds(0, 0, parent.getWidth(), parent.getHeight());
            }
        }
    }

    /**
     * Generated serial UID.
     */
    private static final long serialVersionUID = 3231591593056028734L;

    public AbstractFeedbackLayer() {
        super();
        addAncestorListener(new LayerBoundsAdapter());
    }

    protected void triggerRepaint() {
        if (getParent() != null) {
            getParent().repaint();
        }
    }

    protected Point convertCursorToComponent(final Cursor screenCursor) {
        final Point point = new Point(screenCursor.getX(), screenCursor.getY());
        SwingUtilities.convertPointFromScreen(point, this);
        return point;
    }
}
