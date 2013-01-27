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

package com.github.multitouchframework.base.gesture.tap;

import com.github.multitouchframework.api.Region;
import com.github.multitouchframework.api.touch.TouchEvent;

/**
 * Event fired when the tap gesture is recognized.
 *
 * @see TouchEvent
 * @see TapRecognizer
 */
public class TapEvent implements TouchEvent {

    /**
     * Possible states of the gesture.
     */
    public enum State {

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
     * Region to which the gesture applies.
     */
    private final Region region;

    /**
     * State of the recognized gesture.
     */
    private final State state;

    /**
     * Number of consecutive taps that have been performed, including this one.
     */
    private final int tapCount;

    /**
     * Number of cursors involved for this tap.
     */
    private final int cursorCount;

    /**
     * Constructor specifying all the information on the gesture.
     *
     * @param userId      ID of the user performing the gesture.
     * @param region      Region to which the gesture applies.
     * @param state       State of the recognized gesture.
     * @param tapCount    Number of consecutive taps that have been performed, including this one.
     * @param cursorCount Number of cursors involved for this tap.
     */
    public TapEvent(final long userId, final Region region, final State state, final int tapCount,
                    final int cursorCount) {
        this.userId = userId;
        this.region = region;
        this.state = state;
        this.tapCount = tapCount;
        this.cursorCount = cursorCount;
    }

    /**
     * @see TouchEvent#getUserId()
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @see TouchEvent#getRegion()
     */
    public Region getRegion() {
        return region;
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
     * Gets the number of taps that have been performed, including this one.
     *
     * @return Tap count.
     */
    public double getTapCount() {
        return tapCount;
    }

    /**
     * Gets the number of cursors involved for this tap.
     *
     * @return Total movement.
     */
    public double getCursorCount() {
        return cursorCount;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "TAP{state=" + state + "; taps=" + tapCount + "; cursors=" + cursorCount + "} on " + region;
    }
}
