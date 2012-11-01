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

package com.github.gestureengine.base.area;

import com.github.gestureengine.api.area.TouchableArea;
import com.github.gestureengine.api.flow.Bounds;
import com.github.gestureengine.api.flow.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;

public class CursorToScreenDispatcher extends AbstractCursorToAreaDispatcher {

	private static class TouchableScreen implements TouchableArea {

		private final Bounds screenBounds;

		public TouchableScreen() {
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			screenBounds = new Bounds("Screen", 0, 0, screenSize.width, screenSize.height);
		}

		@Override
		public Bounds getTouchableBounds() {
			return screenBounds;
		}
	}

	private final Collection<TouchableArea> touchableAreas;

	public CursorToScreenDispatcher() {
		touchableAreas = new ArrayList<TouchableArea>();
		touchableAreas.add(new TouchableScreen());
	}

	@Override
	public void process(final Collection<Cursor> cursors) {
		forwardToNextBlocks(cursors, touchableAreas);
	}
}
