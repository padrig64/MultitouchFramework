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

package com.github.multitouchframework.api;

import com.github.multitouchframework.base.cursor.Cursor;

/**
 * Interface to be implemented by touchable areas on the touch-enabled surface.
 * <p/>
 * This may represent a figure of any shape, a GUI component, etc.. Note that a touch target may not be rectangle.
 */
public interface TouchTarget {

    /**
     * Gets the real base object represented by this touch target.
     * <p/>
     * This may be, for instance, a JComponent, the touch target itself, etc..
     *
     * @return Base object represented by this touch target.
     */
    Object getBaseObject();

    /**
     * Gets the maximum width of the touch target.
     *
     * @return Maximum width of the touch target.
     */
    int getMaximumWidth();

    /**
     * Gets the maximum height of the touch target.
     *
     * @return Maximum height of the touch target.
     */
    int getMaximumHeight();

    /**
     * States whether the touch target is touched by the specified cursor.
     *
     * @param cursor Cursor to be checked.
     *
     * @return True if the touch target is touched by the cursor, false otherwise.
     */
    boolean isTouched(Cursor cursor);
}
