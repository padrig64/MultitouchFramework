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

package com.github.multitouchframework.demo.support;

import com.github.multitouchframework.api.filter.InputFilter;
import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ScreenToComponentConverter implements InputFilter {

    private final List<TouchListener<CursorUpdateEvent>> nextBlocks = new ArrayList<TouchListener<CursorUpdateEvent>>();

    private final Component referenceComponent;

    public ScreenToComponentConverter(final Component referenceComponent) {
        this.referenceComponent = referenceComponent;
    }

    @Override
    public <T extends TouchListener<CursorUpdateEvent>> T queue(final T nextBlock) {
        nextBlocks.add(nextBlock);
        return nextBlock;
    }

    @Override
    public <T extends TouchListener<CursorUpdateEvent>> T dequeue(final T nextBlock) {
        nextBlocks.remove(nextBlock);
        return nextBlock;
    }

    @Override
    public void processTouchEvent(final CursorUpdateEvent event) {
        // Convert all cursors
        final List<Cursor> newCursors = new ArrayList<Cursor>();
        for (final Cursor cursor : event.getCursors()) {
            final Point cursorLocation = new Point(cursor.getX(), cursor.getY());
            SwingUtilities.convertPointFromScreen(cursorLocation, referenceComponent);
            newCursors.add(new Cursor(cursor.getId(), cursorLocation.x, cursorLocation.y));
        }

        // Notify listeners
        final CursorUpdateEvent newEvent = new CursorUpdateEvent(event.getUserId(), event.getTouchTarget(), newCursors);
        for (final TouchListener<CursorUpdateEvent> nextBlock : nextBlocks) {
            nextBlock.processTouchEvent(newEvent);
        }
    }
}
