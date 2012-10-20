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

package com.github.gestureengine.api;

import com.github.gestureengine.api.area.Touchable;
import com.github.gestureengine.api.gesture.definition.GestureDefinition;
import com.github.gestureengine.api.gesture.listener.GestureListener;
import com.github.gestureengine.api.gesture.recognizer.GestureRecognizer;
import com.github.gestureengine.api.input.controller.InputController;
import com.github.gestureengine.api.input.filter.InputFilter;

public interface GestureManager {

	public InputController getInputController();

	public void setInputController(InputController inputController);

	public void addInputFilter(InputFilter inputFilter);

	public void removeInputFilter(InputFilter inputFilter);

	// TODO

	public <L extends GestureListener> void addGestureRecognizer(GestureDefinition<L> gestureDefinition,
																 GestureRecognizer<L> gestureRecognizer);

	public <L extends GestureListener> void removeGestureRecognizer(GestureRecognizer<L> gestureRecognizer);

	public <L extends GestureListener> void addGestureListener(GestureDefinition<L> gestureDefinition,
															   L gestureListener);

	public <L extends GestureListener> void removeGestureListener(L gestureListener);

	public <L extends GestureListener> void addGestureListener(GestureDefinition<L> gestureDefinition,
															   L gestureListener, Touchable touchableObject);

	public <L extends GestureListener> void removeGestureListener(L gestureListener, Touchable touchableObject);
}
