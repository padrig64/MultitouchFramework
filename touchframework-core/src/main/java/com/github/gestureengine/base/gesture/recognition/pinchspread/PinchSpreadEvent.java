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

package com.github.gestureengine.base.gesture.recognition.pinchspread;

import com.github.gestureengine.api.gesture.recognition.GestureEvent;
import com.github.gestureengine.api.region.Region;

/**
 * Event fired in recognition of a pinch/spread/zoom gesture.
 */
public class PinchSpreadEvent implements GestureEvent {

    /**
     * Possible states of the gesture.
     */
    public enum State {

        /**
         * The current number of cursors honors the minimum and maximum required by the gesture.<br>There will not be two
         * consecutive events of this state. The state of next event will be either PERFORMED or UNARMED.
         */
        ARMED,

        /**
         * The gesture has been performed.<br>There can be several consecutive events of this state. The state of the next
         * event will be either PERFORMED again or UNARMED.
         */
        PERFORMED,

        /**
         * The current number of cursors does not honor the minimum and maximum required by the gesture.<br>If the previous
         * event was ARMED or PERFORMED, it can be considered that the gesture has ended.<br>There will not be two consecutive
         * events of this state. The state of the next event will be ARMED.
         */
        UNARMED
    }

    /**
     * State of the recognized gesture.
     */
    private final State state;

    /**
     * Region to which the gesture applies.
     */
    private final Region region;

    /**
     * Scale movement axis relatively to the previous event.
     */
    private final double ds;

    /**
     * Scale movement relatively to the very beginning of the gesture.
     */
    private final double dsTotal;

    /**
     * Constructor specifying all the information on the gesture.
     *
     * @param state   State of the recognized gesture.
     * @param region  Region to which the gesture applies
     * @param ds      Scale movement relatively to the previous event.
     * @param dsTotal Scale movement relatively to the very beginning of the gesture.
     */
    public PinchSpreadEvent(final State state, final Region region, final double ds, final double dsTotal) {
        this.state = state;
        this.region = region;
        this.ds = ds;
        this.dsTotal = dsTotal;
    }

    /**
     * Gets the state of the recognized gesture.
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the region to which the gesture applies.
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Gets the scale movement relatively to the previous event.
     *
     * @return New movement.
     */
    public double getDiffScale() {
        return ds;
    }

    /**
     * Gets the scale movement axis relatively to the very beginning of the gesture.
     *
     * @return Total movement.
     */
    public double getTotalDiffScale() {
        return dsTotal;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "PINCHSPREAD{state=" + state + "; ds=" + ds + "; dsTotal=" + dsTotal + "} on " + region;
    }
}
