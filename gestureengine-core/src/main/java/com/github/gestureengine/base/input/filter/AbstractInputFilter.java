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

package com.github.gestureengine.base.input.filter;

import java.util.ArrayList;
import java.util.List;

import com.github.gestureengine.api.flow.TouchPointProcessor;
import com.github.gestureengine.api.input.filter.InputFilter;

/**
 * Abstract implementation of an input filter.<br>Sub-classes are meant to make use of the connected touch point
 * processor to process the filtered touch input, by calling their {@link TouchPointProcessor#process(java.util.Collection)}
 * method.
 *
 * @see InputFilter
 */
public abstract class AbstractInputFilter implements InputFilter {

	/**
	 * Touch point processors connected and processing the output touch points from this input controller.
	 */
	private final List<TouchPointProcessor> nextBlocks = new ArrayList<TouchPointProcessor>();

	/**
	 * Connects the specified touch point processor to this input controller block.<br>Touch point processor can be, for
	 * instance, input filters or touch area controllers.
	 *
	 * @param touchPointProcessor Touch point processor to be connected.
	 */
	@Override
	public void connectNextBlock(TouchPointProcessor touchPointProcessor) {
		nextBlocks.add(touchPointProcessor);
	}

	/**
	 * Disconnects the specified touch point processor from this input controller block.<br>Touch point processor can be,
	 * for instance, input filters or touch area controllers.
	 *
	 * @param touchPointProcessor Touch point processor to be disconnected.
	 */
	@Override
	public void disconnectNextBlock(TouchPointProcessor touchPointProcessor) {
		nextBlocks.remove(touchPointProcessor);
	}
}
