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

public class CompositeBlock<I, O> implements Block<I, O> {

	private final List<Block<I, O>> subBlocks = new ArrayList<Block<I, O>>();

	public void addSubBlock(final Block<I, O> subBlock) {
		// Disconnect next blocks from the last block from the list
		// TODO

		// Connect new sub-block to the last sub-block from the list
		if (!subBlocks.isEmpty()) {
			final Block<I, O> lastSubBlock = subBlocks.get(subBlocks.size() - 1);
			lastSubBlock.addNextBlock((Block<O, ?>) subBlock);
		}

		// Connect next blocks to the new sub-block
		// TODO

		// Add new sub-block to the list
		subBlocks.add(subBlock);
	}

	public void removeSubBlock(final Block<I, O> subBlock) {
		// Disconnect sub-block
		final int subBlockIndex = subBlocks.indexOf(subBlock);
		if (subBlockIndex > 0) {
			// Disconnect it from previous sub-block
			final int previousSubBlockIndex = subBlockIndex - 1;
			if (previousSubBlockIndex >= 0) {
				final Block<I, O> previousSubBlock = subBlocks.get(previousSubBlockIndex);
				previousSubBlock.removeNextBlock((Block<O, ?>) subBlock);
			}

			// Disconnect it from next sub-block
			final int nextSubBlockIndex = subBlockIndex + 1;
			if (nextSubBlockIndex < subBlocks.size()) {
				final Block<I, O> nextSubBlock = subBlocks.get(nextSubBlockIndex);
				subBlock.removeNextBlock((Block<O, ?>) nextSubBlock);
			}
		}

		// Remove sub-block from the list
		subBlocks.remove(subBlock);

	}

	/**
	 * @see Block#process(Object)
	 */
	@Override
	public void process(final I inputData) {
		if (!subBlocks.isEmpty()) {
			final Block<I, O> firstSubBlock = subBlocks.get(0);
			firstSubBlock.process(inputData);
		}
	}

	/**
	 * @see Block#addNextBlock(Block)
	 */
	@Override
	public void addNextBlock(final Block<O, ?> outputBlock) {
		// TODO
	}

	/**
	 * @see Block#removeNextBlock(Block)
	 */
	@Override
	public void removeNextBlock(final Block<O, ?> outputBlock) {
		// TODO
	}
}
