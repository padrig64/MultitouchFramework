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
import com.github.gestureengine.api.flow.CursorRegionProcessor;
import com.github.gestureengine.api.region.CursorToRegionDispatcher;
import com.github.gestureengine.api.region.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCursorToRegionDispatcher implements CursorToRegionDispatcher {

	private final List<CursorRegionProcessor> nextBlocks = new ArrayList<CursorRegionProcessor>();

	@Override
	public void queue(final CursorRegionProcessor cursorRegionProcessor) {
		nextBlocks.add(cursorRegionProcessor);
	}

	@Override
	public void dequeue(final CursorRegionProcessor cursorRegionProcessor) {
		nextBlocks.remove(cursorRegionProcessor);
	}

	protected void forwardToNextBlocks(final Collection<Cursor> cursors, final Collection<Region> regions) {
		for (final CursorRegionProcessor nextBlock : nextBlocks) {
			nextBlock.process(cursors, regions);
		}
	}
}
