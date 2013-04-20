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

package com.github.multitouchframework.base.gesture.drag;

import com.github.multitouchframework.api.touch.TouchEvent;
import com.github.multitouchframework.api.touch.TouchTarget;

/**
 * Event fired when the drag/pan gesture is recognized.
 *
 * @see TouchEvent
 * @see DragRecognizer
 */
public class DragEvent implements TouchEvent {

    /**
     * Possible states of the gesture.
     */
    public static enum State {

        /**
         * The current number of cursors honors the minimum and maximum required by the gesture.<br>There will not be
         * two consecutive events of this state. The state of next event will be either PERFORMED or UNARMED.
         */
        ARMED,

        /**
         * The gesture has been performed.<br>There can be several consecutive events of this state. The state of the
         * next event will be either PERFORMED again or UNARMED.
         */
        PERFORMED,

        /**
         * The current number of cursors does not honor the minimum and maximum required by the gesture.<br>If the
         * previous event was ARMED or PERFORMED, it can be considered that the gesture has ended.<br>There will not
         * be two consecutive events of this state. The state of the next event will be ARMED.
         */
        UNARMED
    }

    /**
     * ID of the user performing the gesture.
     */
    private final long userId;

    /**
     * Touch target for which the event is produced.
     */
    private final TouchTarget target;

    /**
     * State of the recognized gesture.
     */
    private final State state;

    /**
     * Drag movement on the X axis relatively to the previous event.
     */
    private final int dx;

    /**
     * Drag movement on the Y axis relatively to the previous event.
     */
    private final int dy;

    /**
     * Drag movement on the X axis relatively to the very beginning of the gesture.
     */
    private final int dxTotal;

    /**
     * Drag movement on the Y axis relatively to the very beginning of the gesture.
     */
    private final int dyTotal;

    /**
     * Constructor specifying all the information on the gesture.
     *
     * @param userId  ID of the user performing the gesture.
     * @param target  Touch target for which the event is produced.
     * @param state   State of the recognized gesture.
     * @param dx      Drag movement on the X axis relatively to the previous event.
     * @param dy      Drag movement on the Y axis relatively to the previous event.
     * @param dxTotal Drag movement on the X axis relatively to the very beginning of the gesture.
     * @param dyTotal Drag movement on the Y axis relatively to the very beginning of the gesture.
     */
    public DragEvent(final long userId, final TouchTarget target, final State state, final int dx, final int dy,
                     final int dxTotal, final int dyTotal) {
        this.userId = userId;
        this.target = target;
        this.state = state;
        this.dx = dx;
        this.dy = dy;
        this.dxTotal = dxTotal;
        this.dyTotal = dyTotal;
    }

    /**
     * @see TouchEvent#getUserId()
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * @see TouchEvent#getTouchTarget()
     */
    @Override
    public TouchTarget getTouchTarget() {
        return target;
    }

    /**
     * Gets the state of the recognized gesture.
     *
     * @return Gesture state.
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the drag movement on the X axis relatively to the previous event.
     *
     * @return New movement on the X axis.
     */
    public int getDiffX() {
        return dx;
    }

    /**
     * Gets the drag movement on the Y axis relatively to the previous event.
     *
     * @return New movement on the Y axis.
     */
    public int getDiffY() {
        return dy;
    }

    /**
     * Gets the drag movement on the X axis relatively to the very beginning of the gesture.
     *
     * @return Total movement on the X axis.
     */
    public int getTotalDiffX() {
        return dxTotal;
    }

    /**
     * Gets the drag movement on the Y axis relatively to the very beginning of the gesture.
     *
     * @return Total movement on the Y axis.
     */
    public int getTotalDiffY() {
        return dyTotal;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "DRAG{state=" + state + "; dx=" + dx + "; dy=" + dy + "; dxTotal=" + dxTotal + "; dyTotal=" + dyTotal +
                "} on " + target;
    }
}
