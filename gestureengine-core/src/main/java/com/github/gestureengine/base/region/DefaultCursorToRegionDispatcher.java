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
import com.github.gestureengine.api.region.TouchableRegion;
import java.util.ArrayList;
import java.util.List;

public class DefaultCursorToRegionDispatcher extends AbstractCursorToRegionDispatcher {

	private final List<TouchableRegion> regions = new ArrayList<TouchableRegion>();

	public List<TouchableRegion> getRegions() {
		return regions;
	}

	public void addRegionOnTop(final TouchableRegion region) {
		regions.add(region);
	}

	public void insertRegionAt(final int i, final TouchableRegion region) {
		regions.add(i, region);
	}

	public void setRegionAt(final int i, final TouchableRegion region) {
		regions.set(i, region);
	}

	public void insertRegionAbove(final TouchableRegion lowerRegion, final TouchableRegion region) {
		final int i = regions.lastIndexOf(lowerRegion);
		if (i < 0) {
			// Add region on top of everything
			regions.add(region);
		} else {
			regions.add(i + 1, region);
		}
	}

	public void removeRegion(final TouchableRegion region) {
		regions.remove(region);
	}

	/**
	 * @see AbstractCursorToRegionDispatcher#findTouchedRegion
	 */
	@Override
	protected TouchableRegion findTouchedRegion(final Cursor cursor) {
		TouchableRegion foundRegion = SCREEN_REGION;

		for (int i = regions.size() - 1; i >= 0; i--) {
			final TouchableRegion region = regions.get(i);
			if (region.isTouched(cursor)) {
				foundRegion = region;
				break;
			}
		}

		return foundRegion;
	}
}
