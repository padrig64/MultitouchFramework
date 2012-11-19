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

package com.github.gestureengine.base.gesture.recognition.drag;

import com.github.gestureengine.api.input.Cursor;
import com.github.gestureengine.api.region.Region;
import com.github.gestureengine.base.gesture.recognition.AbstractGestureRecognizer;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Entity responsible for recognizing a drag/pan/etc. gesture.<br>The recognition is made on a per-region basis and is
 * based on the location of the mean cursor (average of all the cursors).<br>Note that this recognizer works best after
 * filtering the input and limiting the number of input touch events.
 */
public class DragRecognizer extends AbstractGestureRecognizer<DragEvent> {

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
		 * Last state of the drag gesture for the region.
		 */
		public DragEvent.State previousState = DragEvent.State.UNARMED;

		/**
		 * Last number of cursors on the region.
		 */
		public int previousCursorCount = 0;

		/**
		 * Last X coordinate of the reference point used to calculate the total movement of the drag gesture on the region.
		 */
		public int referenceX = -1;

		/**
		 * Last Y coordinate of the reference point used to calculate the total movement of the drag gesture on the region.
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
	 * Minimum number of cursors required to perform the gesture.
	 */
	private int minCursorCount = 1;

	/**
	 * Maximum number of cursors required to perform the gesture.
	 */
	private int maxCursorCount = Integer.MAX_VALUE;

	/**
	 * Saved recognition context for each region.
	 */
	private final Map<Region, RegionContext> regionContexts = new WeakHashMap<Region, RegionContext>();

	/**
	 * Default constructor.<br>By default, 1 cursor is the minimum required to perform the gesture, and there is no
	 * maximum.
	 */
	public DragRecognizer() {
		// Nothing to be done
	}

	/**
	 * Constructor specifying the minimum and maximum numbers of cursors required to perform the gesture.
	 *
	 * @param minCursorCount Minimum number of cursors required to perform the gesture.
	 * @param maxCursorCount Maximum number of cursors required to perform the gesture.
	 */
	public DragRecognizer(final int minCursorCount, final int maxCursorCount) {
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
				processDragPerformed(context, cursors);
			} else {
				processValidCursorCountChanged(context, cursors);
			}
		} else if (!isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
			processDragArmed(region, context, cursors);
		} else if (isCursorCountValid(context.previousCursorCount) && !isCursorCountValid(cursorCount)) {
			processDragUnarmed(context);
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

	private void processDragArmed(final Region region, final RegionContext context, final Collection<Cursor> cursors) {
		// Trigger listeners
		final DragEvent event = new DragEvent(DragEvent.State.ARMED, region, 0, 0, 0, 0);
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
		context.activeRegion = region; // Prevent garbage collection
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
	 * @param context Region context to be used and updated.
	 * @param cursors New input cursors.
	 */
	private void processDragPerformed(final RegionContext context, final Collection<Cursor> cursors) {
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
		final DragEvent.State state = DragEvent.State.PERFORMED;
		final int offsetX = meanX - context.previousMeanX;
		final int offsetY = meanY - context.previousMeanY;

		// Trigger listeners
		final DragEvent event = new DragEvent(state, context.activeRegion, offsetX, offsetY, meanX - context.referenceX,
				meanY - context.referenceY);
		fireGestureEvent(event);

		// Save context (no change of reference point or active region)
		context.previousState = DragEvent.State.PERFORMED;
		context.previousCursorCount = cursorCount;
		context.previousMeanX = meanX;
		context.previousMeanY = meanY;
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

		// No need to trigger any listener

		// Calculate new reference point to have the same total difference
		final int newReferenceX = meanX - context.previousMeanX + context.referenceX;
		final int newReferenceY = meanY - context.previousMeanY + context.referenceY;

		// Save context (no change of state or active region)
		context.previousCursorCount = cursorCount;
		context.referenceX = newReferenceX;
		context.referenceY = newReferenceY;
		context.previousMeanX = meanX;
		context.previousMeanY = meanY;
	}

	/**
	 * Handles the fact that the change of input cursors unarmed the gesture.
	 *
	 * @param context Region context to be updated.
	 */
	private void processDragUnarmed(final RegionContext context) {
		// Trigger listeners
		final DragEvent event = new DragEvent(DragEvent.State.UNARMED, context.activeRegion, 0, 0,
				context.previousMeanX - context.referenceX, context.previousMeanY - context.referenceY);
		fireGestureEvent(event);

		// Clear context
		context.activeRegion = null; // Allow garbage collection
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
	 * @param context Region context to be updated.
	 */
	private void processNothingHappened(final RegionContext context) {
		// Clear context
		context.activeRegion = null;
		context.previousState = DragEvent.State.UNARMED;
		context.previousCursorCount = 0;
		context.referenceX = 0;
		context.referenceY = 0;
		context.previousMeanX = 0;
		context.previousMeanY = 0;
	}
}
