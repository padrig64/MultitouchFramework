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

package com.github.multitouchframework.swing.dispatch;

import com.github.multitouchframework.api.touch.Cursor;
import com.github.multitouchframework.api.touch.TouchTarget;
import com.github.multitouchframework.base.dispatch.AbstractCursorToTouchTargetDispatcher;
import com.github.multitouchframework.swing.touch.ComponentTouchTarget;

import javax.swing.RootPaneContainer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

public class CursorToComponentDispatcher extends AbstractCursorToTouchTargetDispatcher {

    /**
     * @see AbstractCursorToTouchTargetDispatcher#findTouchedTarget(Cursor)
     */
    @Override
    protected TouchTarget findTouchedTarget(final Cursor cursor) {
        TouchTarget touchTarget = null;

        // TODO Find deepest component of the top most touched window
        for (final Window window : Window.getWindows()) {
            // TODO Check if window is touchable
            final Component component = findDeepestComponent(window, cursor.getX(), cursor.getY());
            if (component != null) {
                touchTarget = new ComponentTouchTarget(component);
                break;
            }
        }

        return touchTarget;
    }

    private Component findDeepestComponent(final Component parent, final int x, final int y) {
        Component deepest = null;

        if (parent.contains(x, y)) {
            if (parent instanceof RootPaneContainer) {
                final Container contentPane = ((RootPaneContainer) parent).getContentPane();
                deepest = findDeepestComponent(contentPane, x - contentPane.getX(), y - contentPane.getY());
            } else if (parent instanceof Container) {
                for (final Component child : ((Container) parent).getComponents()) {
                    deepest = findDeepestComponent(child, x - child.getX(), y - child.getY());
                    if (deepest != null) {
                        break;
                    }
                }
            }

            if (deepest == null) {
                // No child containing the cursor
                deepest = parent;
            }
        }

        return deepest;
    }
}
