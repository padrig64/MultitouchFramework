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

import com.github.multitouchframework.api.TouchListener;
import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.cursor.Cursor;
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;

import java.util.Collection;

/**
 * Abstract implementation of a filter processing cursors.<br>Sub-classes are meant to make use of the queued blocks
 * to process the filtered cursors, by calling their
 * {@link TouchListener#processTouchEvent(com.github.multitouchframework.api.TouchEvent)}  method.
 *
 * @see AbstractFilter
 * @see CursorUpdateEvent
 */
public abstract class AbstractCursorFilter extends AbstractFilter<CursorUpdateEvent> {

    /**
     * Processes the specified cursors using the blocks/listeners that are queued/added to this input filter.
     *
     * @param userId  ID of the user touching the surface.
     * @param target  Touch target to process the cursors for.
     * @param cursors Cursors to be processed by the next blocks.
     *
     * @see #processWithNextBlocks(com.github.multitouchframework.api.TouchEvent)
     */
    protected void processWithNextBlocks(final long userId, final TouchTarget target,
                                         final Collection<Cursor> cursors) {
        processWithNextBlocks(new CursorUpdateEvent(userId, target, cursors));
    }
}
