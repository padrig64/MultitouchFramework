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

package com.github.touchframework.worldwind.demo;

import com.github.touchframework.api.gesture.recognition.GestureListener;
import com.github.touchframework.api.input.filter.InputFilter;
import com.github.touchframework.base.gesture.recognition.drag.DragEvent;
import com.github.touchframework.base.gesture.recognition.drag.DragRecognizer;
import com.github.touchframework.base.input.filter.BoundingBoxFilter;
import com.github.touchframework.base.input.filter.NoChangeFilter;
import com.github.touchframework.base.input.source.TuioSource;
import com.github.touchframework.base.region.dispatch.DefaultCursorToRegionDispatcher;
import com.github.touchframework.swing.region.ComponentRegion;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class WorldWindDemo extends JFrame {

    /**
     * Generated serial UID.
     */
    private static final long serialVersionUID = -5760659472669104898L;

    public WorldWindDemo() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        wwd.setModel(new BasicModel());
        wwd.setPreferredSize(new Dimension(800, 600));

        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(wwd, BorderLayout.CENTER);
        setContentPane(contentPane);

        initTouchProfile(wwd);
    }

    private void initTouchProfile(final WorldWindowGLCanvas wwd) {
        // Create input source
        final TuioSource inputController = new TuioSource();

        // Configure cursor filtering
        final InputFilter boundingBoxFilter = new BoundingBoxFilter();
        inputController.queue(boundingBoxFilter);
        final NoChangeFilter noChangeFilter = new NoChangeFilter();
        boundingBoxFilter.queue(noChangeFilter);

        // Configure cursor to region dispatcher
        final DefaultCursorToRegionDispatcher cursorToRegionDispatcher = new DefaultCursorToRegionDispatcher();
        cursorToRegionDispatcher.addRegionOnTop(new ComponentRegion(wwd));
        noChangeFilter.queue(cursorToRegionDispatcher);

        // Configure gestures
        final DragRecognizer dragRecognizer = new DragRecognizer();
        cursorToRegionDispatcher.queue(dragRecognizer);
        dragRecognizer.queue(new GestureListener<DragEvent>() {
            @Override
            public void processGestureEvent(final DragEvent event) {
                System.out.println("WorldWindDemo.processGestureEvent: " + event);
            }
        });
        inputController.start();
    }

    public static void main(final String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                final JFrame frame = new WorldWindDemo();
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
