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

package com.github.multitouchframework.base.dispatch;

import com.github.multitouchframework.api.Cursor;
import com.github.multitouchframework.api.Region;
import com.github.multitouchframework.api.dispatch.CursorToRegionDispatcher;
import com.github.multitouchframework.api.flow.CursorProcessor;
import com.github.multitouchframework.base.ScreenRegion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of a cursor to region dispatcher.<br>It provides implementation of the (de-)queuing of
 * blocks, as well as the basic dispatching of cursors to regions and the forwarding of the results to the queued
 * blocks. However, the discovery of a region for a single cursor is left to concrete sub-classes.<br>This
 * implementation makes the regions catch the cursors on finger down, and release the cursors on finger up only. But the
 * regions will hold the cursors even if they leave these regions. This makes it more convenient for users working on
 * small regions of the screen (for instance, when several users are working on different small maps displayed on the
 * same device).
 *
 * @see #findTouchedRegion(Cursor)
 */
public abstract class AbstractCursorToRegionDispatcher implements CursorToRegionDispatcher {

    /**
     * Default region that would hold the cursors if no other region on top of it would hold those cursors.
     */
    public static final Region SCREEN_REGION = new ScreenRegion(); // Whole screen

    /**
     * Mapping between cursors and region resulting from the call to {@link #processCursors(Region, Collection)}.
     */
    private Map<Long, Region> oldCursorToRegion = new HashMap<Long, Region>(); // Initially, no cursor down

    /**
     * Cursor-per-region processors connected and processing the output regions and cursors from this dispatcher.
     */
    private final List<CursorProcessor> nextBlocks = new ArrayList<CursorProcessor>();

    /**
     * @see CursorToRegionDispatcher#queue(Object)
     */
    @Override
    public void queue(final CursorProcessor cursorRegionProcessor) {
        nextBlocks.add(cursorRegionProcessor);
    }

    /**
     * @see CursorToRegionDispatcher#dequeue(Object)
     */
    @Override
    public void dequeue(final CursorProcessor cursorRegionProcessor) {
        nextBlocks.remove(cursorRegionProcessor);
    }

    /**
     * @see AbstractCursorToRegionDispatcher#processCursors(Region, Collection)
     */
    @Override
    public void processCursors(final Region region, final Collection<Cursor> cursors) {
        final Map<Long, Region> newCursorToRegion = new HashMap<Long, Region>();
        final Map<Region, Collection<Cursor>> updatesToBeForwarded = new HashMap<Region, Collection<Cursor>>();

        for (final Cursor cursor : cursors) {
            // Find the region holding the cursor
            Region assignedRegion = oldCursorToRegion.get(cursor.getId());
            if (assignedRegion == null) {
                // Find a new candidate region to hold the cursor
                assignedRegion = findTouchedRegion(cursor);
            } else {
                // Region already holds the cursor
                oldCursorToRegion.remove(cursor.getId());
            }

            if (assignedRegion != null) {
                // Update cursor for this region
                newCursorToRegion.put(cursor.getId(), assignedRegion);

                Collection<Cursor> cursorsForThisRegion = updatesToBeForwarded.get(assignedRegion);
                if (cursorsForThisRegion == null) {
                    cursorsForThisRegion = new HashSet<Cursor>();
                    updatesToBeForwarded.put(assignedRegion, cursorsForThisRegion);
                }
                cursorsForThisRegion.add(cursor);
            }
        }

        // Clean up old mapping to notify for regions that have no more cursor
        for (final Region oldRegion : oldCursorToRegion.values()) {
            if (!updatesToBeForwarded.containsKey(oldRegion)) {
                updatesToBeForwarded.put(oldRegion, Collections.<Cursor>emptySet());
            }
        }

        // Forward updated regions and cursors to next blocks
        for (final Map.Entry<Region, Collection<Cursor>> entry : updatesToBeForwarded.entrySet()) {
            forwardToNextBlocks(entry.getKey(), entry.getValue());
        }

        // Save mapping for next time
        oldCursorToRegion = newCursorToRegion;
    }

    /**
     * Finds the region that is touched by the specified cursor.
     *
     * @param cursor Cursor pointing to the region to be found.
     *
     * @return Touched region if found, null otherwise.
     */
    protected abstract Region findTouchedRegion(final Cursor cursor);

    /**
     * Forwards the specified region with its cursors to the next blocks.<br>Typically, this method is called for each
     * region touch by the cursors processed in {@link #processCursors(Region, Collection)}.
     *
     * @param region  Region holding the specified cursors.
     * @param cursors Cursors for the specified region.
     */
    private void forwardToNextBlocks(final Region region, final Collection<Cursor> cursors) {
        for (final CursorProcessor nextBlock : nextBlocks) {
            nextBlock.processCursors(region, cursors);
        }
    }
}
