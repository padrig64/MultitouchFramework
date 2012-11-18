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

public interface GestureEngineProfile {

	public InputSource getInputController();

	public void setInputController(InputSource inputController);

	public void addInputFilter(InputFilter inputFilter);

	public void removeInputFilter(InputFilter inputFilter);

	// TODO

	public <E extends GestureEvent> void addGestureRecognizer(GestureDefinition<E> gestureDefinition,
															  GestureRecognizer<E> gestureRecognizer);

	public <E extends GestureEvent> void removeGestureRecognizer(GestureRecognizer<E> gestureRecognizer);

	public <E extends GestureEvent> void addGestureListener(GestureDefinition<E> gestureDefinition, E gestureListener);

	public <E extends GestureEvent> void removeGestureListener(E gestureListener);

	public <E extends GestureEvent> void addGestureListener(GestureDefinition<E> gestureDefinition, E gestureListener,
															Region touchableObject);

	public <E extends GestureEvent> void removeGestureListener(E gestureListener, Region touchableObject);
}
