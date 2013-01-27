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

package com.github.multitouchframework.swing.flow;

import com.github.multitouchframework.api.flow.Chainable;
import com.github.multitouchframework.api.gesture.GestureEvent;
import com.github.multitouchframework.api.gesture.GestureListener;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EDTSchedulerGestureListener<E extends GestureEvent> implements GestureListener<E>,
        Chainable<GestureListener<E>> {

    /**
     * Listeners to events of the gesture.
     *
     * @see #queue(GestureListener)
     * @see #dequeue(GestureListener)
     * @see #processTouchEvent(GestureEvent)
     */
    private final List<GestureListener<E>> gestureListeners = Collections.synchronizedList(new
            ArrayList<GestureListener<E>>());

    @Override
    public void queue(final GestureListener<E> gestureListener) {
        gestureListeners.add(gestureListener);
    }

    @Override
    public void dequeue(final GestureListener<E> gestureListener) {
        gestureListeners.remove(gestureListener);
    }

    /**
     * Forwards the specified gesture event to the next blocks on the EDT.
     *
     * @see GestureListener#processTouchEvent(GestureEvent)
     */
    @Override
    public void processTouchEvent(final E event) {
        final Runnable edtRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (gestureListeners) {
                    for (final GestureListener<E> nextBlock : gestureListeners) {
                        nextBlock.processTouchEvent(event);
                    }
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            edtRunnable.run();
        } else {
            SwingUtilities.invokeLater(edtRunnable);
        }
    }
}
