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

package com.github.touchframework.base.input.filter;

import com.github.touchframework.api.input.Cursor;
import com.github.touchframework.api.input.CursorProcessor;
import com.github.touchframework.api.input.filter.InputFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation of an input filter.<br>Sub-classes are meant to make use of the connected cursor processor to
 * process the filtered touch input, by calling their {@link CursorProcessor#process(java.util.Collection)} method.
 *
 * @see InputFilter
 */
public abstract class AbstractInputFilter implements InputFilter {

    /**
     * Cursor processors connected and processing the output cursors from this input controller.
     */
    private final List<CursorProcessor> nextBlocks = new ArrayList<CursorProcessor>();

    /**
     * Connects the specified cursor processor to this input controller block.<br>Cursor processor can be, for instance,
     * input filters or cursor-to-region dispatchers.
     *
     * @param cursorProcessor Cursor processor to be connected.
     */
    @Override
    public void queue(final CursorProcessor cursorProcessor) {
        nextBlocks.add(cursorProcessor);
    }

    /**
     * Disconnects the specified cursor processor from this input controller block.<br>Cursor processor can be, for
     * instance, input filters or cursor-to-region dispatchers.
     *
     * @param cursorProcessor Cursor processor to be disconnected.
     */
    @Override
    public void dequeue(final CursorProcessor cursorProcessor) {
        nextBlocks.remove(cursorProcessor);
    }

    /**
     * Processes the specified cursors using the blocks/listeners that are queued/added to this input filter.
     *
     * @param cursors Cursors to be processed by the next blocks.
     */
    protected void processWithNextBlocks(final Collection<Cursor> cursors) {
        for (final CursorProcessor nextBlock : nextBlocks) {
            nextBlock.processCursors(cursors);
        }
    }
}
