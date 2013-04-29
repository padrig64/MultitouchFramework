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
import com.github.multitouchframework.base.cursor.CursorUpdateEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Input filter passing the {@link CursorUpdateEvent}s to the following blocks only if the user ID of the events matches
 * those specified with {@link #addUser(long)}.<br>All events with a user ID not included in this filter will be
 * blocked.
 *
 * @see AbstractFilter
 * @see ExcludeUserFilter
 */
public class IncludeUserFilter<E extends TouchEvent> extends AbstractFilter<E> {

    /**
     * Included user IDs.
     */
    private final Set<Long> userIds = new HashSet<Long>();

    /**
     * Constructor specifying the IDs of the users to be included.
     *
     * @param userIds IDs of the users to be included.
     */
    public IncludeUserFilter(final long... userIds) {
        if (userIds != null) {
            for (final long userId : userIds) {
                this.userIds.add(userId);
            }
        }
    }

    /**
     * Constructor specifying the IDs of the users to be included.
     *
     * @param userIds IDs of the users to be included.
     */
    public IncludeUserFilter(final Collection<Long> userIds) {
        if (userIds != null) {
            this.userIds.addAll(userIds);
        }
    }

    /**
     * Includes the user of the specified ID.
     *
     * @param userId ID of the user to be included.
     */
    public void addUser(final long userId) {
        userIds.add(userId);
    }

    /**
     * Excludes the previously included user of the specified ID.
     *
     * @param userId ID of the user to be excluded.
     */
    public void removeUser(final long userId) {
        userIds.remove(userId);
    }

    /**
     * @see AbstractFilter#processWithNextBlocks(TouchEvent)
     */
    @Override
    public void processTouchEvent(final E event) {
        if (userIds.contains(event.getUserId())) {
            processWithNextBlocks(event);
        }
    }
}
