/*
 * Copyright (c) 2013, Patrick Moawad
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

package com.github.multitouchframework.experimental.chain;

public class CompositeCursorProcessorBlock /*implements CursorProcessor, Chainable<CursorProcessor>*/ {

//    @Override
//    public void queue(final CursorProcessor nextBlock) {
//        // TODO
//    }
//
//    @Override
//    public void dequeue(final CursorProcessor nextBlock) {
//        // TODO
//    }
//
//    @Override
//    public void processTouchEvent(final TouchTarget target, final Collection<Cursor> cursors) {
//        // TODO
//    }
    //	/**
//	 * List of sub-blocks composing this composite block.<br>The first sub-block is the entry point when processing the
//	 * data. All sub-blocks will be chained to each other in the order of addition.
//	 *
//	 * @see #addSubBlock(com.github.multitouchframework.api.chain.CursorProcessorBlock)
//	 * @see #removeSubBlock(com.github.multitouchframework.api.chain.CursorProcessorBlock)
//	 * @see #process(Collection)
//	 */
//	private final List<CursorProcessorBlock<CursorProcessor>> subBlocks =
//			new ArrayList<CursorProcessorBlock<CursorProcessor>>();
//
//	/**
//	 * List of next connected blocks.<br>They will be connected to the last sub-block only.
//	 *
//	 * @see #queue(Object)
//	 * @see #dequeue(Object)
//	 */
//	private final List<CursorProcessor> nextBlocks = new ArrayList<CursorProcessor>();
//
//	/**
//	 * Adds the specified sub-block to the block composition.<br>It will be appended to the last added sub-block,
// if any.
//	 *
//	 * @param subBlock Block to be added as the last sub-block.
//	 */
//	public void addSubBlock(final CursorProcessorBlock<CursorProcessor> subBlock) {
//		if (!subBlocks.isEmpty()) {
//			final CursorProcessorBlock<CursorProcessor> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
//
//			// Disconnect next blocks from the previously last block from the list
//			for (final CursorProcessor nextBlock : nextBlocks) {
//				lastSubBlock.dequeue(nextBlock);
//			}
//
//			// Connect new sub-block to the previously last sub-block from the list
//			lastSubBlock.queue(subBlock);
//		}
//
//		// Connect next blocks to the new (last) sub-block
//		for (final CursorProcessor nextBlock : nextBlocks) {
//			subBlock.dequeue(nextBlock);
//		}
//
//		// Add new sub-block to the list
//		subBlocks.add(subBlock);
//	}
//
//	public void removeSubBlock(final CursorProcessorBlock<CursorProcessor> subBlock) {
//
//		// Disconnect sub-block from everything, and reconnect the rest
//		final int subBlockIndex = subBlocks.indexOf(subBlock);
//		if (subBlockIndex > 0) {
//			// Sub-block was not the first
//
//			final int previousSubBlockIndex = subBlockIndex - 1;
//			if (previousSubBlockIndex >= 0) {
//				// Disconnect it from previous sub-block
//				final CursorProcessorBlock<CursorProcessor> previousSubBlock = subBlocks.get(previousSubBlockIndex);
//				previousSubBlock.dequeue(subBlock);
//
//				// Reconnect next sub-block to previous sub-block
//				final int nextSubBlockIndex = subBlockIndex + 1;
//				if (nextSubBlockIndex < subBlocks.size()) {
//					final CursorProcessorBlock<CursorProcessor> nextSubBlock = subBlocks.get(nextSubBlockIndex);
//					previousSubBlock.queue(nextSubBlock);
//				}
//			}
//
////			// Disconnect it from next sub-block
////			final int nextSubBlockIndex = subBlockIndex + 1;
////			if (nextSubBlockIndex < subBlocks.size()) {
////				final DataProcessorBlock<D, N> nextSubBlock = subBlocks.get(nextSubBlockIndex);
////				subBlock.dequeue((DataProcessorBlock<N, ?>) nextSubBlock);
////			}
////
////			// TODO Reconnect next sub-block to previous sub-block
////
////			// TODO Disconnect next blocks from remove sub-block
////
////			// TODO Reconnect next blocks to last sub-block
//		}
//
//		// Remove sub-block from the list
//		subBlocks.remove(subBlock);
//	}
//
//	/**
//	 * @see com.github.multitouchframework.api.chain.CursorProcessorBlock#process(Collection)
//	 */
//	@Override
//	public void process(final Collection<Cursor> cursors) {
//		if (!subBlocks.isEmpty()) {
//			final CursorProcessor firstSubBlock = subBlocks.get(0);
//			firstSubBlock.process(cursors);
//		}
//	}
//
//	/**
//	 * @see com.github.multitouchframework.api.chain.CursorProcessorBlock#queue(Object)
//	 */
//	@Override
//	public void queue(final CursorProcessor nextBlock) {
//		// Connect next block to last sub-block
//		if (!subBlocks.isEmpty()) {
//			final CursorProcessorBlock<CursorProcessor> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
//			lastSubBlock.queue(nextBlock);
//		}
//
//		// Add next block to the list
//		nextBlocks.add(nextBlock);
//	}
//
//	/**
//	 * @see com.github.multitouchframework.api.chain.CursorProcessorBlock#dequeue(Object)
//	 */
//	@Override
//	public void dequeue(final CursorProcessor nextBlock) {
//		// Disconnect next block from last sub-block
//		if (!subBlocks.isEmpty()) {
//			final CursorProcessorBlock<CursorProcessor> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
//			lastSubBlock.dequeue(nextBlock);
//		}
//
//		// Remove next block from the list
//		nextBlocks.remove(nextBlock);
//	}
}
