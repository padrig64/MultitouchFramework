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

import com.github.multitouchframework.api.chain.Chainable;

public final class ChainBuilder {

    public static class NextIsChainable<T> {

        private final Chainable<T> block;

        public NextIsChainable(final Chainable<T> block) {
            this.block = block;
        }

        @SuppressWarnings("unchecked")
        public <N> NextIsChainable queue(final Chainable<N> nextBlock) {
            this.block.queue((T) nextBlock);
            return new NextIsChainable<N>(nextBlock);
        }

        @SuppressWarnings("unchecked")
        public void queue(final Object... nextBlocks) {
            // TODO Rename endWith() to queue() because it is optional?
            // TODO If so, rename startWith() as well
            for (final Object next : nextBlocks) {
                this.block.queue((T) next);
            }
        }
    }

    /**
     * Private constructor for utility class.
     */
    private ChainBuilder() {
        // Nothing to be done
    }

    public static <N> NextIsChainable queue(final Chainable<N> block) {
        return new NextIsChainable<N>(block);
    }
}
