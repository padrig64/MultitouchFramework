/*
 * Copyright (c) 2013, Patrick Moawad
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

package com.github.multitouchframework.experimental.profile;

import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;
import com.github.multitouchframework.base.processing.filter.Filter;
import com.github.multitouchframework.base.processing.gesture.GestureRecognizer;
import com.github.multitouchframework.base.processing.source.InputSource;

public interface MultitouchFrameworkProfile {

    public InputSource getInputController();

    public void setInputController(InputSource inputController);

    public void addInputFilter(Filter<CursorUpdateEvent> inputFilter);

    public void removeInputFilter(Filter<CursorUpdateEvent> inputFilter);

    // TODO

    public <E extends TouchEvent> void addGestureRecognizer(GestureDefinition<E> gestureDefinition,
                                                            GestureRecognizer<E> gestureRecognizer);

    public <E extends TouchEvent> void removeGestureRecognizer(GestureRecognizer<E> gestureRecognizer);

    public <E extends TouchEvent> void addGestureListener(GestureDefinition<E> gestureDefinition, E gestureListener);

    public <E extends TouchEvent> void removeGestureListener(E gestureListener);

    public <E extends TouchEvent> void addGestureListener(GestureDefinition<E> gestureDefinition, E gestureListener,
                                                          TouchTarget touchableObject);

    public <E extends TouchEvent> void removeGestureListener(E gestureListener, TouchTarget touchableObject);
}
