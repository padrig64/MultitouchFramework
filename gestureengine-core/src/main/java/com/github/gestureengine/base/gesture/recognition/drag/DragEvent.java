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

package com.github.gestureengine.base.gesture.recognition.drag;

import com.github.gestureengine.api.gesture.recognition.GestureEvent;
import com.github.gestureengine.api.region.Region;

/**
 * Event fired in recognition of a drag/pan gesture.
 */
public class DragEvent implements GestureEvent {

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
	 * @param state State of the recognized gesture.
	 * @param region Region to which the gesture applies
	 * @param dx Drag movement on the X axis relatively to the previous event.
	 * @param dy Drag movement on the Y axis relatively to the previous event.
	 * @param dxTotal Drag movement on the X axis relatively to the very beginning of the gesture.
	 * @param dyTotal Drag movement on the Y axis relatively to the very beginning of the gesture.
	 */
	public DragEvent(final State state, final Region region, final int dx, final int dy, final int dxTotal,
					 final int dyTotal) {
		this.state = state;
		this.region = region;
		this.dx = dx;
		this.dy = dy;
		this.dxTotal = dxTotal;
		this.dyTotal = dyTotal;
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
				"} on " + region;
	}
}
