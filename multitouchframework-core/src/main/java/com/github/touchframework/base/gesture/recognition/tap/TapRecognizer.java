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

package com.github.touchframework.base.gesture.recognition.tap;

import com.github.touchframework.api.input.Cursor;
import com.github.touchframework.api.region.Region;
import com.github.touchframework.base.gesture.recognition.AbstractGestureRecognizer;

import java.util.Collection;

/**
 * Entity responsible for recognizing a single-/multiple-tab gesture.<br>The recognition is made on a per-region
 * basis.<br>Note that this recognizer works best after filtering the input and limiting the number of input touch
 * events.
 *
 * @see AbstractGestureRecognizer
 * @see TapEvent
 */
public class TapRecognizer extends AbstractGestureRecognizer<TapRecognizer.RegionContext, TapEvent> {

    /**
     * Context storing the state of recognition of the gesture for a single region.
     */
    protected static class RegionContext {
        // Nothing to be done yet
    }

    /**
     * Default constructor.<br>By default, 1 cursor is the minimum required to perform the gesture, and there is no
     * maximum.
     */
    public TapRecognizer() {
        this(1, Integer.MAX_VALUE);
    }

    /**
     * @see AbstractGestureRecognizer#AbstractGestureRecognizer(int, int)
     */
    public TapRecognizer(final int minCursorCount, final int maxCursorCount) {
        super(minCursorCount, maxCursorCount);
    }

    /**
     * @see AbstractGestureRecognizer#createContext(Region)
     */
    @Override
    protected RegionContext createContext(final Region region) {
        return new RegionContext();
    }

    /**
     * @see AbstractGestureRecognizer#process(Region, Collection)
     */
    @Override
    protected void process(final RegionContext context, final Region region, final Collection<Cursor> cursors) {
        // TODO
    }
}
