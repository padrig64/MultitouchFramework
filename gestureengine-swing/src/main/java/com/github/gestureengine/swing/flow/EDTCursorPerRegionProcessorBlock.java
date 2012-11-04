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

package com.github.gestureengine.swing.flow;

import com.github.gestureengine.api.flow.Cursor;
import com.github.gestureengine.api.flow.CursorPerRegionProcessor;
import com.github.gestureengine.api.flow.CursorPerRegionProcessorBlock;
import com.github.gestureengine.api.region.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Cursor processor block forwarding the cursors on the Event Dispatch Thread.
 */
public class EDTCursorPerRegionProcessorBlock implements CursorPerRegionProcessorBlock<CursorPerRegionProcessor> {

	/**
	 * Blocks that are queued to this block.
	 */
	private final List<CursorPerRegionProcessor> nextBlocks =
			Collections.synchronizedList(new ArrayList<CursorPerRegionProcessor>());

	/**
	 * Default constructor.
	 */
	public EDTCursorPerRegionProcessorBlock() {
		// Nothing to be done
	}

	/**
	 * Constructor specifying the first black to be queued to this block.
	 *
	 * @param firstNextBlock First block to be queued.
	 *
	 * @see #queue(CursorPerRegionProcessor)
	 */
	public EDTCursorPerRegionProcessorBlock(final CursorPerRegionProcessor firstNextBlock) {
		nextBlocks.add(firstNextBlock);
	}

	/**
	 * @see com.github.gestureengine.api.flow.CursorProcessorBlock#queue(Object)
	 */
	@Override
	public void queue(final CursorPerRegionProcessor nextBlock) {
		nextBlocks.add(nextBlock);
	}

	/**
	 * @see com.github.gestureengine.api.flow.CursorProcessorBlock#dequeue(Object)
	 */
	@Override
	public void dequeue(final CursorPerRegionProcessor nextBlock) {
		nextBlocks.remove(nextBlock);
	}

	/**
	 * Forwards the specified cursors to the next blocks on the EDT.
	 *
	 * @see com.github.gestureengine.api.flow.CursorProcessorBlock#process(java.util.Collection)
	 */
	@Override
	public void process(final Region region, final Collection<Cursor> cursors) {
		// Just put the cursors in a new list, no need to clone them
		final Collection<Cursor> copiedData = new ArrayList<Cursor>(cursors);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				synchronized (nextBlocks) {
					for (final CursorPerRegionProcessor nextBlock : nextBlocks) {
						nextBlock.process(region, copiedData);
					}
				}
			}
		});
	}
}
