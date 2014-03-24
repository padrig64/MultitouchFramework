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

package com.github.multitouchframework.experimental.dispatch;

import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.cursor.Cursor;
import com.github.multitouchframework.base.processing.filter.AbstractCursorToTouchTargetDispatcher;
import com.github.multitouchframework.base.target.ScreenTouchTarget;

import java.util.ArrayList;
import java.util.List;

// TODO
public class SimpleCursorToTouchTargetDispatcher extends AbstractCursorToTouchTargetDispatcher {

    public static final TouchTarget SCREEN_TOUCH_TARGET = new ScreenTouchTarget();

    private final List<TouchTarget> targets = new ArrayList<TouchTarget>();

    public List<TouchTarget> getTouchTargets() {
        return targets;
    }

    public void addTouchTargetOnTop(TouchTarget target) {
        targets.add(target);
    }

    public void insertTouchTargetAt(int i, TouchTarget target) {
        targets.add(i, target);
    }

    public void setTouchTargetAt(int i, TouchTarget target) {
        targets.set(i, target);
    }

    public void insertTouchTargetAbove(TouchTarget lowerTarget, TouchTarget target) {
        int i = targets.lastIndexOf(lowerTarget);
        if (i < 0) {
            // Add touch target on top of everything
            targets.add(target);
        } else {
            targets.add(i + 1, target);
        }
    }

    public void removeTouchTarget(TouchTarget target) {
        targets.remove(target);
    }

    /**
     * @see AbstractCursorToTouchTargetDispatcher#findTouchedTarget(Cursor)
     */
    @Override
    protected TouchTarget findTouchedTarget(Cursor cursor) {
        TouchTarget foundTarget = SCREEN_TOUCH_TARGET;

        for (int i = targets.size() - 1; i >= 0; i--) {
            TouchTarget target = targets.get(i);
            if (target.isTouched(cursor)) {
                foundTarget = target;
                break;
            }
        }

        return foundTarget;
    }
}
