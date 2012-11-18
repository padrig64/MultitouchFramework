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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DragRecognizer extends AbstractGestureRecognizer<DragEvent> {

	private class Context {

		public DragEvent.State previousState = DragEvent.State.UNARMED;

		public int previousCursorCount = 0;

		public int referenceMeanX = -1;
		public int referenceMeanY = -1;

		public int previousMeanX = -1;
		public int previousMeanY = -1;

		public int previousTotalOffsetX = 0;
		public int previousTotalOffsetY = 0;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DragRecognizer.class);

	private int minCursorCount = 1;

	private int maxCursorCount = -1;

	private final Map<Region, Context> regionContexts = new HashMap<Region, Context>();

	public DragRecognizer() {
		// Nothing to be done
	}

	public DragRecognizer(final int minCursorCount, final int maxCursorCount) {
		setMinCursorCount(minCursorCount);
		setMaxCursorCount(maxCursorCount);
	}

	public int getMinCursorCount() {
		return minCursorCount;
	}

	public void setMinCursorCount(final int count) {
		minCursorCount = count;
	}

	public int getMaxCursorCount() {
		return maxCursorCount;
	}

	public void setMaxCursorCount(final int count) {
		maxCursorCount = count;
	}

	/**
	 * @see AbstractGestureRecognizer#process(com.github.gestureengine.api.region.Region, Collection)
	 */
	@Override
	public void process(final Region region, final Collection<Cursor> cursors) {
		final int cursorCount = cursors.size();
		final Context context = getContext(region);

		if (isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
			// Test this first because it is the most likely to happen
			processDragPerformed(context, cursors);
		} else if (!isCursorCountValid(context.previousCursorCount) && isCursorCountValid(cursorCount)) {
			processDragArmed(context, cursors);
		} else if (isCursorCountValid(context.previousCursorCount) && !isCursorCountValid(cursorCount)) {
			processDragUnarmed(context);
		} else {
			processNothingHappened(context);
		}
	}

	private Context getContext(final Region region) {
		Context context = regionContexts.get(region);
		if (context == null) {
			context = new Context();
			regionContexts.put(region, context);
		}
		return context;
	}

	private boolean isCursorCountValid(final int cursorCount) {
		return ((minCursorCount < 1) || (minCursorCount <= cursorCount)) &&
				((maxCursorCount < 1) || (cursorCount <= maxCursorCount));
	}

	private void processDragArmed(final Context context, final Collection<Cursor> cursors) {
		// Trigger listeners
		final DragEvent event = new DragEvent(DragEvent.State.ARMED, 0, 0, 0, 0);
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
		context.previousState = DragEvent.State.ARMED;
		context.previousCursorCount = cursorCount;
		context.previousMeanX = meanX;
		context.previousMeanY = meanY;
		context.previousTotalOffsetX = 0;
		context.previousTotalOffsetY = 0;
	}

	private void processDragPerformed(final Context context, final Collection<Cursor> cursors) {
		final int cursorCount = cursors.size();

		// Calculate mean point
		int meanX = 0;
		int meanY = 0;
		for (final Cursor cursor : cursors) {
			meanX += cursor.getX();
			meanY += cursor.getY();
		}
		meanX /= cursorCount;
		meanY /= cursorCount;

		if (DragEvent.State.ARMED.equals(context.previousState) && (context.previousCursorCount != cursorCount)) {
			// Still armed, just update context and no need to trigger listeners
			context.previousState = DragEvent.State.ARMED;
			context.previousCursorCount = cursorCount;
			context.referenceMeanX = meanX;
			context.referenceMeanY = meanY;
			context.previousMeanX = meanX;
			context.previousMeanY = meanY;
			context.previousTotalOffsetX = 0;
			context.previousTotalOffsetY = 0;
		} else {
			// TODO Track cursor count changes and recalculate reference point

			// Determine change
			final DragEvent.State state = DragEvent.State.PERFORMED;
			final int offsetX = meanX - context.previousMeanX;
			final int offsetY = meanY - context.previousMeanY;
			final int totalOffsetX = 0; // TODO
			final int totalOffsetY = 0; // TODO

			// Trigger listeners
			final DragEvent event = new DragEvent(state, offsetX, offsetY, totalOffsetX, totalOffsetY);
			fireGestureEvent(event);

			// Save context
			context.previousState = DragEvent.State.PERFORMED;
			context.previousCursorCount = cursorCount;
			context.previousMeanX = meanX;
			context.previousMeanY = meanY;
			context.previousTotalOffsetX = totalOffsetX;
			context.previousTotalOffsetY = totalOffsetY;
		}
	}

	private void processDragUnarmed(final Context context) {
		// Trigger listeners
		final DragEvent event = new DragEvent(DragEvent.State.UNARMED, 0, 0, context.previousTotalOffsetX,
				context.previousTotalOffsetY);
		fireGestureEvent(event);

		// Clear context
		context.previousState = DragEvent.State.UNARMED;
		context.previousCursorCount = 0;
		context.referenceMeanX = 0;
		context.referenceMeanY = 0;
		context.previousMeanX = 0;
		context.previousMeanY = 0;
		context.previousTotalOffsetX = 0;
		context.previousTotalOffsetY = 0;
	}

	private void processNothingHappened(final Context context) {
		// Clear context
		context.previousState = DragEvent.State.UNARMED;
		context.previousCursorCount = 0;
		context.referenceMeanX = 0;
		context.referenceMeanY = 0;
		context.previousMeanX = 0;
		context.previousMeanY = 0;
		context.previousTotalOffsetX = 0;
		context.previousTotalOffsetY = 0;
	}
}
