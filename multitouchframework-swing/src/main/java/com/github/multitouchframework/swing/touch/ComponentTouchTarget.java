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

package com.github.multitouchframework.swing.touch;

import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.TouchTarget;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Point;

public class ComponentTouchTarget implements TouchTarget {

    private final Component component;

    public ComponentTouchTarget(final Component component) {
        this.component = component;
    }

    /**
     * @see TouchTarget#getBaseObject()
     */
    @Override
    public Object getBaseObject() {
        return component;
    }

    /**
     * @see TouchTarget#getMaximumWidth()
     * @see Component#getWidth()
     */
    @Override
    public int getMaximumWidth() {
        return component.getWidth();
    }

    /**
     * @see TouchTarget#getMaximumHeight()
     * @see Component#getHeight()
     */
    @Override
    public int getMaximumHeight() {
        return component.getHeight();
    }

    /**
     * @see TouchTarget#isTouched(Cursor)
     */
    @Override
    public boolean isTouched(final Cursor cursor) {
        final Point cursorPosition = new Point(cursor.getX(), cursor.getY());
        SwingUtilities.convertPointFromScreen(cursorPosition, component);
        return component.contains(cursorPosition.x, cursorPosition.y);
    }
}
