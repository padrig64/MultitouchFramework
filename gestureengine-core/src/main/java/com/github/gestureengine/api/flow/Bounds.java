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

public class Bounds {

	private final String id;

	private final int x;

	private final int y;

	private final int width;

	private final int height;

	public Bounds(final Bounds bounds) {
		this(bounds.id, bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Bounds(final String id, final int x, final int y, final int width, final int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isIn(final Cursor cursor) {
		return isIn(cursor.getX(), cursor.getY());
	}

	public boolean isIn(final int x, final int y) {
		return (this.x <= x) && (x <= (this.x + this.width)) && (this.y <= y) && (y <= (this.y + height));
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + x;
		result = 31 * result + y;
		result = 31 * result + width;
		result = 31 * result + height;
		return result;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Bounds bounds = (Bounds) o;

		if (height != bounds.height) {
			return false;
		}
		if (width != bounds.width) {
			return false;
		}
		if (x != bounds.x) {
			return false;
		}
		if (y != bounds.y) {
			return false;
		}
		if (id != null ? !id.equals(bounds.id) : bounds.id != null) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "{id=" + id + "; x=" + x + "; y=" + y + "; w=" + width + "; h=" + height + "}";
	}
}
