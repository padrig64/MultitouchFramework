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

package com.github.gestureengine.experimental;

import com.github.gestureengine.api.area.CursorToAreaDispatcher;
import com.github.gestureengine.api.area.TouchableArea;
import com.github.gestureengine.api.gesture.definition.GestureDefinition;
import com.github.gestureengine.api.gesture.listener.GestureListener;
import com.github.gestureengine.api.gesture.recognizer.GestureRecognizer;
import com.github.gestureengine.api.input.controller.InputController;
import com.github.gestureengine.api.input.filter.InputFilter;
import com.github.gestureengine.base.flow.CompositeCursorProcessorBlock;

public class DefaultGestureEngineProfile implements GestureEngineProfile {

	private InputController inputController = null;

	private final CompositeCursorProcessorBlock filterComposition = new CompositeCursorProcessorBlock();

	private CursorToAreaDispatcher toc = null;

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#getInputController()
	 */
	@Override
	public InputController getInputController() {
		return inputController;
	}

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#setInputController(InputController)
	 */
	@Override
	public void setInputController(final InputController inputController) {
		if (this.inputController != null) {
			this.inputController.dequeue(filterComposition);
		}

		this.inputController = inputController;

		if (this.inputController != null) {
			this.inputController.queue(filterComposition);
		}
	}

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#addInputFilter(InputFilter)
	 */
	@Override
	public void addInputFilter(final InputFilter inputFilter) {
		filterComposition.addSubBlock(inputFilter);
	}

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#removeInputFilter(InputFilter)
	 */
	@Override
	public void removeInputFilter(final InputFilter inputFilter) {
		filterComposition.removeSubBlock(inputFilter);
	}

	public CursorToAreaDispatcher getTouchableObjectController() {
		return toc;
	}

	public void setTouchableObjectController(final CursorToAreaDispatcher toc) {
		if (this.toc != null) {
			filterComposition.dequeue(this.toc);
		}

		this.toc = toc;

		if (toc != null) {
			filterComposition.queue(toc);
		}
	}

	@Override
	public <L extends GestureListener> void addGestureRecognizer(final GestureDefinition<L> gestureDefinition,
																 final GestureRecognizer<L> gestureRecognizer) {
		toc.queue(gestureRecognizer);
	}

	@Override
	public <L extends GestureListener> void removeGestureRecognizer(final GestureRecognizer<L> gestureRecognizer) {
		toc.dequeue(gestureRecognizer);
	}

	@Override
	public <L extends GestureListener> void addGestureListener(final GestureDefinition<L> gestureDefinition,
															   final L gestureListener) {
	}

	@Override
	public <L extends GestureListener> void removeGestureListener(final L gestureListener) {
	}

	@Override
	public <L extends GestureListener> void addGestureListener(final GestureDefinition<L> gestureDefinition,
															   final L gestureListener,
															   final TouchableArea touchableObject) {
	}

	@Override
	public <L extends GestureListener> void removeGestureListener(final L gestureListener,
																  final TouchableArea touchableObject) {
	}
}
