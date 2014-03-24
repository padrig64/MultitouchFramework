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

package com.github.multitouchframework.base.cursor;

import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchTarget;

import java.util.Collection;

/**
 * Event representing updates of cursor positions.
 *
 * @see TouchEvent
 */
public class CursorUpdateEvent implements TouchEvent {

    /**
     * ID of the user touching the surface.
     */
    private final long userId;

    /**
     * Touchable target for which the event is produced.
     */
    private final TouchTarget target;

    /**
     * Cursors on the associated target.
     */
    private final Collection<Cursor> cursors;

    /**
     * Constructor specifying the ID of the user touching the surface, the touched target and the cursors representing
     * the points of contacts with the surface.
     *
     * @param userId  ID of the user touching the surface.
     * @param target  Touch target for which the event is produced.
     * @param cursors Cursors on the associated touch target.
     */
    public CursorUpdateEvent(long userId, TouchTarget target, Collection<Cursor> cursors) {
        this.userId = userId;
        this.target = target;
        this.cursors = cursors;
    }

    /**
     * @see TouchEvent#getUserId()
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * @see TouchEvent#getTouchTarget()
     */
    @Override
    public TouchTarget getTouchTarget() {
        return target;
    }

    /**
     * Gets the cursors corresponding to the points of contacts with the surface on the associated target.
     *
     * @return Cursors on the associated target.
     */
    public Collection<Cursor> getCursors() {
        return cursors;
    }
}
