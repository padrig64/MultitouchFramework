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

package com.github.multitouchframework.base.processing.filter;

import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a filter.
 * <p/>
 * Sub-classes are meant to make use of the queued blocks to process the filtered touch event,
 * by calling their {@link TouchListener#processTouchEvent(TouchEvent)} method.
 *
 * @see Filter
 * @see TouchEvent
 */
public abstract class AbstractFilter<E extends TouchEvent> implements Filter<E> {

    /**
     * Cursor processors connected and processing the output cursors from this input controller.
     */
    private final List<TouchListener<E>> registeredNextBlocks = new ArrayList<TouchListener<E>>();

    /**
     * Connects the specified cursor processor to this input controller block.
     * <p/>
     * Cursor processor can be, for instance, input filters or cursor-to-target dispatchers.
     *
     * @param cursorProcessor Cursor processor to be connected.
     */
    @Override
    public void queue(TouchListener<E> cursorProcessor) {
        registeredNextBlocks.add(cursorProcessor);
    }

    /**
     * Disconnects the specified cursor processor from this input controller block.
     * <p/>
     * Cursor processor can be, for instance, input filters or cursor-to-target dispatchers.
     *
     * @param cursorProcessor Cursor processor to be disconnected.
     */
    @Override
    public void dequeue(TouchListener<E> cursorProcessor) {
        registeredNextBlocks.remove(cursorProcessor);
    }

    /**
     * Processes the specified event using the blocks/listeners that are queued/added to this input filter.
     *
     * @param event Cursor update event to be processed by the next blocks.
     */
    protected void processWithNextBlocks(E event) {
        for (TouchListener<E> nextBlock : registeredNextBlocks) {
            nextBlock.processTouchEvent(event);
        }
    }
}
