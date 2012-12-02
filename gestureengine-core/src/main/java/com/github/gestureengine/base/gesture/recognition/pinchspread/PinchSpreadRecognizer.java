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

package com.github.gestureengine.base.gesture.recognition.pinchspread;

import com.github.gestureengine.api.input.Cursor;
import com.github.gestureengine.api.region.Region;
import com.github.gestureengine.base.gesture.recognition.AbstractGestureRecognizer;
import com.github.gestureengine.base.gesture.recognition.drag.DragEvent;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Entity responsible for recognizing a pinch/spread/zoom/etc. gesture.<br>The recognition is made on a per-region basis
 * and is based on the mean distance of all the cursors to the mean cursor (average of all the cursors).<br>Note that
 * this recognizer works best after filtering the input and limiting the number of input touch events.
 */
public class PinchSpreadRecognizer extends AbstractGestureRecognizer<PinchSpreadEvent> {

	/**
	 * Context storing the state of recognition of the gesture for a single region.
	 */
	private class RegionContext {

		/**
		 * Strong reference to the region when the gesture is not unarmed to prevent garbage collection.<br>This makes sure
		 * that we will get the complete set of events.
		 */
		public Region activeRegion = null;

		/**
		 * Last state of the gesture for the region.
		 */
		public DragEvent.State previousState = DragEvent.State.UNARMED;

		/**
		 * Last number of cursors on the region.
		 */
		public int previousCursorCount = 0;

		public double referenceDistance = 1.0;

		public double previousMeanDistance = 1.0;
	}

	/**
	 * Minimum number of cursors required to perform the gesture.
	 */
	private int minCursorCount = 2;

	/**
	 * Maximum number of cursors required to perform the gesture.
	 */
	private int maxCursorCount = Integer.MAX_VALUE;

	/**
	 * Saved recognition context for each region.
	 */
	private final Map<Region, RegionContext> regionContexts = new WeakHashMap<Region, RegionContext>();

	/**
	 * Default constructor.<br>By default, 2 cursors is the minimum required to perform the gesture, and there is no
	 * maximum.
	 */
	public PinchSpreadRecognizer() {
		// Nothing to be done
	}

	/**
	 * Constructor specifying the minimum and maximum numbers of cursors required to perform the gesture.
	 *
	 * @param minCursorCount Minimum number of cursors required to perform the gesture.
	 * @param maxCursorCount Maximum number of cursors required to perform the gesture.
	 */
	public PinchSpreadRecognizer(final int minCursorCount, final int maxCursorCount) {
		setMinCursorCount(minCursorCount);
		setMaxCursorCount(maxCursorCount);
	}

	/**
	 * Gets the minimum number of cursors required to perform the gesture.
	 *
	 * @return Minimum cursor count.
	 */
	public int getMinCursorCount() {
		return minCursorCount;
	}

	/**
	 * Sets the minimum number of cursors required to perform the gesture.
	 *
	 * @param count Minimum cursor count.
	 */
	public void setMinCursorCount(final int count) {
		minCursorCount = count;
	}

	/**
	 * Gets the maximum number of cursors required to perform the gesture.
	 *
	 * @return Maximum cursor count.
	 */
	public int getMaxCursorCount() {
		return maxCursorCount;
	}

	/**
	 * Sets the maximum number of cursors required to perform the gesture.
	 *
	 * @param count Maximum cursor count.
	 */
	public void setMaxCursorCount(final int count) {
		maxCursorCount = count;
	}

	/**
	 * @see AbstractGestureRecognizer#process(Region, Collection)
	 */
	@Override
	public void process(final Region region, final Collection<Cursor> cursors) {
		final int cursorCount = cursors.size();
		final RegionContext context = getContext(region);

		// Test this first because it is the most likely to happen
		if (isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
			if (context.previousCursorCount == cursorCount) {
				processPinchOrSpreadPerformed(context, cursors);
			} else {
				processValidCursorCountChanged(context, cursors);
			}
		} else if (!isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
			processPinchOrSpreadArmed(region, context, cursors);
		} else if (isCursorCountValid(context.previousCursorCount) && !isCursorCountValid(cursorCount)) {
			processPinchOrSpreadUnarmed(context);
		} else {
			processNothingHappened(context);
		}
	}

	/**
	 * Gets a context for the specified region.
	 *
	 * @param region Region to get or create a context for.
	 *
	 * @return Context for the region.
	 */
	private RegionContext getContext(final Region region) {
		RegionContext context = regionContexts.get(region);
		if (context == null) {
			context = new RegionContext();
			regionContexts.put(region, context);
		}
		return context;
	}

	/**
	 * States whether the number of input cursors matches the minimum and maximum required by the gesture.
	 *
	 * @param cursorCount Input cursor count.
	 *
	 * @return True if the minimum and maximum are honored, false otherwise.
	 */
	private boolean isCursorCountValid(final int cursorCount) {
		return (minCursorCount <= cursorCount) && (cursorCount <= maxCursorCount);
	}

	private void processPinchOrSpreadArmed(final Region region, final RegionContext context,
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
			meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) +
					(meanY - cursor.getY()) * (meanY - cursor.getY()));
		}
		meanDistance /= cursorCount;

		// Save context
		context.activeRegion = region; // Prevent garbage collection
		context.previousState = DragEvent.State.ARMED;
		context.previousCursorCount = cursorCount;
		context.referenceDistance = meanDistance;
		context.previousMeanDistance = meanDistance;
	}

	/**
	 * Handles the fact that the change of input cursors corresponds to a drag movement.
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
			meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) +
					(meanY - cursor.getY()) * (meanY - cursor.getY()));
		}
		meanDistance /= cursorCount;

		// Trigger listeners
		final PinchSpreadEvent event = new PinchSpreadEvent(PinchSpreadEvent.State.PERFORMED, context.activeRegion,
				meanDistance / context.previousMeanDistance, meanDistance / context.referenceDistance);
		fireGestureEvent(event);

		// Save context (no change of reference point or active region)
		context.previousState = DragEvent.State.PERFORMED;
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
			meanDistance += Math.sqrt((meanX - cursor.getX()) * (meanX - cursor.getX()) +
					(meanY - cursor.getY()) * (meanY - cursor.getY()));
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
		context.previousState = DragEvent.State.UNARMED;
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
		context.previousState = DragEvent.State.UNARMED;
		context.previousCursorCount = 0;
		context.referenceDistance = 1.0;
		context.previousMeanDistance = 1.0;
	}
}
