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

import com.github.gestureengine.api.gesture.recognition.GestureEvent;
import com.github.gestureengine.api.gesture.recognition.GestureRecognizer;
import com.github.gestureengine.api.input.filter.InputFilter;
import com.github.gestureengine.api.input.source.InputSource;
import com.github.gestureengine.api.region.Region;
import com.github.gestureengine.api.region.dispatch.CursorToRegionDispatcher;

public class DefaultGestureEngineProfile implements GestureEngineProfile {

	private InputSource inputController = null;

	private final CompositeCursorProcessorBlock filterComposition = new CompositeCursorProcessorBlock();

	private CursorToRegionDispatcher toc = null;

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#getInputController()
	 */
	@Override
	public InputSource getInputController() {
		return inputController;
	}

	/**
	 * @see com.github.gestureengine.experimental.GestureEngineProfile#setInputController(com.github.gestureengine.api.input.source.InputSource)
	 */
	@Override
	public void setInputController(final InputSource inputController) {
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
//		filterComposition.addSubBlock(inputFilter);
	}

	/**
	 * @see GestureEngineProfile#removeInputFilter(InputFilter)
	 */
	@Override
	public void removeInputFilter(final InputFilter inputFilter) {
//		filterComposition.removeSubBlock(inputFilter);
	}

	public CursorToRegionDispatcher getTouchableObjectController() {
		return toc;
	}

	public void setTouchableObjectController(final CursorToRegionDispatcher toc) {
		if (this.toc != null) {
			filterComposition.dequeue(this.toc);
		}

		this.toc = toc;

		if (toc != null) {
			filterComposition.queue(toc);
		}
	}

	@Override
	public <E extends GestureEvent> void addGestureRecognizer(final GestureDefinition<E> gestureDefinition,
															  final GestureRecognizer<E> gestureRecognizer) {
		toc.queue(gestureRecognizer);
	}

	@Override
	public <E extends GestureEvent> void removeGestureRecognizer(final GestureRecognizer<E> gestureRecognizer) {
		toc.dequeue(gestureRecognizer);
	}

	@Override
	public <E extends GestureEvent> void addGestureListener(final GestureDefinition<E> gestureDefinition,
															final E gestureListener) {
	}

	@Override
	public <E extends GestureEvent> void removeGestureListener(final E gestureListener) {
	}

	@Override
	public <E extends GestureEvent> void addGestureListener(final GestureDefinition<E> gestureDefinition,
															final E gestureListener, final Region touchableObject) {
	}

	@Override
	public <E extends GestureEvent> void removeGestureListener(final E gestureListener, final Region touchableObject) {
	}
}
