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

package com.github.gestureengine.api.flow;

public class Cursor {

	private final long id;

	private final int x;

	private final int y;

	public Cursor(final Cursor cursor) {
		this(cursor.id, cursor.x, cursor.y);
	}

	public Cursor(final long id, final int x, final int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public long getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		int hash = (int) (id ^ (id >>> 32));
		hash = 31 * hash + x;
		hash = 31 * hash + y;
		return hash;
	}

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

	@Override
	public String toString() {
		return "{id=" + id + "; x=" + x + "; y=" + y + "}";
	}
}
