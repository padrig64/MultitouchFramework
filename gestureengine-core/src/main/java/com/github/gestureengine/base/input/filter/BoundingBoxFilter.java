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

import com.github.gestureengine.api.flow.TouchPoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxFilter extends AbstractInputFilter {

	private static final int MAX_DIFF = 10;

	private Map<Long, TouchPoint> filteredTouchPoints = new HashMap<Long, TouchPoint>();

	@Override
	public void process(final Collection<TouchPoint> touchPoints) {
		// Quick way to remove the points that are no longer there
		final Map<Long, TouchPoint> oldFilteredPoints = filteredTouchPoints;
		filteredTouchPoints = new HashMap<Long, TouchPoint>();

		for (final TouchPoint rawTouchPoint : touchPoints) {
			final TouchPoint oldFilteredTouchPoint = oldFilteredPoints.get(rawTouchPoint.getId());
			if (oldFilteredTouchPoint == null) {
				// Touch point was not yet filtered, so just added it now to the list
				filteredTouchPoints.put(rawTouchPoint.getId(), rawTouchPoint);
			} else {
				// Touch point was already filtered
				final TouchPoint newFilteredTouchPoint = filterTouchPoint(rawTouchPoint, oldFilteredTouchPoint);
				filteredTouchPoints.put(rawTouchPoint.getId(), newFilteredTouchPoint);
			}
		}

		forwardToNextBlocks(filteredTouchPoints.values());
	}

	private TouchPoint filterTouchPoint(final TouchPoint rawTouchPoint, final TouchPoint oldFilteredTouchPoint) {
		final int filteredX;
		final int filteredY;

		// Filter on the X axis
		if (rawTouchPoint.getX() < (oldFilteredTouchPoint.getX() - MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredX = rawTouchPoint.getX() + MAX_DIFF;
		} else if (rawTouchPoint.getX() > (oldFilteredTouchPoint.getX() + MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredX = rawTouchPoint.getX() - MAX_DIFF;
		} else {
			// Just reuse the old position
			filteredX = oldFilteredTouchPoint.getX();
		}

		// Filter on the Y axis
		if (rawTouchPoint.getY() < (oldFilteredTouchPoint.getY() - MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredY = rawTouchPoint.getY() + MAX_DIFF;
		} else if (rawTouchPoint.getY() > (oldFilteredTouchPoint.getY() + MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredY = rawTouchPoint.getY() - MAX_DIFF;
		} else {
			// Just reuse the old position
			filteredY = oldFilteredTouchPoint.getY();
		}

		return new TouchPoint(rawTouchPoint.getId(), filteredX, filteredY);
	}
}
