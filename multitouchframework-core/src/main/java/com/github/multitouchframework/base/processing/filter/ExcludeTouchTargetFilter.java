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

package com.github.multitouchframework.base.processing.filter;

import com.github.multitouchframework.api.TouchEvent;
import com.github.multitouchframework.api.TouchTarget;
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Input filter passing the {@link CursorUpdateEvent}s to the following blocks only if the touch target of the events
 * does not match those specified with {@link #addTouchTarget(TouchTarget)}.
 * <p/>
 * All events for a touch target included in this filter will be blocked.
 *
 * @see AbstractFilter
 * @see IncludeTouchTargetFilter
 */
public class ExcludeTouchTargetFilter<E extends TouchEvent> extends AbstractFilter<E> {

    /**
     * Excluded touch targets.
     */
    private final Set<TouchTarget> touchTargets = new HashSet<TouchTarget>();

    /**
     * Constructor specifying the touch targets to be excluded.
     *
     * @param touchTargets Touch targets to be excluded.
     */
    public ExcludeTouchTargetFilter(TouchTarget... touchTargets) {
        if (touchTargets != null) {
            Collections.addAll(this.touchTargets, touchTargets);
        }
    }

    /**
     * Constructor specifying the touch targets to be excluded.
     *
     * @param touchTargets Touch targets to be excluded.
     */
    public ExcludeTouchTargetFilter(Collection<TouchTarget> touchTargets) {
        if (touchTargets != null) {
            this.touchTargets.addAll(touchTargets);
        }
    }

    /**
     * Excludes the specified touch target.
     *
     * @param touchTarget Touch target to be excluded.
     */
    public void addTouchTarget(TouchTarget touchTarget) {
        touchTargets.add(touchTarget);
    }

    /**
     * Includes the specified previously excluded touch target.
     *
     * @param touchTarget Touch target to be included.
     */
    public void removeTouchTarget(TouchTarget touchTarget) {
        touchTargets.remove(touchTarget);
    }

    /**
     * @see AbstractFilter#processWithNextBlocks(TouchEvent)
     */
    @Override
    public void processTouchEvent(E event) {
        if (!touchTargets.contains(event.getTouchTarget())) {
            processWithNextBlocks(event);
        }
    }
}
