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

package com.github.multitouchframework.base.filter;

import com.github.multitouchframework.api.filter.InputFilter;
import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.api.touch.TouchTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation of an input filter.<br>Sub-classes are meant to make use of the connected cursor
 * processors to process the filtered touch input, by calling their
 * {@link TouchListener#processTouchEvent(com.github.multitouchframework.api.touch.TouchEvent)} method.
 *
 * @see InputFilter
 */
public abstract class AbstractInputFilter implements InputFilter {

    /**
     * Cursor processors connected and processing the output cursors from this input controller.
     */
    private final List<TouchListener<CursorUpdateEvent>> registeredNextBlocks = new
            ArrayList<TouchListener<CursorUpdateEvent>>();

    /**
     * Connects the specified cursor processor to this input controller block.<br>Cursor processor can be, for instance,
     * input filters or cursor-to-target dispatchers.
     *
     * @param cursorProcessor Cursor processor to be connected.
     *
     * @return Cursor processor passed as argument.
     */
    @Override
    public <T extends TouchListener<CursorUpdateEvent>> T queue(final T cursorProcessor) {
        registeredNextBlocks.add(cursorProcessor);
        return cursorProcessor;
    }

    /**
     * Disconnects the specified cursor processor from this input controller block.<br>Cursor processor can be, for
     * instance, input filters or cursor-to-target dispatchers.
     *
     * @param cursorProcessor Cursor processor to be disconnected.
     *
     * @return Cursor processor passed as argument.
     */
    @Override
    public <T extends TouchListener<CursorUpdateEvent>> T dequeue(final T cursorProcessor) {
        registeredNextBlocks.remove(cursorProcessor);
        return cursorProcessor;
    }

    /**
     * Processes the specified cursors using the blocks/listeners that are queued/added to this input filter.
     *
     * @param userId  ID of the user touching the surface.
     * @param target  Touch target to process the cursors for.
     * @param cursors Cursors to be processed by the next blocks.
     */
    protected void processWithNextBlocks(final long userId, final TouchTarget target,
                                         final Collection<Cursor> cursors) {
        processWithNextBlocks(new CursorUpdateEvent(userId, target, cursors));
    }

    /**
     * Processes the specified event using the blocks/listeners that are queued/added to this input filter.
     *
     * @param event Cursor update event to be processed by the next blocks.
     */
    protected void processWithNextBlocks(final CursorUpdateEvent event) {
        for (final TouchListener<CursorUpdateEvent> nextBlock : registeredNextBlocks) {
            nextBlock.processTouchEvent(event);
        }
    }
}
