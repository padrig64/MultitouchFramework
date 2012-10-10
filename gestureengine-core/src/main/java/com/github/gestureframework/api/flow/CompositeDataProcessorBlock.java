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

package com.github.gestureframework.api.flow;

import java.util.ArrayList;
import java.util.List;

public class CompositeDataProcessorBlock<D> implements DataProcessorBlock<D, DataProcessor<D>> {

	private final List<DataProcessorBlock<D, DataProcessor<D>>> subBlocks =
			new ArrayList<DataProcessorBlock<D, DataProcessor<D>>>();

	private final List<DataProcessor<D>> nextBlocks = new ArrayList<DataProcessor<D>>();

	/**
	 * Adds the specified sub-block to the block composition.<br>It will be appended to the last added sub-block, if any.
	 *
	 * @param subBlock Block to be added as the last sub-block.
	 */
	public void addSubBlock(final DataProcessorBlock<D, DataProcessor<D>> subBlock) {
		if (!subBlocks.isEmpty()) {
			final DataProcessorBlock<D, DataProcessor<D>> lastSubBlock = subBlocks.get(subBlocks.size() - 1);

			// Disconnect next blocks from the last block from the list
			for (final DataProcessor<D> nextBlock : nextBlocks) {
				lastSubBlock.removeNextBlock(nextBlock);
			}

			// Connect new sub-block to the last sub-block from the list
			lastSubBlock.addNextBlock(subBlock);
		}

		// Connect next blocks to the new sub-block
		for (final DataProcessor<D> nextBlock : nextBlocks) {
			subBlock.removeNextBlock(nextBlock);
		}

		// Add new sub-block to the list
		subBlocks.add(subBlock);
	}

	public void removeSubBlock(final DataProcessorBlock<D, DataProcessor<D>> subBlock) {
//		// Disconnect sub-block
//		final int subBlockIndex = subBlocks.indexOf(subBlock);
//		if (subBlockIndex > 0) {
//			// Disconnect it from previous sub-block
//			final int previousSubBlockIndex = subBlockIndex - 1;
//			if (previousSubBlockIndex >= 0) {
//				final DataProcessorBlock<D, N> previousSubBlock = subBlocks.get(previousSubBlockIndex);
//				previousSubBlock.removeNextBlock((DataProcessorBlock<N, ?>) subBlock);
//			}
//
//			// Disconnect it from next sub-block
//			final int nextSubBlockIndex = subBlockIndex + 1;
//			if (nextSubBlockIndex < subBlocks.size()) {
//				final DataProcessorBlock<D, N> nextSubBlock = subBlocks.get(nextSubBlockIndex);
//				subBlock.removeNextBlock((DataProcessorBlock<N, ?>) nextSubBlock);
//			}
//
//			// TODO Reconnect next sub-block to previous sub-block
//
//			// TODO Disconnect next blocks from remove sub-block
//
//			// TODO Reconnect next blocks to last sub-block
//		}
//
//		// Remove sub-block from the list
//		subBlocks.remove(subBlock);
	}

	/**
	 * @see DataProcessorBlock#process(Object)
	 */
	@Override
	public void process(final D data) {
		if (!subBlocks.isEmpty()) {
			final DataProcessorBlock<D, DataProcessor<D>> firstSubBlock = subBlocks.get(0);
			firstSubBlock.process(data);
		}
	}

	/**
	 * @see DataProcessorBlock#addNextBlock(Object)
	 */
	@Override
	public void addNextBlock(final DataProcessor<D> nextBlock) {
		// Connect next block to last sub-block
		if (!subBlocks.isEmpty()) {
			final DataProcessorBlock<D, DataProcessor<D>> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
			lastSubBlock.addNextBlock(nextBlock);
		}

		// Add next block to the list
		nextBlocks.add(nextBlock);
	}

	/**
	 * @see DataProcessorBlock#removeNextBlock(Object)
	 */
	@Override
	public void removeNextBlock(final DataProcessor<D> nextBlock) {
		// Disconnect next block from last sub-block
		if (!subBlocks.isEmpty()) {
			final DataProcessorBlock<D, DataProcessor<D>> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
			lastSubBlock.removeNextBlock(nextBlock);
		}

		// Remove next block from the list
		nextBlocks.remove(nextBlock);
	}
}
