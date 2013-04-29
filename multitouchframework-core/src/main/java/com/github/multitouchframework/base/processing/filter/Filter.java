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

package com.github.multitouchframework.base.processing.filter;

import com.github.multitouchframework.api.Chainable;
import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchListener;

/**
 * Interface to be implemented by input filters.<br>Input filters are cursor processors that filter the input cursors
 * from {@link com.github.multitouchframework.base.processing.source.InputSource}s. They provide the filtered cursors to
 * one or several other cursor processors, typically other {@link Filter}s or
 * {@link com.github.multitouchframework.base.processing.dispatch.CursorToTouchTargetDispatcher}s.<br>Input filters
 * can be
 * used, for instance to reduced the number of events, to alter cursor positions, to re-schedule the processing to
 * another thread, to generate more events for inertia, etc.
 *
 * @see TouchListener
 * @see Chainable
 */
public interface Filter<E extends TouchEvent> extends TouchListener<E>, Chainable<TouchListener<E>> {
    // Nothing more to be done
}