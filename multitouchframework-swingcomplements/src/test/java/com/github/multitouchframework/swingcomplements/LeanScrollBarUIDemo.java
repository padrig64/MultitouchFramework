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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;

public class LeanScrollBarUIDemo extends JFrame {

    private static final long serialVersionUID = 1728612871445898398L;

    public LeanScrollBarUIDemo() throws HeadlessException {
        super();
        init();
    }

    private void init() {
        setTitle("Scroll Demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Set content pane
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        final JLabel scrollableComponent = new JLabel("Scrollable area");
        scrollableComponent.setHorizontalAlignment(JLabel.CENTER);
        scrollableComponent.setFont(scrollableComponent.getFont().deriveFont(25.0f).deriveFont(Font.BOLD));
        scrollableComponent.setBorder(new LineBorder(Color.GREEN));
        scrollableComponent.setPreferredSize(new Dimension(300, 300));
        contentPane.add(new JScrollPane(scrollableComponent), BorderLayout.CENTER);
        setContentPane(contentPane);

        // Set size
        pack();
        setSize(new Dimension(300, 300));

        // Set location
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 3);
    }

    public static void main(final String... args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Set look-and-feel
                try {
                    for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            UIManager.put("ScrollBarUI", LeanScrollBarUI.class.getCanonicalName());
                            break;
                        }
                    }
                } catch (UnsupportedLookAndFeelException e) {
                    // handle exception
                } catch (ClassNotFoundException e) {
                    // handle exception
                } catch (InstantiationException e) {
                    // handle exception
                } catch (IllegalAccessException e) {
                    // handle exception
                }

                // Show window
                new LeanScrollBarUIDemo().setVisible(true);
            }
        });
    }
}
