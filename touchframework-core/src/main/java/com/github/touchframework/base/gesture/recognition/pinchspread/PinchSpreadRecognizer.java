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

package com.github.touchframework.base.gesture.recognition.pinchspread;

import com.github.touchframework.api.input.Cursor;
import com.github.touchframework.api.region.Region;
import com.github.touchframework.base.gesture.recognition.AbstractGestureRecognizer;

import java.util.Collection;

/**
 * Entity responsible for recognizing a pinch/spread/zoom/etc. gesture.<br>The recognition is made on a per-region basis
 * and is based on the mean distance of all the cursors to the mean cursor (average of all the cursors).<br>Note that
 * this recognizer works best after filtering the input and limiting the number of input touch events.
 *
 * @see AbstractGestureRecognizer
 * @see PinchSpreadEvent
 */
public class PinchSpreadRecognizer extends AbstractGestureRecognizer<PinchSpreadRecognizer.RegionContext,
        PinchSpreadEvent> {

    /**
     * Context storing the state of recognition of the gesture for a single region.
     */
    protected static class RegionContext {

        /**
         * Strong reference to the region when the gesture is not unarmed to prevent garbage collection.<br>This
         * makes sure
         * that we will get the complete set of events.
         */
        public Region activeRegion = null;

        /**
         * Last state of the gesture for the region.
         */
        public PinchSpreadEvent.State previousState = PinchSpreadEvent.State.UNARMED;

        /**
         * Last number of cursors on the region.
         */
        public int previousCursorCount = 0;

        public double referenceDistance = 1.0;

        public double previousMeanDistance = 1.0;
    }

    /**
     * Default constructor.<br>By default, 2 cursors is the minimum required to perform the gesture, and there is no
     * maximum.
     */
    public PinchSpreadRecognizer() {
        this(2, Integer.MAX_VALUE);
    }

    /**
     * @see AbstractGestureRecognizer#AbstractGestureRecognizer(int, int)
     */
    public PinchSpreadRecognizer(final int minCursorCount, final int maxCursorCount) {
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
     * @see AbstractGestureRecognizer#process(Object, Region, Collection)
     */
    @Override
    protected void process(final RegionContext context, final Region region, final Collection<Cursor> cursors) {
        final int cursorCount = cursors.size();

        // Test this first because it is the most likely to happen
        if (isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
            if (context.previousCursorCount == cursorCount) {
                processPinchOrSpreadPerformed(context, cursors);
            } else {
                processValidCursorCountChanged(context, cursors);
            }
        } else if (!isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
            processPinchOrSpreadArmed(context, region, cursors);
        } else if (isCursorCountValid(context.previousCursorCount) && !isCursorCountValid(cursorCount)) {
            processPinchOrSpreadUnarmed(context);
        } else {
            processNothingHappened(context);
        }
    }

    /**
     * Handles the fact that the change of input cursors armed the gesture.
     *
     * @param context Region context to be updated.
     * @param region  Touchable region to which the cursors are associated.
     * @param cursors New input cursors.
     */
    private void processPinchOrSpreadArmed(final RegionContext context, final Region region,
                                           final Collection<Cursor> cursors) {
        // Trigger listeners
        final PinchSpreadEvent event = new PinchSpreadEvent(PinchSpreadEvent.State.ARMED, region, 1.0, 1.0);
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

        // Calculate mean distance to mean point
        double meanDistance = 0.0;
        for (final Cursor cursor : cursors) {
            meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) + (meanY - cursor.getY()) *
                    (meanY - cursor.getY()));
        }
        meanDistance /= cursorCount;

        // Save context
        context.activeRegion = region; // Prevent garbage collection
        context.previousState = PinchSpreadEvent.State.ARMED;
        context.previousCursorCount = cursorCount;
        context.referenceDistance = meanDistance;
        context.previousMeanDistance = meanDistance;
    }

    /**
     * Handles the fact that the change of input cursors corresponds to a pinch or spread.
     *
     * @param context Region context to be used and updated.
     * @param cursors New input cursors.
     */
    private void processPinchOrSpreadPerformed(final RegionContext context, final Collection<Cursor> cursors) {
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

        // Calculate mean distance to mean point
        double meanDistance = 0.0;
        for (final Cursor cursor : cursors) {
            meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) + (meanY - cursor.getY()) *
                    (meanY - cursor.getY()));
        }
        meanDistance /= cursorCount;

        // Trigger listeners
        final PinchSpreadEvent event = new PinchSpreadEvent(PinchSpreadEvent.State.PERFORMED, context.activeRegion,
                meanDistance / context.previousMeanDistance, meanDistance / context.referenceDistance);
        fireGestureEvent(event);

        // Save context (no change of reference point or active region)
        context.previousState = PinchSpreadEvent.State.PERFORMED;
        context.previousCursorCount = cursorCount;
        context.previousMeanDistance = meanDistance;
    }

    /**
     * Handles the fact that the number of cursors changed.<br>Note that it is expected here that the validity of the
     * cursor count has already been checked before.
     *
     * @param context Region context to be used and updated.
     * @param cursors New input cursors.
     */
    private void processValidCursorCountChanged(final RegionContext context, final Collection<Cursor> cursors) {
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

        // Calculate mean distance to mean point
        double meanDistance = 0.0;
        for (final Cursor cursor : cursors) {
            meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) + (meanY - cursor.getY()) *
                    (meanY - cursor.getY()));
        }
        meanDistance /= cursorCount;

        // No need to trigger any listener

        // Calculate new reference point to have the same total difference
        final double newReferenceDistance = meanDistance * context.referenceDistance / context.previousMeanDistance;

        // Save context (no change of state or active region)
        context.previousCursorCount = cursorCount;
        context.referenceDistance = newReferenceDistance;
        context.previousMeanDistance = meanDistance;
    }

    /**
     * Handles the fact that the change of input cursors unarmed the gesture.
     *
     * @param context Region context to be updated.
     */
    private void processPinchOrSpreadUnarmed(final RegionContext context) {
        // Trigger listeners
        final PinchSpreadEvent event = new PinchSpreadEvent(PinchSpreadEvent.State.UNARMED, context.activeRegion, 0,
                context.previousMeanDistance / context.referenceDistance);
        fireGestureEvent(event);

        // Clear context
        context.activeRegion = null; // Allow garbage collection
        context.previousState = PinchSpreadEvent.State.UNARMED;
        context.previousCursorCount = 0;
        context.referenceDistance = 1.0;
        context.previousMeanDistance = 1.0;
    }

    /**
     * Handles the fact that the change of input cursors has no effect in the gesture.
     *
     * @param context Region context to be updated.
     */
    private void processNothingHappened(final RegionContext context) {
        // Clear context
        context.activeRegion = null;
        context.previousState = PinchSpreadEvent.State.UNARMED;
        context.previousCursorCount = 0;
        context.referenceDistance = 1.0;
        context.previousMeanDistance = 1.0;
    }
}
