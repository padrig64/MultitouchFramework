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

package com.github.multitouchframework.experimental.gesture.drag;

import com.github.multitouchframework.api.Chainable;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.base.gesture.drag.DragEvent;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

import java.util.ArrayList;
import java.util.List;

// TODO Finish implementation
public class DragInertia implements TouchListener<DragEvent>, Chainable<TouchListener<DragEvent>> {

    private class DeceleratorTarget implements TimingTarget {

        @Override
        public void begin(final Animator animator) {
            System.out.println("DragInertia$DeceleratorTarget.begin");
        }

        @Override
        public void end(final Animator animator) {
            System.out.println("DragInertia$DeceleratorTarget.end");
        }

        @Override
        public void repeat(final Animator animator) {
            System.out.println("DragInertia$DeceleratorTarget.repeat");
        }

        @Override
        public void reverse(final Animator animator) {
            System.out.println("DragInertia$DeceleratorTarget.reverse");
        }

        @Override
        public void timingEvent(final Animator animator, final double v) {
            System.out.println("DragInertia$DeceleratorTarget.timingEvent: " + v);
        }
    }

    private final DragEvent[] samples;

    private int sampleIndex = 0;

    private Animator animator = null;

    private final DeceleratorTarget deceleratorTarget = new DeceleratorTarget();

    /**
     * Listeners to events of the gesture.
     *
     * @see #queue(TouchListener)
     * @see #dequeue(TouchListener)
     * @see #fireGestureEvent(DragEvent)
     */
    private final List<TouchListener<DragEvent>> gestureListeners = new ArrayList<TouchListener<DragEvent>>();

    public DragInertia() {
        this(4);
    }

    public DragInertia(final int sampleCount) {
        samples = new DragEvent[sampleCount];
    }

    /**
     * @see Chainable#queue(Object)
     */
    @Override
    public void queue(final TouchListener<DragEvent> gestureListener) {
        gestureListeners.add(gestureListener);
    }

    /**
     * @see Chainable#dequeue(Object)
     */
    @Override
    public void dequeue(final TouchListener<DragEvent> gestureListener) {
        gestureListeners.remove(gestureListener);
    }

    /**
     * Fires the specified event to the registered gesture listeners.<br>This method is to be called by sub-classes to
     * notify gesture listeners.
     *
     * @param event Gesture event to be fired.
     */
    protected void fireGestureEvent(final DragEvent event) {
        for (final TouchListener<DragEvent> listener : gestureListeners) {
            listener.processTouchEvent(event);
        }
    }

    /**
     * @see TouchListener#processTouchEvent(com.github.multitouchframework.api.touch.TouchEvent)
     */
    @Override
    public void processTouchEvent(final DragEvent event) {
        switch (event.getState()) {
            case ARMED:
                processDragArmed(event);
                break;
            case PERFORMED:
                processDragPerformed(event);
                break;
            case UNARMED:
                processDragUnarmed(event);
                break;
        }
    }

    private void processDragArmed(final DragEvent event) {
        // TODO Stop current inertia if any, and continue current pane
        if (animator.isRunning()) {
            animator.cancel();
            animator.removeTarget(deceleratorTarget);
        }

        // Otherwise, just start the drag
        fireGestureEvent(event);
    }

    private void processDragPerformed(final DragEvent event) {
        samples[sampleIndex++] = new DragEvent(event);
        if (sampleIndex >= samples.length) {
            sampleIndex = 0;
        }
        fireGestureEvent(event);
    }

    private void processDragUnarmed(final DragEvent event) {
        animator = new Animator.Builder(new ScheduledExecutorTimingSource()).setInterpolator(new
                AccelerationInterpolator(1.0, 1.0)).addTarget(deceleratorTarget).build();

        animator.start();

        fireGestureEvent(event);
    }
}
