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

public class DragEvent implements GestureEvent {

	public enum State {
		ARMED,
		PERFORMED,
		UNARMED
	}

	private final State state;

	private final int dx;

	private final int dy;

	private final int dxTotal;

	private final int dyTotal;

	public DragEvent(final State state, final int dx, final int dy, final int dxTotal, final int dyTotal) {
		this.state = state;
		this.dx = dx;
		this.dy = dy;
		this.dxTotal = dxTotal;
		this.dyTotal = dyTotal;
	}

	public State getState() {
		return state;
	}

	public int getDiffX() {
		return dx;
	}

	public int getDiffY() {
		return dy;
	}

	public int getTotalDiffX() {
		return dxTotal;
	}

	public int getTotalDiffY() {
		return dyTotal;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "{state=" + state + "; dx=" + dx + "; dy=" + dy + "; dxTotal=" + dxTotal + "; dyTotal=" + dyTotal + "}";
	}
}
