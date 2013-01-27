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

package com.github.multitouchframework.base.gesture;

import com.github.multitouchframework.api.Cursor;
import com.github.multitouchframework.api.Region;
import com.github.multitouchframework.api.gesture.GestureRecognizer;
import com.github.multitouchframework.api.touch.CursorEvent;
import com.github.multitouchframework.api.touch.TouchEvent;
import com.github.multitouchframework.api.touch.TouchListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Abstract implementation of a gesture recognizer.<br>It provides basic support for a per-region gesture recognition
 * and for notifying gesture listeners.<br>Implementing sub-classes are meant to create context objects that will hold
 * all the metadata associated to the recognition of the gesture for a specific region, and to process the cursors for
 * this context and region.
 *
 * @param <C> Type of context holding the recognition metadata associated to a region.
 * @param <E> Type of gesture events fired by the gesture recognizer.
 *
 * @see GestureRecognizer
 * @see TouchEvent
 * @see TouchListener
 */
public abstract class AbstractGestureRecognizer<C, E extends TouchEvent> implements GestureRecognizer<E> {

    /**
     * Minimum number of cursors required to perform the gesture.
     */
    private int minCursorCount = 1;

    /**
     * Maximum number of cursors required to perform the gesture.
     */
    private int maxCursorCount = Integer.MAX_VALUE;

    /**
     * Listeners to events of the gesture.
     *
     * @see #queue(TouchListener)
     * @see #dequeue(TouchListener)
     * @see #fireGestureEvent(TouchEvent)
     */
    private final List<TouchListener<E>> gestureListeners = new ArrayList<TouchListener<E>>();

    /**
     * Saved recognition context for each region.
     *
     * @see #getContext(long, Region)
     * @see #createContext(long, Region)
     */
    private final Map<Region, C> regionContexts = new WeakHashMap<Region, C>();

    /**
     * Constructor specifying the minimum and maximum numbers of cursors required to perform the gesture.
     *
     * @param minCursorCount Minimum number of cursors required to perform the gesture.
     * @param maxCursorCount Maximum number of cursors required to perform the gesture.
     */
    public AbstractGestureRecognizer(final int minCursorCount, final int maxCursorCount) {
        setMinCursorCount(minCursorCount);
        setMaxCursorCount(maxCursorCount);
    }

    /**
     * Gets the minimum number of cursors required to perform the gesture.
     *
     * @return Minimum cursor count.
     */
    public int getMinCursorCount() {
        return minCursorCount;
    }

    /**
     * Sets the minimum number of cursors required to perform the gesture.
     *
     * @param count Minimum cursor count.
     */
    public void setMinCursorCount(final int count) {
        minCursorCount = count;
    }

    /**
     * Gets the maximum number of cursors required to perform the gesture.
     *
     * @return Maximum cursor count.
     */
    public int getMaxCursorCount() {
        return maxCursorCount;
    }

    /**
     * Sets the maximum number of cursors required to perform the gesture.
     *
     * @param count Maximum cursor count.
     */
    public void setMaxCursorCount(final int count) {
        maxCursorCount = count;
    }

    /**
     * States whether the number of input cursors matches the minimum and maximum required by the gesture.<br>This is a
     * convenience that may be used by sub-classes.
     *
     * @param cursorCount Input cursor count.
     *
     * @return True if the minimum and maximum are honored, false otherwise.
     */
    protected boolean isCursorCountValid(final int cursorCount) {
        return (minCursorCount <= cursorCount) && (cursorCount <= maxCursorCount);
    }

    /**
     * @see GestureRecognizer#queue(Object)
     */
    @Override
    public void queue(final TouchListener<E> gestureListener) {
        gestureListeners.add(gestureListener);
    }

    /**
     * @see GestureRecognizer#dequeue(Object)
     */
    @Override
    public void dequeue(final TouchListener<E> gestureListener) {
        gestureListeners.remove(gestureListener);
    }

    /**
     * Fires the specified event to the registered gesture listeners.<br>This method is to be called by sub-classes to
     * notify gesture listeners.
     *
     * @param event Gesture event to be fired.
     */
    protected void fireGestureEvent(final E event) {
        for (final TouchListener<E> listener : gestureListeners) {
            listener.processTouchEvent(event);
        }
    }

    /**
     * @see GestureRecognizer#processTouchEvent(TouchEvent)
     */
    @Override
    public void processTouchEvent(final CursorEvent event) {
        process(getContext(event.getUserId(), event.getRegion()), event.getUserId(), event.getRegion(),
                event.getCursors());
    }

    /**
     * Gets a context for the specified region.<br>This method will create a new context for the region if it does not
     * exist.
     *
     * @param userId ID of the user performing the gesture.
     * @param region Region to get a context for.
     *
     * @return Context for the region.
     *
     * @see #createContext(long, Region)
     */
    protected C getContext(final long userId, final Region region) {
        C context = regionContexts.get(region);
        if (context == null) {
            context = createContext(userId, region);
            regionContexts.put(region, context);
        }
        return context;
    }

    /**
     * Creates a new context for the specified user and region.<br>This method is to be implemented by sub-classes.
     *
     * @param userId ID of the user performing the gesture.
     * @param region Region to create a context for.
     *
     * @return Newly created context for the user and region.
     */
    protected abstract C createContext(long userId, Region region);

    /**
     * Processes the specified cursors for the specified region and region context.<br>This method is to be
     * implemented by sub-classes.
     *
     * @param context Context associated to the region to which the cursors apply.
     * @param userId  ID of the user performing the gesture.
     * @param region  Touchable region to which the cursors are associated.
     * @param cursors Cursors to be processed.
     */
    protected abstract void process(C context, long userId, Region region, Collection<Cursor> cursors);
}
