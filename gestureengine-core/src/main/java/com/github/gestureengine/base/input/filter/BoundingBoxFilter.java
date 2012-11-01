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

import com.github.gestureengine.api.flow.Cursor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoundingBoxFilter extends AbstractInputFilter {

	private static final int MAX_DIFF = 10;

	private Map<Long, Cursor> filteredCursors = new HashMap<Long, Cursor>();

	@Override
	public void process(final Collection<Cursor> cursors) {
		// Quick way to remove the cursors that are no longer there
		final Map<Long, Cursor> oldFilteredCursors = filteredCursors;
		filteredCursors = new HashMap<Long, Cursor>();

		for (final Cursor rawCursor : cursors) {
			final Cursor oldFilteredCursor = oldFilteredCursors.get(rawCursor.getId());
			if (oldFilteredCursor == null) {
				// Cursor was not yet filtered, so just added it now to the list
				filteredCursors.put(rawCursor.getId(), rawCursor);
			} else {
				// Cursor was already filtered
				final Cursor newFilteredCursor = filterCursor(rawCursor, oldFilteredCursor);
				filteredCursors.put(rawCursor.getId(), newFilteredCursor);
			}
		}

		forwardToNextBlocks(filteredCursors.values());
	}

	private Cursor filterCursor(final Cursor rawCursor, final Cursor oldFilteredCursor) {
		final int filteredX;
		final int filteredY;

		// Filter on the X axis
		if (rawCursor.getX() < (oldFilteredCursor.getX() - MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredX = rawCursor.getX() + MAX_DIFF;
		} else if (rawCursor.getX() > (oldFilteredCursor.getX() + MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredX = rawCursor.getX() - MAX_DIFF;
		} else {
			// Just reuse the old position
			filteredX = oldFilteredCursor.getX();
		}

		// Filter on the Y axis
		if (rawCursor.getY() < (oldFilteredCursor.getY() - MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredY = rawCursor.getY() + MAX_DIFF;
		} else if (rawCursor.getY() > (oldFilteredCursor.getY() + MAX_DIFF)) {
			// New position is out of the box, so move the box
			filteredY = rawCursor.getY() - MAX_DIFF;
		} else {
			// Just reuse the old position
			filteredY = oldFilteredCursor.getY();
		}

		return new Cursor(rawCursor.getId(), filteredX, filteredY);
	}
}
