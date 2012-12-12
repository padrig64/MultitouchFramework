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

package com.github.gestureengine.base.region;

import com.github.gestureengine.api.input.Cursor;
import com.github.gestureengine.api.region.Region;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Touchable region of the touch surface representing the whole screen.<br>Note that this is meaningful only in cases
 * where the display matches the whole touch surface. In case of a non-clone dual screen (for instance, extension of the
 * desktop), the behavior will not be as expected.
 *
 * @see Toolkit#getScreenSize()
 */
public class ScreenRegion implements Region {

    /**
     * Screen size.<br>Note that
     */
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * @see Region#isTouched(Cursor)
     */
    @Override
    public boolean isTouched(final Cursor cursor) {
        return ((0 <= cursor.getX()) && (cursor.getX() < screenSize.width) && (0 <= cursor.getY()) &&
                (cursor.getY() < screenSize.height));
    }
}
