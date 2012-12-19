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
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.view.orbit.OrbitView;

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

        final WorldWindowGLCanvas wwd = createWorldWindCanvas();

        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(wwd, BorderLayout.CENTER);
        setContentPane(contentPane);

        initTouchProfile(wwd);
    }

    private WorldWindowGLCanvas createWorldWindCanvas() {
        final WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();

        wwd.setModel(new BasicModel());
        wwd.setPreferredSize(new Dimension(800, 600));

        final OrbitView view = (OrbitView) wwd.getView();
        System.out.println("WorldWindDemo.createWorldWindCanvas: " + view);
        System.out.println(view.getCurrentEyePoint());

        resetOrbitView(wwd, view);
        // Go some distance in the control mouse direction
//        Angle heading = computePanHeading(view, control);
//        Angle distance = computePanAmount(wwd.getModel().getGlobe(), view, control, panStep);
//        LatLon newViewCenter = LatLon.greatCircleEndPosition(view.getCenterPosition(),
//                heading, distance);
//        // Turn around if passing by a pole - TODO: better handling of the pole crossing situation
//        if (this.isPathCrossingAPole(newViewCenter, view.getCenterPosition()))
//            view.setHeading(Angle.POS180.subtract(view.getHeading()));
//        // Set new center pos
//        view.setCenterPosition(new Position(newViewCenter, view.getCenterPosition().getElevation()));

        return wwd;
    }

    protected void resetOrbitView(final WorldWindowGLCanvas wwd, OrbitView view) {
        if (view.getZoom() > 0)   // already in orbit view mode
            return;

        // Find out where on the terrain the eye is looking at in the viewport center
        // TODO: if no terrain is found in the viewport center, iterate toward viewport bottom until it is found
        Vec4 centerPoint = computeSurfacePoint(wwd, view, view.getHeading(), view.getPitch());
        // Reset the orbit view center point heading, pitch and zoom
        if (centerPoint != null) {
            Vec4 eyePoint = view.getEyePoint();
            // Center pos on terrain surface
            Position centerPosition = wwd.getModel().getGlobe().computePositionFromPoint(centerPoint);
            // Compute pitch and heading relative to center position
            Vec4 normal = wwd.getModel().getGlobe().computeSurfaceNormalAtLocation(centerPosition.getLatitude(),
                    centerPosition.getLongitude());
            Vec4 north = wwd.getModel().getGlobe().computeNorthPointingTangentAtLocation(centerPosition.getLatitude(),
                    centerPosition.getLongitude());
            // Pitch
            view.setPitch(Angle.POS180.subtract(view.getForwardVector().angleBetween3(normal)));
            // Heading
            Vec4 perpendicular = view.getForwardVector().perpendicularTo3(normal);
            Angle heading = perpendicular.angleBetween3(north);
            double direction = Math.signum(-normal.cross3(north).dot3(perpendicular));
            view.setHeading(heading.multiply(direction));
            // Zoom
            view.setZoom(eyePoint.distanceTo3(centerPoint));
            // Center pos
            view.setCenterPosition(centerPosition);
        }
    }

    protected boolean isPathCrossingAPole(LatLon p1, LatLon p2) {
        return Math.abs(p1.getLongitude().degrees - p2.getLongitude().degrees) > 20
                && Math.abs(p1.getLatitude().degrees - 90 * Math.signum(p1.getLatitude().degrees)) < 10;
    }

    protected Vec4 computeSurfacePoint(final WorldWindowGLCanvas wwd, OrbitView view, Angle heading, Angle pitch) {
        Globe globe = wwd.getModel().getGlobe();
        // Compute transform to be applied to north pointing Y so that it would point in the view direction
        // Move coordinate system to view center point
        Matrix transform = globe.computeSurfaceOrientationAtPosition(view.getCenterPosition());
        // Rotate so that the north pointing axes Y will point in the look at direction
        transform = transform.multiply(Matrix.fromRotationZ(heading.multiply(-1)));
        transform = transform.multiply(Matrix.fromRotationX(Angle.NEG90.add(pitch)));
        // Compute forward vector
        Vec4 forward = Vec4.UNIT_Y.transformBy4(transform);
        // Return intersection with terrain
        Intersection[] intersections = wwd.getSceneController().getTerrain().intersect(
                new Line(view.getEyePoint(), forward));
        return (intersections != null && intersections.length != 0) ? intersections[0].getIntersectionPoint() : null;
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
