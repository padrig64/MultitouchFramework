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

package com.github.gestureframework.base;

import com.github.gestureframework.api.GestureManager;
import com.github.gestureframework.api.area.Touchable;
import com.github.gestureframework.api.area.TouchableAreaController;
import com.github.gestureframework.api.flow.CompositeTouchPointProcessorBlock;
import com.github.gestureframework.api.gesture.definition.GestureDefinition;
import com.github.gestureframework.api.gesture.listener.GestureListener;
import com.github.gestureframework.api.gesture.recognizer.GestureRecognizer;
import com.github.gestureframework.api.input.controller.InputController;
import com.github.gestureframework.api.input.filter.InputFilter;

public class DefaultGestureManager implements GestureManager {

	private InputController inputController = null;

	private final CompositeTouchPointProcessorBlock filterComposition = new CompositeTouchPointProcessorBlock();

	private TouchableAreaController toc = null;

	/**
	 * @see GestureManager#getInputController()
	 */
	@Override
	public InputController getInputController() {
		return inputController;
	}

	/**
	 * @see GestureManager#setInputController(InputController)
	 */
	@Override
	public void setInputController(final InputController inputController) {
		if (this.inputController != null) {
			this.inputController.removeNextBlock(filterComposition);
		}

		this.inputController = inputController;

		if (this.inputController != null) {
			this.inputController.addNextBlock(filterComposition);
		}
	}

	/**
	 * @see GestureManager#addInputFilter(InputFilter)
	 */
	@Override
	public void addInputFilter(final InputFilter inputFilter) {
		filterComposition.addSubBlock(inputFilter);
	}

	/**
	 * @see GestureManager#removeInputFilter(InputFilter)
	 */
	@Override
	public void removeInputFilter(final InputFilter inputFilter) {
		filterComposition.removeSubBlock(inputFilter);
	}

	public TouchableAreaController getTouchableObjectController() {
		return toc;
	}

	public void setTouchableObjectController(TouchableAreaController toc) {
		if (this.toc != null) {
			filterComposition.removeNextBlock(this.toc);
		}

		this.toc = toc;

		if (toc != null) {
			filterComposition.addNextBlock(toc);
		}
	}

	@Override
	public <L extends GestureListener> void addGestureRecognizer(GestureDefinition<L> gestureDefinition,
																 GestureRecognizer<L> gestureRecognizer) {
		toc.addNextBlock(gestureRecognizer);
	}

	@Override
	public <L extends GestureListener> void removeGestureRecognizer(GestureRecognizer<L> gestureRecognizer) {
		toc.removeNextBlock(gestureRecognizer);
	}

	@Override
	public <L extends GestureListener> void addGestureListener(GestureDefinition<L> gestureDefinition,
															   L gestureListener) {
	}

	@Override
	public <L extends GestureListener> void removeGestureListener(L gestureListener) {
	}

	@Override
	public <L extends GestureListener> void addGestureListener(GestureDefinition<L> gestureDefinition,
															   L gestureListener, Touchable touchableObject) {
	}

	@Override
	public <L extends GestureListener> void removeGestureListener(L gestureListener, Touchable touchableObject) {
	}
}
