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

package com.github.multitouchframework.base.dispatch;

import com.github.multitouchframework.api.dispatch.CursorToTouchTargetDispatcher;
import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.CursorUpdateEvent;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.api.touch.TouchTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of a cursor to touch target dispatcher.<br>It provides implementation of the (de-)queuing of
 * blocks, as well as the basic dispatching of cursors to touch targets and the forwarding of the results to the queued
 * blocks. However, the discovery of a touch target for a single cursor is left to concrete sub-classes.<br>This
 * implementation makes the touch targets catch the cursors on finger down, and release the cursors on finger up only.
 * But the touch targets will hold the cursors even if they leave these touch targets. This makes it more convenient
 * for users working on small touch targets of the screen (for instance, when several users are working on different
 * small maps displayed on the same device).
 *
 * @see #findTouchedTarget(Cursor)
 */
public abstract class AbstractCursorToTouchTargetDispatcher implements CursorToTouchTargetDispatcher {

    /**
     * Mapping between cursors and touch targets resulting from the call to {@link #processTouchEvent
     * (CursorUpdateEvent)}.
     */
    private Map<Long, TouchTarget> oldCursorToTarget = new HashMap<Long, TouchTarget>(); // Initially, no cursor down

    /**
     * Cursor-per-target processors connected and processing the output touch targets and cursors from this dispatcher.
     */
    private final List<TouchListener<CursorUpdateEvent>> nextBlocks = new ArrayList<TouchListener<CursorUpdateEvent>>();

    /**
     * @see CursorToTouchTargetDispatcher#queue(Object)
     */
    @Override
    public void queue(final TouchListener<CursorUpdateEvent> cursorTargetProcessor) {
        nextBlocks.add(cursorTargetProcessor);
    }

    /**
     * @see CursorToTouchTargetDispatcher#dequeue(Object)
     */
    @Override
    public void dequeue(final TouchListener<CursorUpdateEvent> cursorTargetProcessor) {
        nextBlocks.remove(cursorTargetProcessor);
    }

    /**
     * @see CursorToTouchTargetDispatcher#processTouchEvent(com.github.multitouchframework.api.touch.TouchEvent)
     */
    @Override
    public void processTouchEvent(final CursorUpdateEvent event) {
        final Map<Long, TouchTarget> newCursorToTarget = new HashMap<Long, TouchTarget>();
        final Map<TouchTarget, Collection<Cursor>> updatesToBeForwarded = new HashMap<TouchTarget,
                Collection<Cursor>>();

        for (final Cursor cursor : event.getCursors()) {
            // Find the touch target holding the cursor
            TouchTarget assignedTarget = oldCursorToTarget.get(cursor.getId());
            if (assignedTarget == null) {
                // Find a new candidate touch target to hold the cursor
                assignedTarget = findTouchedTarget(cursor);
            } else {
                // Touch target already holds the cursor
                oldCursorToTarget.remove(cursor.getId());
            }

            if (assignedTarget != null) {
                // Update cursor for this touch target
                newCursorToTarget.put(cursor.getId(), assignedTarget);

                Collection<Cursor> cursorsForThisTarget = updatesToBeForwarded.get(assignedTarget);
                if (cursorsForThisTarget == null) {
                    cursorsForThisTarget = new HashSet<Cursor>();
                    updatesToBeForwarded.put(assignedTarget, cursorsForThisTarget);
                }
                cursorsForThisTarget.add(cursor);
            }
        }

        // Clean up old mapping to notify for touch targets that have no more cursor
        for (final TouchTarget oldTarget : oldCursorToTarget.values()) {
            if (!updatesToBeForwarded.containsKey(oldTarget)) {
                updatesToBeForwarded.put(oldTarget, Collections.<Cursor>emptySet());
            }
        }

        // Forward updated touch targets and cursors to next blocks
        for (final Map.Entry<TouchTarget, Collection<Cursor>> entry : updatesToBeForwarded.entrySet()) {
            forwardToNextBlocks(event.getUserId(), entry.getKey(), entry.getValue());
        }

        // Save mapping for next time
        oldCursorToTarget = newCursorToTarget;
    }

    /**
     * Finds the touch target that is touched by the specified cursor.
     *
     * @param cursor Cursor pointing to the touch target to be found.
     *
     * @return Touched target if found, null otherwise.
     */
    protected abstract TouchTarget findTouchedTarget(final Cursor cursor);

    /**
     * Forwards the specified touch target with its cursors to the next blocks.<br>Typically,
     * this method is called for each touch target touched by the cursors processed in {@link #processTouchEvent
     * (CursorUpdateEvent)}.
     *
     * @param userId  ID of the user touching the surface.
     * @param target  Touch target holding the specified cursors.
     * @param cursors Cursors for the specified touch target.
     */
    private void forwardToNextBlocks(final long userId, final TouchTarget target, final Collection<Cursor> cursors) {
        final CursorUpdateEvent event = new CursorUpdateEvent(userId, target, cursors);
        for (final TouchListener<CursorUpdateEvent> nextBlock : nextBlocks) {
            nextBlock.processTouchEvent(event);
        }
    }
}
