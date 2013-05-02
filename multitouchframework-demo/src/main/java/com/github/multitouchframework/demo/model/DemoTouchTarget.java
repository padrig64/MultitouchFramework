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

package com.github.multitouchframework.demo.model;

import com.github.multitouchframework.base.cursor.Cursor;
import com.github.multitouchframework.api.TouchTarget;

import java.awt.Color;
import java.awt.Rectangle;

public class DemoTouchTarget implements TouchTarget {

    private final String id;
    private final Color color;
    private Rectangle bounds = null;

    public DemoTouchTarget(final String id, final Color color, final int x, final int y, final int width,
                           final int height) {
        this(id, color, new Rectangle(x, y, width, height));
    }

    public DemoTouchTarget(final String id, final Color color, final Rectangle bounds) {
        this.id = id;
        this.color = color;
        this.bounds = bounds;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(final Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * @see TouchTarget#getBaseObject()
     */
    @Override
    public Object getBaseObject() {
        return this;
    }

    @Override
    public int getMaximumWidth() {
        return bounds.width;
    }

    @Override
    public int getMaximumHeight() {
        return bounds.height;
    }

    @Override
    public boolean isTouched(final Cursor cursor) {
        return bounds.contains(cursor.getX(), cursor.getY());
    }

    @Override
    public String toString() {
        return id;
    }
}
