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

package com.github.gestureengine.base.region;

import com.github.gestureengine.api.flow.Cursor;
import com.github.gestureengine.api.region.Region;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCursorToRegionDispatcher extends AbstractCursorToRegionDispatcher {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCursorToRegionDispatcher.class);

	/**
	 * Default region that would hold the cursors if no other region on top of it would hold those cursors.
	 */
	private static final Region SCREEN_REGION = new ScreenRegion(); // Whole screen

	private Map<Long, Region> oldCursorToRegion = new HashMap<Long, Region>(); // Initially, no cursor down

	@Override
	public void process(final Collection<Cursor> cursors) {
		final Map<Long, Region> newCursorToRegion = new HashMap<Long, Region>();
		final Map<Region, Collection<Cursor>> updatesToBeForwarded = new HashMap<Region, Collection<Cursor>>();

		for (final Cursor cursor : cursors) {
			// Find the region holding the cursor
			Region region = oldCursorToRegion.get(cursor.getId());
			if (region == null) {
				// Find a new candidate region to hold the cursor
				region = findRegionForCursor(cursor);
				if (region == null) {
					LOGGER.info("No region found for cursor: " + cursor);
				} else {
					newCursorToRegion.put(cursor.getId(), region);

				}
			} else {
				// Region already holds the cursor, so update cursor in this region
				oldCursorToRegion.remove(cursor.getId());
				newCursorToRegion.put(cursor.getId(), region);

				Collection<Cursor> cursorsForThisRegion = updatesToBeForwarded.get(region);
				if (cursorsForThisRegion == null) {
					cursorsForThisRegion = new HashSet<Cursor>();
					updatesToBeForwarded.put(region, cursorsForThisRegion);
				}
				cursorsForThisRegion.add(cursor);
			}
		}

		// Clean up old mapping for notify regions that have no more cursor
		for (final Region oldRegion : oldCursorToRegion.values()) {
			updatesToBeForwarded.put(oldRegion, Collections.<Cursor>emptySet());
		}

		// Forward updated regions and cursors to next blocks
		for (final Map.Entry<Region, Collection<Cursor>> entry : updatesToBeForwarded.entrySet()) {
			forwardToNextBlocks(entry.getKey(), entry.getValue());
		}

		// Save mapping for next time
		oldCursorToRegion = newCursorToRegion;
	}

	private Region findRegionForCursor(final Cursor cursor) {
		// TODO
		return SCREEN_REGION;
	}

//	private void forwardToNextBlocks() {
//		final Set<Region> toBeRemoved = new HashSet<Region>();
//
//		for (final Map.Entry<Region, Collection<Cursor>> entry : regionToCursors.entrySet()) {
//			forwardToNextBlocks(entry.getValue(), Collections.singleton(entry.getKey()));
//			if (entry.getValue().isEmpty()) {
//				toBeRemoved.add(entry.getKey());
//			}
//		}
//
//		for (final Region region : toBeRemoved) {
//			regionToCursors.remove(region);
//		}
//	}
}
