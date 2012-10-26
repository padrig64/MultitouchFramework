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

package com.github.gestureengine.base.input.filter;

import com.github.gestureengine.api.input.controller.TouchPoint;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoundingBoxFilter extends AbstractInputFilter {

	private static final int MAX_DIFF = 5;

	private final List<TouchPoint> filteredTouchPoints = new ArrayList<TouchPoint>();

	@Override
	public void process(final Collection<TouchPoint> data) {
//		final int cursorCount = cursorPoints.size();
//		if (filteredTouchPoints.size() == cursorCount) {
//			// Filter cursor points
//			for (int i = 0; i < cursorCount; i++) {
//				final Point filteredCursor = filteredTouchPoints.get(i);
//				final Point newCursor = cursorPoints.get(i);
//
//				filterPoint(newCursor, filteredCursor);
//			}
//
//			// Filter mean point
//			if (meanPoint == null) {
//				// No mean point to be filtered
//				filteredMeanPoint = null;
//			} else if (filteredMeanPoint == null) {
//				// Mean point cannot be filtered yet, so just copy it
//				filteredMeanPoint = new Point(meanPoint);
//			} else {
//				// Filter
//				filterPoint(meanPoint, filteredMeanPoint);
//			}
//		} else {
//			// Not the same number of fingers as before so just duplicate the list
//			filteredTouchPoints.clear();
//			for (final Point cursor : cursorPoints) {
//				filteredTouchPoints.add(new Point(cursor));
//			}
//
//			if (meanPoint == null) {
//				// No mean point at all
//				filteredMeanPoint = null;
//			} else {
//				filteredMeanPoint = new Point(meanPoint);
//			}
//		}
	}

	private void filterPoint(final Point newCursor, final Point filteredCursor) {
		// Move box on X axis if needed
		if (newCursor.x < (filteredCursor.x - MAX_DIFF)) {
			filteredCursor.x = newCursor.x + MAX_DIFF;
		} else if (newCursor.x > (filteredCursor.x + MAX_DIFF)) {
			filteredCursor.x = newCursor.x - MAX_DIFF;
		}

		// Move box on Y axis if needed
		if (newCursor.y < (filteredCursor.y - MAX_DIFF)) {
			filteredCursor.y = newCursor.y + MAX_DIFF;
		} else if (newCursor.y > (filteredCursor.y + MAX_DIFF)) {
			filteredCursor.y = newCursor.y - MAX_DIFF;
		}

		// Update new cursor with filtered cursor
		newCursor.x = filteredCursor.x;
		newCursor.y = filteredCursor.y;
	}
}
