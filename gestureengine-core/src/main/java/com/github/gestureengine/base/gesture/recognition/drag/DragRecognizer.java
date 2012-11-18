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

public class DragRecognizer extends AbstractGestureRecognizer<DragEvent> {

	private class Context {

		public DragEvent.State previousState = null;

		public int previousCursorCount = 0;

		public int previousMeanX = -1;

		public int previousMeanY = -1;

		public int previousTotalOffsetX = 0;

		public int previousTotalOffsetY = 0;
	}

	private int minCursorCount;

	private int maxCursorCount;

	private final Map<Region, Context> regionContexts = new HashMap<Region, Context>();

	public DragRecognizer() {
		this(1, -1);
	}

	public DragRecognizer(final int minCursorCount, final int maxCursorCount) {
		this.minCursorCount = minCursorCount;
		this.maxCursorCount = maxCursorCount;
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

		if (isValid(context.previousCursorCount) && isValid(cursorCount)) {
			// Drag performed

			// TODO Check if it was already armed and only the number of fingers changed
			// Determine change
			final DragEvent.State state = DragEvent.State.PERFORMED;
			final int offsetX = 0; // TODO
			final int offsetY = 0; // TODO
			final int totalOffsetX = 0; // TODO
			final int totalOffsetY = 0; // TODO

			// Trigger listeners
			final DragEvent event = new DragEvent(state, offsetX, offsetY, totalOffsetX, totalOffsetY);
			fireGestureEvent(event);

			// Save context
			context.previousState = DragEvent.State.ARMED;
			context.previousMeanX = 0; // TODO
			context.previousMeanY = 0; // TODO
			context.previousTotalOffsetX = 0;
			context.previousTotalOffsetY = 0;
		} else if (!isValid(context.previousCursorCount) && isValid(cursorCount)) {
			// Drag armed

			// Trigger listeners
			final DragEvent event = new DragEvent(DragEvent.State.ARMED, 0, 0, 0, 0);
			fireGestureEvent(event);

			// Save context
			context.previousState = DragEvent.State.ARMED;
			context.previousMeanX = 0; // TODO
			context.previousMeanY = 0; // TODO
			context.previousTotalOffsetX = 0;
			context.previousTotalOffsetY = 0;
		} else if (isValid(context.previousCursorCount) && !isValid(cursorCount)) {
			// Drag ended

			// Trigger listeners
			final DragEvent event = new DragEvent(DragEvent.State.UNARMED, 0, 0, context.previousTotalOffsetX,
					context.previousTotalOffsetY);
			fireGestureEvent(event);

			// Save context
			context.previousState = DragEvent.State.UNARMED;
			context.previousCursorCount = cursorCount;
			context.previousMeanX = 0;
			context.previousMeanY = 0;
		} else {
			// Nothing special happened

			// Save context
			context.previousState = null;
			context.previousCursorCount = cursorCount;
			context.previousMeanX = 0;
			context.previousMeanY = 0;
		}
//		int dx = 0;
//		int dy = 0;
//
//		// Check if number of fingers changed
//		final int cursorCount = cursorPoints.size();
//		if ((cursorCount == previousCursorCount) && (meanPoint != null) && (previousMeanPoint != null)) {
//			// Same number of finger as the previous call, so pan detected
//			dx = meanPoint.x - previousMeanPoint.x;
//			dy = meanPoint.y - previousMeanPoint.y;
//		}
//
//		// Fire start event if pan just started (if at least 1 finger)
//		if ((previousCursorCount == 0) && (cursorCount > 0)) {
//			for (final PanListener listener : gestureListeners) {
//				listener.panStarted();
//			}
//		}
//
//		// Fire pan event (if at least 1 finger)
//		for (final PanListener listener : gestureListeners) {
//			listener.panPerformed(dx, dy, meanPoint);
//		}
//
//		// Fire end event if pan just ended (if no more finger)
//		if ((previousCursorCount >= 0) && (cursorCount == 0)) {
//			for (final PanListener listener : gestureListeners) {
//				listener.panEnded();
//			}
//		}
//
//		// Save cursor count and mean point for next call
//		if (meanPoint == null) {
//			previousMeanPoint = null;
//		} else {
//			previousMeanPoint = new Point(meanPoint);
//		}
//		previousCursorCount = cursorCount;
	}

	private Context getContext(final Region region) {
		Context context = regionContexts.get(region);
		if (context == null) {
			context = new Context();
			regionContexts.put(region, context);
		}
		return context;
	}

	private boolean isValid(final int cursorCount) {
		return (minCursorCount <= cursorCount) && (cursorCount <= maxCursorCount);
	}
}
