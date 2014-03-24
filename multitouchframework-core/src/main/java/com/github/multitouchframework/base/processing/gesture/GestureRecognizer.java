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

package com.github.multitouchframework.base.processing.gesture;

import com.github.multitouchframework.api.Chainable;
import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchListener;
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;

/**
 * Interface to be implemented by gesture recognizers.
 * <p/>
 * Gesture recognizers are typically queued to cursor-to-target dispatchers in order to recognize gestures on a specific
 * touch target of the touch surface.
 *
 * @param <E> Type of gesture events fired by the gesture recognizer.
 *
 * @see TouchListener
 * @see TouchEvent
 * @see CursorUpdateEvent
 * @see Chainable
 */
public interface GestureRecognizer<E extends TouchEvent> extends TouchListener<CursorUpdateEvent>,
        Chainable<TouchListener<E>> {
    // Nothing more to be done
}
