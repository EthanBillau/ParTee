package com.project.golf.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * AccountBackgroundPanel.java
 *
 * Background panel for AccountOptionsGUI.
 * Keeps the image aspect ratio, shows the whole image ("contain"),
 * and chooses a preferred size based on children + image ratio.
 */
public class AccountBackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;

    // Uses the same image as BackgroundPanel
    private static final String CLASSPATH_IMAGE =
            "/com/project/golf/gui/backgroundDarkest.jpg";

    private static final String FILE_IMAGE_1 =
            "com/project/golf/gui/backgroundDarkest.jpg";
    private static final String FILE_IMAGE_2 =
            "backgroundDarkest.jpg";

    public AccountBackgroundPanel() {
        this(null);
    }

    public AccountBackgroundPanel(LayoutManager layout) {
        super(layout);
        loadBackgroundImage();
        setOpaque(true);
        setBackground(Color.BLACK); // fallback if image missing
    }

    private void loadBackgroundImage() {
        try {
            // 1) Try classpath
            URL url = getClass().getResource(CLASSPATH_IMAGE);
            if (url != null) {
                backgroundImage = ImageIO.read(url);
                return;
            }

            // 2) Fallback: file paths
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

            System.err.println("Account background image not found: "
                    + CLASSPATH_IMAGE + " / " + FILE_IMAGE_1 + " / " + FILE_IMAGE_2);
        } catch (IOException e) {
            System.err.println("Error loading account background image: " + e.getMessage());
        }
    }

    /**
     * Preferred size = the smallest scaled version of the image
     * that is large enough to contain all child components.
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension childPref = super.getPreferredSize();

        if (backgroundImage == null) {
            return childPref;
        }

        int imgW = backgroundImage.getWidth();
        int imgH = backgroundImage.getHeight();

        // No children yet → just use raw image size
        if (childPref.width == 0 && childPref.height == 0) {
            return new Dimension(imgW, imgH);
        }

        int minW = childPref.width;
        int minH = childPref.height;

        // Scale so the scaled image is >= child size in both directions
        double scale = Math.max(minW / (double) imgW,
                                minH / (double) imgH);

        // If children are smaller than the image, don't shrink the image below 1x
        if (scale < 1.0) {
            scale = 1.0;
        }

        int scaledW = (int) Math.ceil(imgW * scale);
        int scaledH = (int) Math.ceil(imgH * scale);

        return new Dimension(scaledW, scaledH);
    }

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

        // "CONTAIN" behavior: whole image visible, aspect ratio preserved
        double scale = Math.min(panelW / (double) imgW,
                                panelH / (double) imgH);

        int drawW = (int) Math.round(imgW * scale);
        int drawH = (int) Math.round(imgH * scale);

        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g2d.drawImage(backgroundImage, x, y, drawW, drawH, this);
        g2d.dispose();
    }
}
