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

package com.github.gestureengine.base.input.controller;

import com.github.gestureengine.api.flow.TouchPointProcessor;
import com.github.gestureengine.api.input.controller.TouchPoint;
import com.mlawrie.yajtl.TUIOCursor;
import com.mlawrie.yajtl.TUIOEvent;
import com.mlawrie.yajtl.TUIOReceiver;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Input controller making use of a TUIO client to provide touch points received from a TUIO server.
 *
 * @see AbstractInputController
 */
public class TuioController extends AbstractInputController {

	private class TuioClientAdapter implements TUIOEvent {

		private final Map<Long, TouchPoint> currentTouchPoints = new HashMap<Long, TouchPoint>();

		/**
		 * @see TUIOEvent#newCursorEvent(TUIOCursor)
		 */
		@Override
		public void newCursorEvent(final TUIOCursor tuioCursor) {
			final TouchPoint touchPoint =
					new TouchPoint(Long.valueOf(tuioCursor.id()).intValue(), Float.valueOf(tuioCursor.x()).intValue(),
							Float.valueOf(tuioCursor.y()).intValue());
			currentTouchPoints.put(tuioCursor.id(), touchPoint);
			processWithNextBlocks();
		}

		/**
		 * @see TUIOEvent#removeCursorEvent(TUIOCursor)
		 */
		@Override
		public void removeCursorEvent(final TUIOCursor tuioCursor) {
			currentTouchPoints.remove(tuioCursor.id());
			processWithNextBlocks();
		}

		/**
		 * @see TUIOEvent#moveCursorEvent(TUIOCursor)
		 */
		@Override
		public void moveCursorEvent(final TUIOCursor tuioCursor) {
			// Update by just replacing the cursor
			newCursorEvent(tuioCursor);
			processWithNextBlocks();
		}

		private void processWithNextBlocks() {
			final Collection<TouchPoint> touchPointList = currentTouchPoints.values();
			for (final TouchPointProcessor nextBlock : nextBlocks) {
				nextBlock.process(touchPointList);
			}
		}
	}

	/**
	 * Logger for this class.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(TuioController.class);

	/**
	 * Default port to be used to connect to the TUIO server.
	 */
	private final static short DEFAULT_TUIO_PORT = 3333;

	/**
	 * Port to be used to connect to the TUIO server.
	 */
	private final short tuioPort;

	/**
	 * TUIO client receiving touch input from to the TUIO server.
	 */
	private TUIOReceiver tuioClient;

	/**
	 * Listener to the TUIO client, adapting the input events into {@link TouchPoint}s.
	 */
	private final TUIOEvent tuioClientAdapter = new TuioClientAdapter();

	/**
	 * Default constructor making use of the default TUIO port number to connect to the TUIO server.
	 *
	 * @see #DEFAULT_TUIO_PORT
	 */
	public TuioController() {
		this(DEFAULT_TUIO_PORT);
	}

	/**
	 * Constructor specifying the TUIO port number to connect to the TUIO server.
	 *
	 * @param tuioPort TUIO port number to connect to the TUIO server.
	 */
	public TuioController(final short tuioPort) {
		this.tuioPort = tuioPort;
	}

	/**
	 * @see AbstractInputController#start()
	 */
	@Override
	public void start() {
		// Ignore behavior of parent class
		if (isStarted()) {
			LOGGER.warn("TUIO input controller is already started");
		} else {
			// Connect to TUIO server if not already done before
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (tuioClient == null) {
				try {
					tuioClient = new TUIOReceiver(screenSize.width, screenSize.height, tuioPort);
				} catch (SocketException e) {
					LOGGER.error("Could not connect to TUIO server", e);
				}
			}

			// Add TUIO client adapter
			if (tuioClient != null) {
				tuioClient.setHandler(tuioClientAdapter);

				// Everything went fine
				super.start();
			}
		}
	}

	/**
	 * @see AbstractInputController#stop()
	 */
	@Override
	public void stop() {
		// Remove TUIO client adapter
		if (tuioClient != null) {
			tuioClient.setHandler(null);
		}
		super.stop();
	}
}
