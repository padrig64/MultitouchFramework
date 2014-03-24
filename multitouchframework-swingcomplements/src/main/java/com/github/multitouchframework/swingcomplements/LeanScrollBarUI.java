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
import javax.swing.Timer;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

// TODO Finish implementation

/**
 * Scrollbar UI that makes {@link JScrollBar}s look a bit like the scrollbars on Mac OS X.
 */
public class LeanScrollBarUI extends ScrollBarUI {

    /**
     * Entity responsible for handling the dragging of the scrollbar using the mouse.
     */
    private class MouseControlAdapter extends MouseAdapter {

        /**
         * Last know position of the mouse when dragging the scrollbar.
         * <p/>
         * It is used to calculate the amount of scroll on each mouse movement.
         */
        private Point prevPoint = null;

        /**
         * @see MouseAdapter#mousePressed(MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            prevPoint = e.getPoint();
        }

        /**
         * @see MouseAdapter#mouseReleased(MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            prevPoint = null;
        }

        /**
         * @see MouseAdapter#mouseDragged(MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (prevPoint != null) {
                switch (scrollBar.getOrientation()) {

                    case JScrollBar.VERTICAL:
                        int diffVerticalValue = diffPixelsToDiffModelValue(e.getY() - prevPoint.y);
                        scrollBar.getModel().setValue(scrollBar.getModel().getValue() + diffVerticalValue);
                        break;

                    case JScrollBar.HORIZONTAL:
                        int diffHorizontalValue = diffPixelsToDiffModelValue(e.getX() - prevPoint.x);
                        scrollBar.getModel().setValue(scrollBar.getModel().getValue() + diffHorizontalValue);
                        break;

                    default:
                        LOGGER.error("Invalid orientation: " + scrollBar.getOrientation());
                }
            }
            prevPoint = e.getPoint();
        }
    }

    /**
     * Entity responsibility of making the scrollbar visible/invisible.
     * <p/>
     * (1) The scrollbar should be visible on rollover and invisible when no rollover.
     * <p/>
     * (2) The scrollbar should visible when being dragged even when the mouse is no longer on the scrollbar.
     * <p/>
     * (3) The scrollbar should not be visible on rollover if another scrollbar is being dragged.
     * <p/>
     * (4) The scrollbar should become visible when the content is scrolled.
     */
    private static class VisibilityAdapter implements ComponentListener, MouseListener, MouseMotionListener,
            PropertyChangeListener, ChangeListener, ActionListener, TimingTarget {

        private static final float MIN_ALPHA = 0.0f;
        private static final float MAX_ALPHA = 1.0f;
        private static final float FADE_IN_MAX_DURATION = 125; // ms
        private static final float FADE_OUT_MAX_DURATION = 300; // ms
        private static final int FADE_OUT_DELAY = 1500; // ms

        private static Component sharedScrollingBar = null;

        private final JScrollBar scrollBar;

        private int visibleRequestCounter = 0;

        private float initialAlpha = MIN_ALPHA;
        private float targetAlpha = MAX_ALPHA;
        private float currentAlpha = MIN_ALPHA;

        private Animator animator = null;

        private final Timer fadeOutDelayTimer = new Timer(FADE_OUT_DELAY, this);

        public VisibilityAdapter(JScrollBar scrollBar) {
            this.scrollBar = scrollBar;
            TimingSource ts = new SwingTimerTimingSource();
            Animator.setDefaultTimingSource(ts);
            ts.init();
        }

        /**
         * @see ComponentListener#componentShown(ComponentEvent)
         */
        @Override
        public void componentShown(ComponentEvent e) {
            requestVisible(true);
            requestVisible(false);
        }

        /**
         * @see ComponentListener#componentHidden(ComponentEvent)
         */
        @Override
        public void componentHidden(ComponentEvent e) {
            visibleRequestCounter = 0;
        }

        /**
         * @see ComponentListener#componentResized(ComponentEvent)
         */
        @Override
        public void componentResized(ComponentEvent e) {
            // Nothing to be done
        }

        /**
         * @see ComponentListener#componentMoved(ComponentEvent)
         */
        @Override
        public void componentMoved(ComponentEvent e) {
            // Nothing to be done
        }

        /**
         * @see MouseListener#mouseEntered(MouseEvent)
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            if ((sharedScrollingBar == null) || (sharedScrollingBar.equals(e.getComponent()))) { // (3)
                requestVisible(true); // (1)
            }
        }

        /**
         * @see MouseListener#mouseExited(MouseEvent)
         */
        @Override
        public void mouseExited(MouseEvent e) {
            if ((sharedScrollingBar == null) || (sharedScrollingBar.equals(e.getComponent()))) { // (3)
                requestVisible(false); // (1)
            }
        }

        /**
         * @see MouseListener#mousePressed(MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            sharedScrollingBar = scrollBar; // (3)
            requestVisible(true); // (2)
        }

        /**
         * @see MouseListener#mouseReleased(MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            sharedScrollingBar = null; // (3)
            requestVisible(false); // (2)
        }

        /**
         * @see MouseListener#mouseClicked(MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // Nothing to be done
        }

        /**
         * @see MouseMotionListener#mouseMoved(MouseEvent)
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            if (visibleRequestCounter == 0) {
                // This can happen after dragging another scrollbar, releasing on this scrollbar and moving the mouse
                requestVisible(true); // (1) because of (3): Simulate a mouse entered
            }
        }

        /**
         * @see MouseMotionListener#mouseDragged(MouseEvent)
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            mouseMoved(e); // (1) because of (3)
        }

        /**
         * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if ("model".equals(event.getPropertyName())) {
                // (4) Model replaced, so hook to the new model
                ((BoundedRangeModel) event.getOldValue()).removeChangeListener(this);
                ((BoundedRangeModel) event.getNewValue()).addChangeListener(this);
                requestVisible(true); // (4)
                requestVisible(false);
            }
        }

        /**
         * @see ChangeListener#stateChanged(ChangeEvent)
         */
        @Override
        public void stateChanged(ChangeEvent event) {
            // Model value changed
            requestVisible(true); // (4)
            requestVisible(false);
        }

        private void requestVisible(boolean visible) {
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
            if (fadeOutDelayTimer.isRunning()) {
                fadeOutDelayTimer.stop();
            }

            // Stop previous animation
            if ((animator != null) && (animator.isRunning())) {
                animator.removeTarget(this);
                animator.stop();
            }

            // Start new animation from where the previous one stopped
            initialAlpha = currentAlpha;
            targetAlpha = MAX_ALPHA;
            long duration = (long) ((MAX_ALPHA - initialAlpha) * FADE_IN_MAX_DURATION / (MAX_ALPHA - MIN_ALPHA));
            if (duration <= 0) {
                timingEvent(null, 1.0);
            } else {
                animator = new Animator.Builder().setDuration(duration, TimeUnit.MILLISECONDS).setInterpolator(new
                        SplineInterpolator(0.8, 0.2, 0.2, 0.8)).addTarget(this).build();
                animator.start();
            }
        }

        private void fadeOut() {
            if (!fadeOutDelayTimer.isRunning()) {
                fadeOutDelayTimer.start();
            }
        }

        private void doFadeOut() {
            // Stop previous animation
            if ((animator != null) && (animator.isRunning())) {
                animator.removeTarget(this);
                animator.stop();
            }

            // Start new animation from where the previous one stopped
            initialAlpha = currentAlpha;
            targetAlpha = MIN_ALPHA;
            long duration = (long) ((initialAlpha - MIN_ALPHA) * FADE_OUT_MAX_DURATION / (MAX_ALPHA - MIN_ALPHA));
            if (duration <= 0) {
                timingEvent(null, 1.0);
            } else {
                animator = new Animator.Builder().setDuration(duration, TimeUnit.MILLISECONDS).setInterpolator(new
                        SplineInterpolator(0.8, 0.2, 0.2, 0.8)).addTarget(this).build();
                animator.start();
            }
        }

        /**
         * @see ActionListener#actionPerformed(ActionEvent)
         * @see #fadeOutDelayTimer
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            doFadeOut();
        }

        /**
         * @see TimingTarget#begin(Animator)
         */
        @Override
        public void begin(Animator animator) {
            // Nothing to be done because we stop the animation manually
        }

        /**
         * @see TimingTarget#end(Animator)
         */
        @Override
        public void end(Animator animator) {
            // Nothing to be done because we stop the animation manually
        }

        /**
         * @see TimingTarget#repeat(Animator)
         */
        @Override
        public void repeat(Animator animator) {
            // Nothing to be done
        }

        /**
         * @see TimingTarget#reverse(Animator)
         */
        @Override
        public void reverse(Animator animator) {
            // Nothing to be done
        }

        /**
         * @see TimingTarget#timingEvent(Animator, double)
         */
        @Override
        public void timingEvent(Animator animator, double v) {
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

    private final MouseControlAdapter mouseControlAdapter = new MouseControlAdapter();

    /**
     * Creates a UI for the specified component.<br>This method is called by the {@link javax.swing.UIManager} via the
     * {@link javax.swing.UIDefaults}.
     *
     * @param c Scrollbar to create the UI for.
     *
     * @return UI for the specified component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LeanScrollBarUI();
    }

    /**
     * Protected default constructor.
     *
     * @see #createUI(JComponent)
     */
    protected LeanScrollBarUI() {
        super();
    }

    public void installUI(JComponent c) {
        if (c instanceof JScrollBar) {
            scrollBar = (JScrollBar) c;
            visibilityAdapter = new VisibilityAdapter(scrollBar);

            installDefaults();
//            installComponents();
            installListeners();
//            installKeyboardActions();
        }
    }

    public void uninstallUI(JComponent c) {
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
        scrollBar.addMouseListener(mouseControlAdapter);
        scrollBar.addMouseMotionListener(mouseControlAdapter);

        scrollBar.addMouseListener(visibilityAdapter);
        scrollBar.addMouseMotionListener(visibilityAdapter);
        scrollBar.addComponentListener(visibilityAdapter);

        scrollBar.addPropertyChangeListener(visibilityAdapter);
        scrollBar.getModel().addChangeListener(visibilityAdapter);

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
        scrollBar.removeMouseListener(mouseControlAdapter);
        scrollBar.removeMouseMotionListener(mouseControlAdapter);

        scrollBar.removeMouseListener(visibilityAdapter);
        scrollBar.removeComponentListener(visibilityAdapter);

        scrollBar.removePropertyChangeListener(visibilityAdapter);
        scrollBar.getModel().removeChangeListener(visibilityAdapter);
    }

    /**
     * @see ScrollBarUI#getMinimumSize(JComponent)
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        Dimension size;
        Insets insets = scrollBar.getInsets();

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
     * @see ScrollBarUI#getPreferredSize(JComponent)
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension size;
        Insets insets = scrollBar.getInsets();

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
     * @see ScrollBarUI#paint(Graphics, JComponent)
     */
    @Override
    public void paint(Graphics g, JComponent c) {

        // Paint in image buffer because alpha composite does not seem to work as expected on the given Graphics
        BufferedImage buffer = new BufferedImage(scrollBar.getWidth(), scrollBar.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(BASE_COLOR.getRed(), BASE_COLOR.getGreen(), BASE_COLOR.getBlue(),
                (int) (visibilityAdapter.currentAlpha * 255)));

        int extentInPixels = getExtentInPixels();
        int valueInPixels = getValueInPixels();

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
        int maxExtentInPixels;

        Insets insets = scrollBar.getInsets();

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
        int extentInPixels;
        BoundedRangeModel model = scrollBar.getModel();

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
        int valueInPixels;
        BoundedRangeModel model = scrollBar.getModel();

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

    private int diffPixelsToDiffModelValue(int diffValueInPixels) {
        int modelValue;
        BoundedRangeModel model = scrollBar.getModel();

        modelValue = diffValueInPixels * (model.getMaximum() - model.getMinimum() - model.getExtent()) /
                (getMaxExtentInPixels() - getExtentInPixels());

        return modelValue;
    }

    private void paintVertical(Graphics2D g2d, int valueInPixels, int extentInPixels) {
        Insets insets = scrollBar.getInsets();
        int barX = insets.left;
        int barY = insets.top + valueInPixels;

        // Paint head
        g2d.fillOval(barX, barY, THICKNESS, THICKNESS);

        // Paint tail
        g2d.fillOval(barX, barY + extentInPixels - THICKNESS, THICKNESS, THICKNESS);

        // Paint trunk
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g2d.fillRect(barX, barY + THICKNESS / 2, THICKNESS, extentInPixels - THICKNESS / 2 - THICKNESS / 2);
    }

    private void paintHorizontal(Graphics2D g2d, int valueInPixels, int extentInPixels) {
        Insets insets = scrollBar.getInsets();
        int barX = insets.left + valueInPixels;
        int barY = insets.top;

        // Paint head
        g2d.fillOval(barX, barY, THICKNESS, THICKNESS);

        // Paint tail
        g2d.fillOval(barX + extentInPixels - THICKNESS, barY, THICKNESS, THICKNESS);

        // Paint trunk
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g2d.fillRect(barX + THICKNESS / 2, barY, extentInPixels - THICKNESS / 2 - THICKNESS / 2, THICKNESS);
    }
}
