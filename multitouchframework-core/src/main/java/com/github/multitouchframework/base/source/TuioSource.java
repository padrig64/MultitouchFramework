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

package com.github.multitouchframework.base.source;

import com.github.multitouchframework.api.Cursor;
import com.github.multitouchframework.api.Region;
import com.github.multitouchframework.api.touch.CursorEvent;
import com.github.multitouchframework.api.touch.TouchListener;
import com.github.multitouchframework.base.ScreenRegion;
import com.mlawrie.yajtl.TUIOCursor;
import com.mlawrie.yajtl.TUIOEvent;
import com.mlawrie.yajtl.TUIOReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.BindException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * Input controller making use of a TUIO client to provide cursors received from a TUIO server.<br>This implementation
 * is based on the YAJTL library (MIT license) instead of the official TUIO client implementation (GPL license).
 *
 * @see AbstractInputSource
 */
public class TuioSource extends AbstractInputSource {

    private class TuioClientAdapter implements TUIOEvent {

        /**
         * Cursors currently detected by the touch surface.
         */
        private final Map<Long, Cursor> currentCursors = new HashMap<Long, Cursor>();

        /**
         * @see TUIOEvent#newCursorEvent(TUIOCursor)
         */
        @Override
        public void newCursorEvent(final TUIOCursor tuioCursor) {
            // Sanity check
            if (currentCursors.containsKey(tuioCursor.id())) {
                LOGGER.warn("+++ Cursor " + tuioCursor.id() + " was already tracked");
            }

            // Process cursor addition
            final Cursor cursor = new Cursor(tuioCursor.id(), Float.valueOf(tuioCursor.x()).intValue(),
                    Float.valueOf(tuioCursor.y()).intValue());
            currentCursors.put(tuioCursor.id(), cursor);
            processWithNextBlocks();
        }

        /**
         * @see TUIOEvent#removeCursorEvent(TUIOCursor)
         */
        @Override
        public void removeCursorEvent(final TUIOCursor tuioCursor) {
            // Sanity check
            if (!currentCursors.containsKey(tuioCursor.id())) {
                LOGGER.warn("--- Cursor " + tuioCursor.id() + " was not tracked");
            }

            // Process cursor removal
            currentCursors.remove(tuioCursor.id());
            processWithNextBlocks();
        }

        /**
         * @see TUIOEvent#moveCursorEvent(TUIOCursor)
         */
        @Override
        public void moveCursorEvent(final TUIOCursor tuioCursor) {
            // Sanity check
            if (!currentCursors.containsKey(tuioCursor.id())) {
                LOGGER.warn("~~~ Cursor " + tuioCursor.id() + " was not tracked (it will now be tracked)");
            }

            // Update by just replacing the cursor
            final Cursor cursor = new Cursor(tuioCursor.id(), Float.valueOf(tuioCursor.x()).intValue(),
                    Float.valueOf(tuioCursor.y()).intValue());
            currentCursors.put(tuioCursor.id(), cursor);
            processWithNextBlocks();
        }

        /**
         * Processes the current cursors using the blocks/listeners that are queued/added to this input source.
         */
        private void processWithNextBlocks() {
            final CursorEvent event = new CursorEvent(0, region, currentCursors.values());
            for (final TouchListener<CursorEvent> nextBlock : nextBlocks) {
                nextBlock.processTouchEvent(event);
            }
        }
    }

    /**
     * Logger for this class.
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(TuioSource.class);

    /**
     * Default port to be used to connect to the TUIO server.
     */
    private final static short DEFAULT_TUIO_PORT = 3333;

    /**
     * Default touchable region for which the cursors will be produced.
     */
    private final static Region DEFAULT_REGION = new ScreenRegion();

    /**
     * Port to be used to connect to the TUIO server.
     */
    private final short tuioPort;

    /**
     * Touchable region for which the cursors will be produced.
     */
    private Region region = DEFAULT_REGION;

    /**
     * Reference width of the source of cursors.<br>The X coordinate of all cursors generated by this input source
     * will be within the range [0; sourceWidth-1].
     */
    private final int sourceWidth;

    /**
     * Reference height of the source of cursors.<br>The Y coordinate of all cursors generated by this input source
     * will be within the range [0; sourceHeight-1].
     */
    private final int sourceHeight;

    /**
     * TUIO client receiving touch input from to the TUIO server.
     */
    private TUIOReceiver tuioClient;

    /**
     * Listener to the TUIO client, adapting the input events into {@link com.github.multitouchframework.api.Cursor}s.
     */
    private final TUIOEvent tuioClientAdapter = new TuioClientAdapter();

    /**
     * Default constructor making use of the default TUIO port number to connect to the TUIO server.
     *
     * @see #DEFAULT_TUIO_PORT
     */
    public TuioSource() {
        this(DEFAULT_TUIO_PORT);
    }

    /**
     * Constructor specifying the reference dimensions for all cursors.
     *
     * @param sourceWidth  Reference width for all cursors.
     * @param sourceHeight Reference height for all cursors.
     *
     * @see #sourceWidth
     * @see #sourceHeight
     */
    public TuioSource(final int sourceWidth, final int sourceHeight) {
        this(DEFAULT_TUIO_PORT, sourceWidth, sourceHeight);
    }

    /**
     * Constructor specifying the TUIO port number to connect to the TUIO server.
     *
     * @param tuioPort TUIO port number to connect to the TUIO server.
     */
    public TuioSource(final short tuioPort) {
        this.tuioPort = tuioPort;

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.sourceWidth = screenSize.width;
        this.sourceHeight = screenSize.height;
    }

    /**
     * Constructor specifying the TUIO port number to connect to the TUIO server and the reference dimensions for all
     * cursors.
     *
     * @param tuioPort     TUIO port number to connect to the TUIO server.
     * @param sourceWidth  Reference width for all cursors.
     * @param sourceHeight Reference height for all cursors.
     *
     * @see #sourceWidth
     * @see #sourceHeight
     */
    public TuioSource(final short tuioPort, final int sourceWidth, final int sourceHeight) {
        this.tuioPort = tuioPort;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(final Region region) {
        this.region = region;
    }

    /**
     * @see AbstractInputSource#start()
     */
    @Override
    public void start() {
        // Ignore behavior of parent class
        if (isStarted()) {
            LOGGER.warn("TUIO input controller is already started");
        } else {
            // Connect to TUIO server if not already done before
            if (tuioClient == null) {
                try {
                    tuioClient = new TUIOReceiver(sourceWidth, sourceHeight, tuioPort);
                } catch (BindException e) {
                    LOGGER.error("Could not connect to TUIO server on port " + tuioPort + ": " + e.getMessage(), e);
                } catch (SocketException e) {
                    LOGGER.error("Could not connect to TUIO server on port " + tuioPort + ": " + e.getMessage(), e);
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
     * @see AbstractInputSource#stop()
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
