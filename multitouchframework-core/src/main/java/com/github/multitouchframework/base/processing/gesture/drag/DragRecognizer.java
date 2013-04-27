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

package com.github.multitouchframework.base.processing.gesture.drag;

import com.github.multitouchframework.base.cursor.Cursor;
import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.processing.gesture.AbstractGestureRecognizer;

import java.util.Collection;

/**
 * Entity responsible for recognizing a drag/pan/etc. gesture.<br>The recognition is made on a per-target basis and is
 * based on the location of the mean cursor (average of all the cursors).<br>Note that this recognizer works best after
 * filtering the input and limiting the number of input touch events.
 *
 * @see AbstractGestureRecognizer
 * @see DragEvent
 */
public class DragRecognizer extends AbstractGestureRecognizer<DragRecognizer.TouchTargetContext, DragEvent> {

    /**
     * Context storing the state of recognition of the gesture for a single touch target.<br>The recognition is based on
     * the movement of the mean point of all cursors on the touched surface, but making sure that changing the number of
     * cursors has no influence on the gesture.
     */
    protected static class TouchTargetContext {

        /**
         * ID of the user performing the gesture.
         */
        public long userId = -1;

        /**
         * Strong reference to the touch target when the gesture is not unarmed to prevent garbage collection.<br>This
         * makes sure that we will get the complete set of events.
         */
        public TouchTarget activeTarget = null;

        /**
         * Last state of the gesture for the touch target.
         */
        public DragEvent.State previousState = DragEvent.State.UNARMED;

        /**
         * Last number of cursors on the touch target.
         */
        public int previousCursorCount = 0;

        /**
         * Last X coordinate of the reference point used to calculate the total movement of the gesture on the touch
         * target.
         */
        public int referenceX = -1;

        /**
         * Last Y coordinate of the reference point used to calculate the total movement of the gesture on the touch
         * target.
         */
        public int referenceY = -1;

        /**
         * Last X coordinate of the mean cursor.
         */
        public int previousMeanX = -1;

        /**
         * Last Y coordinate of the mean cursor.
         */
        public int previousMeanY = -1;
    }

    /**
     * Default minimum number of cursors needed to perform the gesture.
     */
    public static final int DEFAULT_MIN_CURSOR_COUNT = 1;

    /**
     * Default maximum number of cursors allowed to performed the gesture.
     */
    public static final int DEFAULT_MAX_CURSOR_COUNT = Integer.MAX_VALUE; // No maximum

    /**
     * Default constructor.<br>By default, 1 cursor is the minimum required to perform the gesture, and there is no
     * maximum.
     */
    public DragRecognizer() {
        this(DEFAULT_MIN_CURSOR_COUNT, DEFAULT_MAX_CURSOR_COUNT);
    }

    /**
     * @see AbstractGestureRecognizer#AbstractGestureRecognizer(int, int)
     */
    public DragRecognizer(final int minCursorCount, final int maxCursorCount) {
        super(minCursorCount, maxCursorCount);
    }

    /**
     * @see AbstractGestureRecognizer#createContext(long, TouchTarget)
     */
    protected TouchTargetContext createContext(final long userId, final TouchTarget target) {
        return new TouchTargetContext();
    }

    /**
     * @see AbstractGestureRecognizer#process(Object, long, TouchTarget, Collection)
     */
    @Override
    protected void process(final TouchTargetContext context, final long userId, final TouchTarget target,
                           final Collection<Cursor> cursors) {
        final int cursorCount = cursors.size();

        // Test this first because it is the most likely to happen
        if (isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
            if (context.previousCursorCount == cursorCount) {
                processDragPerformed(context, cursors);
            } else {
                processValidCursorCountChanged(context, cursors);
            }
        } else if (!isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
            processDragArmed(context, userId, target, cursors);
        } else if (isCursorCountValid(context.previousCursorCount) && !isCursorCountValid(cursorCount)) {
            processDragUnarmed(context);
        } else {
            processNothingHappened(context);
        }
    }

    /**
     * Handles the fact that the change of input cursors armed the gesture.
     *
     * @param context Target context to be updated.
     * @param userId  ID of the user performing the gesture.
     * @param target  Touch target to which the cursors are associated.
     * @param cursors New input cursors.
     */
    private void processDragArmed(final TouchTargetContext context, final long userId, final TouchTarget target,
                                  final Collection<Cursor> cursors) {
        // Trigger listeners
        final DragEvent event = new DragEvent(userId, target, DragEvent.State.ARMED, 0, 0, 0, 0);
        fireGestureEvent(event);

        // Calculate mean point
        final int cursorCount = cursors.size();
        int meanX = 0;
        int meanY = 0;
        for (final Cursor cursor : cursors) {
            meanX += cursor.getX();
            meanY += cursor.getY();
        }
        meanX /= cursorCount;
        meanY /= cursorCount;

        // Save context
        context.userId = userId;
        context.activeTarget = target; // Prevent garbage collection
        context.previousState = DragEvent.State.ARMED;
        context.previousCursorCount = cursorCount;
        context.referenceX = meanX;
        context.referenceY = meanY;
        context.previousMeanX = meanX;
        context.previousMeanY = meanY;
    }

    /**
     * Handles the fact that the change of input cursors corresponds to a drag movement.
     *
     * @param context Target context to be used and updated.
     * @param cursors New input cursors.
     */
    private void processDragPerformed(final TouchTargetContext context, final Collection<Cursor> cursors) {
        // Calculate mean point
        final int cursorCount = cursors.size();
        int meanX = 0;
        int meanY = 0;
        for (final Cursor cursor : cursors) {
            meanX += cursor.getX();
            meanY += cursor.getY();
        }
        meanX /= cursorCount;
        meanY /= cursorCount;

        // Determine change
        final int offsetX = meanX - context.previousMeanX;
        final int offsetY = meanY - context.previousMeanY;

        // Trigger listeners
        final DragEvent event = new DragEvent(context.userId, context.activeTarget, DragEvent.State.PERFORMED,
                offsetX, offsetY, meanX - context.referenceX, meanY - context.referenceY);
        fireGestureEvent(event);

        // Save context (no change of reference point or active touch target)
        context.previousState = DragEvent.State.PERFORMED;
        context.previousCursorCount = cursorCount;
        context.previousMeanX = meanX;
        context.previousMeanY = meanY;
    }

    /**
     * Handles the fact that the number of cursors changed.<br>Note that it is expected here that the validity of the
     * cursor count has already been checked before.
     *
     * @param context Touch target context to be used and updated.
     * @param cursors New input cursors.
     */
    private void processValidCursorCountChanged(final TouchTargetContext context, final Collection<Cursor> cursors) {
        // Calculate mean point
        final int cursorCount = cursors.size();
        int meanX = 0;
        int meanY = 0;
        for (final Cursor cursor : cursors) {
            meanX += cursor.getX();
            meanY += cursor.getY();
        }
        meanX /= cursorCount;
        meanY /= cursorCount;

        // No need to trigger any listener

        // Calculate new reference point to have the same total difference
        final int newReferenceX = meanX - context.previousMeanX + context.referenceX;
        final int newReferenceY = meanY - context.previousMeanY + context.referenceY;

        // Save context (no change of state or active touch target)
        context.previousCursorCount = cursorCount;
        context.referenceX = newReferenceX;
        context.referenceY = newReferenceY;
        context.previousMeanX = meanX;
        context.previousMeanY = meanY;
    }

    /**
     * Handles the fact that the change of input cursors unarmed the gesture.
     *
     * @param context Target context to be updated.
     */
    private void processDragUnarmed(final TouchTargetContext context) {
        // Trigger listeners
        final DragEvent event = new DragEvent(context.userId, context.activeTarget, DragEvent.State.UNARMED, 0, 0,
                context.previousMeanX - context.referenceX, context.previousMeanY - context.referenceY);
        fireGestureEvent(event);

        // Clear context
        context.userId = -1;
        context.activeTarget = null; // Allow garbage collection
        context.previousState = DragEvent.State.UNARMED;
        context.previousCursorCount = 0;
        context.referenceX = 0;
        context.referenceY = 0;
        context.previousMeanX = 0;
        context.previousMeanY = 0;
    }

    /**
     * Handles the fact that the change of input cursors has no effect in the gesture.
     *
     * @param context Touch target context to be updated.
     */
    private void processNothingHappened(final TouchTargetContext context) {
        // Clear context
        context.userId = -1;
        context.activeTarget = null;
        context.previousState = DragEvent.State.UNARMED;
        context.previousCursorCount = 0;
        context.referenceX = 0;
        context.referenceY = 0;
        context.previousMeanX = 0;
        context.previousMeanY = 0;
    }
}
