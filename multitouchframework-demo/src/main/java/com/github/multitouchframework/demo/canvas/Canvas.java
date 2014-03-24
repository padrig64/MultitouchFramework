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

package com.github.multitouchframework.demo.canvas;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Canvas extends JComponent {

    /**
     * Generated serial UID.
     */
    private static final long serialVersionUID = -7342433026335755329L;

    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private final List<CanvasLayer> layers = new ArrayList<CanvasLayer>();

    private final Set<CanvasLayer> visibleLayers = new HashSet<CanvasLayer>();

    public Canvas() {
        super();
        setName("Canvas");
    }

    public void addLayer(CanvasLayer layer) {
        layers.add(layer);
        visibleLayers.add(layer);
        repaint();
    }

    public void removeLayer(CanvasLayer layer) {
        visibleLayers.remove(layer);
        layers.remove(layer);
        repaint();
    }

    public boolean isLayerVisible(CanvasLayer layer) {
        return visibleLayers.contains(layer);
    }

    public void setLayerVisible(CanvasLayer layer, boolean visible) {
        if (visible) {
            visibleLayers.add(layer);
        } else {
            visibleLayers.remove(layer);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Insets insets = getInsets();
        int contentWidth = getWidth() - insets.left - insets.right;
        int contentHeight = getHeight() - insets.top - insets.bottom;

        // Clear background
        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(insets.left, insets.top, contentWidth, contentHeight);

        // Set anti-aliasing
        Graphics2D g2d = (Graphics2D) graphics.create(insets.left, insets.top, contentWidth, contentHeight);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw all visible layers
        for (CanvasLayer layer : layers) {
            if (visibleLayers.contains(layer)) {
                layer.paint(g2d);
            }
        }

        g2d.dispose();
    }
}
