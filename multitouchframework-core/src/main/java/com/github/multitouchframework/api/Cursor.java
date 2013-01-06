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

package com.github.multitouchframework.api;

/**
 * Entity representing a touch point.<br>This is a point of contact with the touch-enabled surface.<br>Typically, The
 * coordinates of the cursor correspond to the location on the touch screen, and cursors are typically created by input
 * controllers.
 */
public class Cursor {

    /**
     * Cursor ID.<br>All cursors that are simultaneously in contact with the touch-enabled surface have unique IDs.<br>
     * However, a same ID may be re-used by sub-sequent cursors.
     */
    private final long id;

    /**
     * X coordinate of the cursor on the touch-enabled surface.
     */
    private final int x;

    /**
     * Y coordinate of the cursor on the touch-enabled surface.
     */
    private final int y;

    /**
     * Constructor specifying a cursor to copy from.
     *
     * @param cursor Cursor to be duplicated.
     */
    public Cursor(final Cursor cursor) {
        this(cursor.id, cursor.x, cursor.y);
    }

    /**
     * Constructor specifying all the ID and the coordinates of the cursor.<br>The meaning of the coordinates is left
     * to the application logic and generally depends on the {@link com.github.multitouchframework.api.source
     * .InputSource}.<br>You may refer to the {@link com.github.multitouchframework.api.source.InputSource}
     * implementation in use in the application.
     *
     * @param id Cursor ID.
     * @param x  X coordinate of the cursor.
     * @param y  Y coordinate of the cursor.
     */
    public Cursor(final long id, final int x, final int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the ID.
     *
     * @return Cursor ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the X coordinate.<br>The meaning of the coordinates is left to the application logic and generally depends
     * on the {@link com.github.multitouchframework.api.source.InputSource}.
     *
     * @return X coordinate of the cursor.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y coordinate.<br>The meaning of the coordinates is left to the application logic and generally depends
     * on the {@link com.github.multitouchframework.api.source.InputSource}.
     *
     * @return Y coordinate of the cursor.
     */
    public int getY() {
        return y;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = (int) (id ^ (id >>> 32));
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        return hash;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(final Object o) {
        final boolean equal;

        if (this == o) {
            // Same instance
            equal = true;
        } else if ((o == null) || (getClass() != o.getClass())) {
            // Different class
            equal = false;
        } else {
            // Same class, so check attributes
            final Cursor that = (Cursor) o;
            equal = (id == that.id) && (x == that.x) && (y == that.y);
        }

        return equal;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "{id=" + id + "; x=" + x + "; y=" + y + "}";
    }
}
