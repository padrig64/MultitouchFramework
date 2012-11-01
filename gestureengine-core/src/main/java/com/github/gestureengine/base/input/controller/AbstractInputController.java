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

package com.github.gestureengine.base.input.controller;

import com.github.gestureengine.api.flow.CursorProcessor;
import com.github.gestureengine.api.input.controller.InputController;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of an input controller.<br>Sub-classes are meant to make use of the connected cursor
 * processor to process the touch input, by calling their {@link com.github.gestureengine.api.flow.CursorProcessor#process(java.util.Collection)}
 * method.
 *
 * @see InputController
 */
public abstract class AbstractInputController implements InputController {

	/**
	 * Flag indicating whether the input controller is started or not.
	 *
	 * @see #start()
	 * @see #stop()
	 */
	protected boolean started = false;

	/**
	 * Cursor processors connected and processing the output cursors from this input controller.
	 */
	protected final List<CursorProcessor> nextBlocks = new ArrayList<CursorProcessor>();

	/**
	 * Connects the specified cursor processor to this input controller block.<br>Cursor processor can be, for instance,
	 * input filters or touch area controllers.
	 *
	 * @param cursorProcessor Cursor processor to be connected.
	 */
	@Override
	public void queue(final CursorProcessor cursorProcessor) {
		nextBlocks.add(cursorProcessor);
	}

	/**
	 * Disconnects the specified cursor processor from this input controller block.<br>Cursor processor can be, for
	 * instance, input filters or touch area controllers.
	 *
	 * @param cursorProcessor Cursor processor to be disconnected.
	 */
	@Override
	public void dequeue(final CursorProcessor cursorProcessor) {
		nextBlocks.remove(cursorProcessor);
	}

	/**
	 * @see InputController#isStarted()
	 */
	@Override
	public boolean isStarted() {
		return started;
	}

	/**
	 * @see InputController#start()
	 */
	@Override
	public void start() {
		if (!started) {
			started = true;
		}
	}

	/**
	 * @see InputController#stop()
	 */
	@Override
	public void stop() {
		started = false;
	}
}
