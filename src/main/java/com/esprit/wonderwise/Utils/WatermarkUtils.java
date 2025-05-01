package com.esprit.wonderwise.Utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WatermarkUtils {
    /**
     * Applies a semi-transparent, diagonal, and repeated watermark text with alternating colors across the entire image and saves it to destFile.
     * @param inputFile The source image file
     * @param destFile The destination file to save the watermarked image
     * @param watermarkText The text to use as the watermark
     * @throws IOException If file operations fail
     */
    public static void applyTextWatermark(File inputFile, File destFile, String watermarkText) throws IOException {
        BufferedImage sourceImage = ImageIO.read(inputFile);
        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();

        // Set rendering hints for better text quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set watermark properties
        float opacity = 0.3f; // Lower opacity for subtle effect
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
        g2d.setComposite(alphaChannel);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 40)); // Smaller font for repetition

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(watermarkText);
        int stringHeight = fontMetrics.getAscent();

        // Define two distinct colors for alternating
        Color color1 = Color.PINK;  // First color
        Color color2 = Color.LIGHT_GRAY; // Second color

        // Rotate the text 45 degrees
        g2d.rotate(Math.toRadians(45));

        // Calculate spacing for tiling
        int spacingX = stringWidth + 50; // Horizontal spacing between text
        int spacingY = stringHeight + 50; // Vertical spacing between text
        int imageWidth = sourceImage.getWidth();
        int imageHeight = sourceImage.getHeight();

        // Tile the watermark across the image with alternating colors
        boolean useColor1 = true; // Toggle between colors
        for (int x = -imageHeight; x < imageWidth + imageHeight; x += spacingX) {
            useColor1 = !useColor1; // Alternate colors for each row
            for (int y = -imageHeight; y < imageHeight + imageWidth; y += spacingY) {
                g2d.setColor(useColor1 ? color1 : color2); // Switch between colors
                g2d.drawString(watermarkText, x, y);
            }
        }

        g2d.dispose();
        System.out.println("[DEBUG] Diagonal repeating watermark with alternating colors (red and blue) applied.");
        ImageIO.write(sourceImage, getFileExtension(destFile), destFile);
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "png";
        return name.substring(dotIndex + 1).toLowerCase();
    }
}