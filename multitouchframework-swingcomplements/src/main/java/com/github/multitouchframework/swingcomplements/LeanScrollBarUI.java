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

package com.github.multitouchframework.swingcomplements;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

/**
 * Scrollbar UI that makes {@link JScrollBar}s look a bit like the scrollbars on Mac OS X.
 */
public class LeanScrollBarUI extends ScrollBarUI {

    private class ModelChangeAdapter implements PropertyChangeListener, ChangeListener {

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            if ("model".equals(event.getPropertyName())) {
                ((BoundedRangeModel) event.getOldValue()).removeChangeListener(this);
                ((BoundedRangeModel) event.getNewValue()).addChangeListener(this);
                scrollBar.repaint();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            scrollBar.repaint();
        }
    }

    private class MouseControlAdapter extends MouseAdapter {

        private Point prevPoint = null;

        /**
         * @see MouseAdapter#mousePressed(MouseEvent)
         */
        @Override
        public void mousePressed(final MouseEvent e) {
            prevPoint = e.getPoint();
        }

        /**
         * @see MouseAdapter#mouseReleased(MouseEvent)
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
            prevPoint = null;
        }

        /**
         * @see MouseAdapter#mouseDragged(MouseEvent)
         */
        @Override
        public void mouseDragged(final MouseEvent e) {
            if (prevPoint != null) {
                switch (scrollBar.getOrientation()) {

                    case JScrollBar.VERTICAL:
                        final int diffVerticalValue = diffPixelsToDiffModelValue(e.getY() - prevPoint.y);
                        scrollBar.getModel().setValue(scrollBar.getModel().getValue() + diffVerticalValue);
                        break;

                    case JScrollBar.HORIZONTAL:
                        final int diffHorizontalValue = diffPixelsToDiffModelValue(e.getX() - prevPoint.x);
                        scrollBar.getModel().setValue(scrollBar.getModel().getValue() + diffHorizontalValue);
                        break;

                    default:
                        LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
                }
            }
            prevPoint = e.getPoint();
        }
    }

    private static class VisibilityAdapter implements MouseListener, MouseMotionListener, ComponentListener,
            TimingTarget {

        private final float MIN_ALPHA = 0.0f;
        private final float MAX_ALPHA = 1.0f;
        private final float FADE_IN_MAX_DURATION = 125;
        private final float FADE_OUT_MAX_DURATION = 300;

        private static Component sharedScrollingBar = null;

        private final JScrollBar scrollBar;

        private int visibleRequestCounter = 0;

        private float initialAlpha = MIN_ALPHA;
        private float targetAlpha = MAX_ALPHA;
        private float currentAlpha = MIN_ALPHA;

        private Animator animator = null;

        public VisibilityAdapter(final JScrollBar scrollBar) {
            this.scrollBar = scrollBar;
            final TimingSource ts = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(ts);
            ts.init();
        }

        /**
         * @see ComponentListener#componentShown(ComponentEvent)
         */
        @Override
        public void componentShown(final ComponentEvent e) {
            requestVisible(true);
            requestVisible(false);
        }

        /**
         * @see ComponentListener#componentHidden(ComponentEvent)
         */
        @Override
        public void componentHidden(final ComponentEvent e) {
            visibleRequestCounter = 0;
        }

        /**
         * @see ComponentListener#componentResized(ComponentEvent)
         */
        @Override
        public void componentResized(final ComponentEvent e) {
            // Nothing to be done
        }

        /**
         * @see ComponentListener#componentMoved(ComponentEvent)
         */
        @Override
        public void componentMoved(final ComponentEvent e) {
            // Nothing to be done
        }

        /**
         * @see MouseListener#mouseEntered(MouseEvent)
         */
        @Override
        public void mouseEntered(final MouseEvent e) {
            if ((sharedScrollingBar == null) || (sharedScrollingBar.equals(e.getComponent()))) {
                requestVisible(true);
            }
        }

        /**
         * @see MouseListener#mouseExited(MouseEvent)
         */
        @Override
        public void mouseExited(final MouseEvent e) {
            if ((sharedScrollingBar == null) || (sharedScrollingBar.equals(e.getComponent()))) {
                requestVisible(false);
            }
        }

        /**
         * @see MouseListener#mousePressed(MouseEvent)
         */
        @Override
        public void mousePressed(final MouseEvent e) {
            sharedScrollingBar = scrollBar;
            requestVisible(true);
        }

        /**
         * @see MouseListener#mouseReleased(MouseEvent)
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
            sharedScrollingBar = null;
            requestVisible(false);
        }

        /**
         * @see MouseListener#mouseClicked(MouseEvent)
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            // Nothing to be done
        }

        /**
         * @see MouseMotionListener#mouseMoved(MouseEvent)
         */
        @Override
        public void mouseMoved(final MouseEvent e) {
            if (visibleRequestCounter == 0) {
                // This can happen after dragging another scrollbar, releasing on this scrollbar and moving the mouse
                requestVisible(true); // Simulate a mouse entered
            }
        }

        /**
         * @see MouseMotionListener#mouseDragged(MouseEvent)
         */
        @Override
        public void mouseDragged(final MouseEvent e) {
            mouseMoved(e);
        }

        private void requestVisible(final boolean visible) {
            if (visible) {
                visibleRequestCounter++;
            } else {
                visibleRequestCounter--;
            }

            if (visibleRequestCounter == 1) {
                fadeIn();
            } else if (visibleRequestCounter == 0) {
                fadeOut();
            } else if (visibleRequestCounter < 0) {
                visibleRequestCounter = 0;
            }
        }

        private void fadeIn() {
            // Stop previous animation
            if ((animator != null) && (animator.isRunning())) {
                animator.removeTarget(this);
                animator.stop();
            }

            // Start new animation from where the previous one stopped
            initialAlpha = currentAlpha;
            targetAlpha = MAX_ALPHA;
            final long duration = (long) ((MAX_ALPHA - initialAlpha) * FADE_IN_MAX_DURATION / (MAX_ALPHA - MIN_ALPHA));
            if (duration <= 0) {
                timingEvent(null, 1.0);
            } else {
                animator = new Animator.Builder().setDuration(duration, TimeUnit.MILLISECONDS).setInterpolator(new
                        SplineInterpolator(0.8, 0.2, 0.2, 0.8)).addTarget(this).build();
                animator.start();
            }
        }

        private void fadeOut() {
            // Stop previous animation
            if ((animator != null) && (animator.isRunning())) {
                animator.removeTarget(this);
                animator.stop();
            }

            // Start new animation from where the previous one stopped
            initialAlpha = currentAlpha;
            targetAlpha = MIN_ALPHA;
            final long duration = (long) ((initialAlpha - MIN_ALPHA) * FADE_OUT_MAX_DURATION / (MAX_ALPHA - MIN_ALPHA));
            if (duration <= 0) {
                timingEvent(null, 1.0);
            } else {
                animator = new Animator.Builder().setDuration(duration, TimeUnit.MILLISECONDS).setInterpolator(new
                        SplineInterpolator(0.8, 0.2, 0.2, 0.8)).addTarget(this).build();
                animator.start();
            }
        }

        /**
         * @see TimingTarget#begin(Animator)
         */
        @Override
        public void begin(final Animator animator) {
            // Nothing to be done because we stop the animation manually
        }

        /**
         * @see TimingTarget#end(Animator)
         */
        @Override
        public void end(final Animator animator) {
            // Nothing to be done because we stop the animation manually
        }

        /**
         * @see TimingTarget#repeat(Animator)
         */
        @Override
        public void repeat(final Animator animator) {
            // Nothing to be done
        }

        /**
         * @see TimingTarget#reverse(Animator)
         */
        @Override
        public void reverse(final Animator animator) {
            // Nothing to be done
        }

        /**
         * @see TimingTarget#timingEvent(Animator, double)
         */
        @Override
        public void timingEvent(final Animator animator, final double v) {
            currentAlpha = (float) (v * (targetAlpha - initialAlpha) + initialAlpha);
            scrollBar.repaint();
        }
    }

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LeanScrollBarUI.class);

    private static final Color BASE_COLOR = new Color(64, 64, 64, 192);

    private static final int MIN_LENGTH = 50; // Including the heads

    private static final int THICKNESS = 7;

    private JScrollBar scrollBar = null;

    private VisibilityAdapter visibilityAdapter = null;

    private final ModelChangeAdapter modelChangeAdapter = new ModelChangeAdapter();

    private final MouseControlAdapter mouseControlAdapter = new MouseControlAdapter();

    /**
     * Creates a UI for the specified component.
     *
     * @param c Scrollbar to create the UI for.
     *
     * @return UI for the specified component.
     */
    public static ComponentUI createUI(final JComponent c) {
        return new LeanScrollBarUI();
    }

    /**
     * Protected default constructor.
     *
     * @see #createUI(javax.swing.JComponent)
     */
    protected LeanScrollBarUI() {
        super();
    }

    public void installUI(final JComponent c) {
        if (c instanceof JScrollBar) {
            scrollBar = (JScrollBar) c;
            visibilityAdapter = new VisibilityAdapter(scrollBar);

            installDefaults();
//            installComponents();
            installListeners();
//            installKeyboardActions();
        }
    }

    public void uninstallUI(final JComponent c) {
//        uninstallKeyboardActions();
        uninstallListeners();
//        uninstallComponents();
        uninstallDefaults();

        scrollBar = null;
        visibilityAdapter = null;
    }

    private void installDefaults() {
//        final Border border = scrollBar.getBorder();
//        if ((border == null) || (border instanceof UIResource)) {
//            scrollBar.setBorder(new LineBorder(Color.RED));
//        }
    }

    private void uninstallDefaults() {
//        if (scrollBar.getBorder() instanceof UIResource) {
//            scrollBar.setBorder(null);
//        }
    }

    private void installListeners() {
        scrollBar.addPropertyChangeListener(modelChangeAdapter);
        scrollBar.getModel().addChangeListener(modelChangeAdapter);

        scrollBar.addMouseListener(mouseControlAdapter);
        scrollBar.addMouseMotionListener(mouseControlAdapter);

        scrollBar.addMouseListener(visibilityAdapter);
        scrollBar.addMouseMotionListener(visibilityAdapter);
        scrollBar.addComponentListener(visibilityAdapter);

//        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//            @Override
//            public void eventDispatched(final AWTEvent event) {
//                if (visibilityAdapter.exclusionAncestor != null) {
//                    if (event.getID() == MouseEvent.MOUSE_ENTERED) {
//                        visibilityAdapter.mouseEntered((MouseEvent) event);
//                    } else if (event.getID() == MouseEvent.MOUSE_EXITED) {
//                        visibilityAdapter.mouseExited((MouseEvent) event);
//                    }
//                }
//            }
//        }, MouseEvent.MOUSE_EVENT_MASK);
    }

    private void uninstallListeners() {
        scrollBar.removePropertyChangeListener(modelChangeAdapter);
        scrollBar.getModel().removeChangeListener(modelChangeAdapter);

        scrollBar.removeMouseListener(mouseControlAdapter);
        scrollBar.removeMouseMotionListener(mouseControlAdapter);

        scrollBar.removeMouseListener(visibilityAdapter);
        scrollBar.removeComponentListener(visibilityAdapter);
    }

    /**
     * @see javax.swing.plaf.ScrollBarUI#getMinimumSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getMinimumSize(final JComponent c) {
        final Dimension size;
        final Insets insets = scrollBar.getInsets();

        switch (scrollBar.getOrientation()) {

            case JScrollBar.VERTICAL:
                size = new Dimension(insets.left + THICKNESS + insets.right, insets.top + MIN_LENGTH + insets.bottom);
                break;

            case JScrollBar.HORIZONTAL:
                size = new Dimension(insets.left + MIN_LENGTH + insets.right, insets.top + THICKNESS + insets.bottom);
                break;

            default:
                LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
                size = null;
        }

        return size;
    }

    /**
     * @see javax.swing.plaf.ScrollBarUI#getPreferredSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final Dimension size;
        final Insets insets = scrollBar.getInsets();

        switch (scrollBar.getOrientation()) {

            case JScrollBar.VERTICAL:
                size = new Dimension(insets.left + THICKNESS + insets.right, insets.top + insets.bottom);
                break;

            case JScrollBar.HORIZONTAL:
                size = new Dimension(insets.left + insets.right, insets.top + THICKNESS + insets.bottom);
                break;

            default:
                LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
                size = null;
        }

        return size;
    }

    /**
     * @see javax.swing.plaf.ScrollBarUI#paint(java.awt.Graphics, javax.swing.JComponent)
     */
    @Override
    public void paint(final Graphics g, final JComponent c) {

        // Paint in image buffer because alpha composite does not seem to work as expected on the given Graphics
        final BufferedImage buffer = new BufferedImage(scrollBar.getWidth(), scrollBar.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(BASE_COLOR.getRed(), BASE_COLOR.getGreen(), BASE_COLOR.getBlue(),
                (int) (visibilityAdapter.currentAlpha * 255)));

        final int extentInPixels = getExtentInPixels();
        final int valueInPixels = getValueInPixels();

        switch (scrollBar.getOrientation()) {
            case JScrollBar.VERTICAL:
                paintVertical(g2d, valueInPixels, extentInPixels);
                break;
            case JScrollBar.HORIZONTAL:
                paintHorizontal(g2d, valueInPixels, extentInPixels);
                break;
            default:
                LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
        }

        g2d.dispose();

        g.drawImage(buffer, 0, 0, null);
    }

    private int getMaxExtentInPixels() {
        final int maxExtentInPixels;

        final Insets insets = scrollBar.getInsets();

        switch (scrollBar.getOrientation()) {
            case JScrollBar.VERTICAL:
                maxExtentInPixels = scrollBar.getHeight() - insets.top - insets.bottom;
                break;
            case JScrollBar.HORIZONTAL:
                maxExtentInPixels = scrollBar.getWidth() - insets.left - insets.right;
                break;
            default:
                LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
                maxExtentInPixels = 0;
        }

        return maxExtentInPixels;
    }

    private int getExtentInPixels() {
        final int extentInPixels;
        final BoundedRangeModel model = scrollBar.getModel();

        /* extentInPixels = f(modelExtent)       */
        /*                                       */
        /* modelExtent       | extentInPixels    */
        /* ------------------------------------- */
        /* 0                 | MIN_LENGTH        */
        /* maximum - minimum | maxExtentInPixels */
        extentInPixels = model.getExtent() * (getMaxExtentInPixels() - MIN_LENGTH) / (model.getMaximum() - model
                .getMinimum()) + MIN_LENGTH;

        return extentInPixels;
    }

    private int getValueInPixels() {
        final int valueInPixels;
        final BoundedRangeModel model = scrollBar.getModel();

        /* valueInPixels = f(modelValue)                        */
        /*                                                      */
        /* modelValue       | valueInPixels                     */
        /* ---------------------------------------------------- */
        /* minimum          | 0                                 */
        /* maximum - extent | maxValueInPixels - extentInPixels */
        valueInPixels = (model.getValue() - model.getMinimum()) * (getMaxExtentInPixels() - getExtentInPixels()) /
                (model.getMaximum() - model.getMinimum() - model.getExtent());

        return valueInPixels;
    }

    private int diffPixelsToDiffModelValue(final int diffValueInPixels) {
        final int modelValue;
        final BoundedRangeModel model = scrollBar.getModel();

        modelValue = diffValueInPixels * (model.getMaximum() - model.getMinimum() - model.getExtent()) /
                (getMaxExtentInPixels() - getExtentInPixels());

        return modelValue;
    }

    private void paintVertical(final Graphics2D g2d, final int valueInPixels, final int extentInPixels) {
        final Insets insets = scrollBar.getInsets();
        final int barX = insets.left;
        final int barY = insets.top + valueInPixels;

        // Paint head
        g2d.fillOval(barX, barY, THICKNESS, THICKNESS);

        // Paint tail
        g2d.fillOval(barX, barY + extentInPixels - THICKNESS, THICKNESS, THICKNESS);

        // Paint trunk
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g2d.fillRect(barX, barY + THICKNESS / 2, THICKNESS, extentInPixels - THICKNESS / 2 - THICKNESS / 2);
    }

    private void paintHorizontal(final Graphics2D g2d, final int valueInPixels, final int extentInPixels) {
        final Insets insets = scrollBar.getInsets();
        final int barX = insets.left + valueInPixels;
        final int barY = insets.top;

        // Paint head
        g2d.fillOval(barX, barY, THICKNESS, THICKNESS);

        // Paint tail
        g2d.fillOval(barX + extentInPixels - THICKNESS, barY, THICKNESS, THICKNESS);

        // Paint trunk
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g2d.fillRect(barX + THICKNESS / 2, barY, extentInPixels - THICKNESS / 2 - THICKNESS / 2, THICKNESS);
    }
}
