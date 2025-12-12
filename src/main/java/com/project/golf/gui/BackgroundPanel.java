package com.project.golf.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * BackgroundPanel.java
 *
 * Custom JPanel with full-coverage scaled background image rendering.
 * Loads from classpath resources with filesystem fallbacks for compatibility.
 *
 * Data structures: BufferedImage for background, static String constants
 * for resource paths (classpath and filesystem alternatives).
 * Algorithm: Image loading with fallback chain (classpath → filesystem paths),
 * scaling with aspect ratio preservation, high-quality rendering hints.
 * Features: Custom background image rendering, transparent overlay support,
 * aspect ratio preservation, multi-source image loading, IDE compatibility.
 *
 * @author Ethan Billau (ebillau), L15
 *
 * @version December 7, 2025
 */

public class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;  // loaded background image for display

    // Classpath location for background image resource
    private static final String CLASSPATH_IMAGE =
            "/com/project/golf/gui/backgroundDarkest.jpg";

    // Filesystem fallback paths for background image loading
    private static final String FILE_IMAGE_1 =
            "com/project/golf/gui/backgroundDarkest.jpg";
    private static final String FILE_IMAGE_2 =
            "backgroundDarkest.jpg";

    public BackgroundPanel() {
        super();
        loadBackgroundImage();
        setOpaque(true);
        setBackground(Color.BLACK);
    }

    public BackgroundPanel(LayoutManager layout) {
        super(layout);
        loadBackgroundImage();
        setOpaque(true);
        setBackground(Color.BLACK); // fallback if image missing
    }

    private void loadBackgroundImage() {
        try {
            // 1) Try to load from classpath
            URL url = getClass().getResource(CLASSPATH_IMAGE);
            if (url != null) {
                backgroundImage = ImageIO.read(url);
                return;
            }

            // 2) Fallback: original file locations
            File f = new File(FILE_IMAGE_1);
            if (f.exists()) {
                backgroundImage = ImageIO.read(f);
                return;
            }

            f = new File(FILE_IMAGE_2);
            if (f.exists()) {
                backgroundImage = ImageIO.read(f);
                return;
            }

            System.err.println("Background image not found: "
                    + CLASSPATH_IMAGE + " / " + FILE_IMAGE_1
                    + " / " + FILE_IMAGE_2);
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    // IMPORTANT: we do NOT override getPreferredSize anymore.
    // That means:
    // - pack() will size the window based on the layout / components,
    //   not based on the background image.
    // - setSize(...) will just work as-is.

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                             RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        int imgW = backgroundImage.getWidth();
        int imgH = backgroundImage.getHeight();
        int panelW = getWidth();
        int panelH = getHeight();

        if (panelW <= 0 || panelH <= 0) {
            g2d.dispose();
            return;
        }

        // "COVER" behavior:
        // Scale the image so it COMPLETELY FILLS the panel,
        // even if that means cropping some edges.
        double scaleX = panelW / (double) imgW;
        double scaleY = panelH / (double) imgH;
        double scale = Math.max(scaleX, scaleY);

        int drawW = (int) Math.round(imgW * scale);
        int drawH = (int) Math.round(imgH * scale);

        // Center the image in the panel
        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g2d.drawImage(backgroundImage, x, y, drawW, drawH, this);
        g2d.dispose();
    }
}
