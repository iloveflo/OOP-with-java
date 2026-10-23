package GUI.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageUtil {

    public static ImageIcon getScaledImage(String path, int targetWidth, int targetHeight) {
        if (path != null && !path.trim().isEmpty()) {
            File file = new File(path);
            if (!file.exists()) {
                // Try relative to workspace
                file = new File("images", new File(path).getName());
            }
            if (file.exists() && file.isFile()) {
                try {
                    BufferedImage original = ImageIO.read(file);
                    if (original != null) {
                        return createSmoothScaledIcon(original, targetWidth, targetHeight);
                    }
                } catch (IOException ignored) {
                }
            }
        }
        return createPlaceholder(targetWidth, targetHeight, "No Image");
    }

    private static ImageIcon createSmoothScaledIcon(BufferedImage src, int targetWidth, int targetHeight) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        // Calculate aspect-ratio scale
        double scale = Math.min((double) targetWidth / srcWidth, (double) targetHeight / srcHeight);
        int scaledW = Math.max(1, (int) (srcWidth * scale));
        int scaledH = Math.max(1, (int) (srcHeight * scale));

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(new Color(241, 245, 249)); // Slate-100
        g2.fillRoundRect(0, 0, targetWidth, targetHeight, 8, 8);

        // Center draw
        int x = (targetWidth - scaledW) / 2;
        int y = (targetHeight - scaledH) / 2;
        g2.drawImage(src, x, y, scaledW, scaledH, null);
        g2.dispose();

        return new ImageIcon(scaledImage);
    }

    public static ImageIcon createPlaceholder(int width, int height, String text) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(241, 245, 249));
        g2.fillRoundRect(0, 0, width, height, 8, 8);

        g2.setColor(new Color(203, 213, 225));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);

        g2.setColor(new Color(148, 163, 184));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, Math.max(10, width / 10)));
        FontMetrics fm = g2.getFontMetrics();
        int strW = fm.stringWidth(text);
        int strH = fm.getAscent();
        g2.drawString(text, (width - strW) / 2, (height + strH) / 2 - 2);

        g2.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createCaptchaImage(String code, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(238, 242, 246));
        g2.fillRect(0, 0, width, height);

        // Noise lines
        g2.setColor(new Color(180, 200, 220));
        for (int i = 0; i < 6; i++) {
            int x1 = (int) (Math.random() * width);
            int y1 = (int) (Math.random() * height);
            int x2 = (int) (Math.random() * width);
            int y2 = (int) (Math.random() * height);
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw letters
        g2.setFont(new Font("Courier New", Font.BOLD, 22));
        g2.setColor(new Color(30, 41, 59));
        int charW = width / (code.length() + 1);
        for (int i = 0; i < code.length(); i++) {
            int x = (i + 1) * charW - 6;
            int y = height / 2 + 7 + (int) ((Math.random() - 0.5) * 8);
            g2.drawString(String.valueOf(code.charAt(i)), x, y);
        }
        g2.dispose();
        return new ImageIcon(img);
    }

    public static String copyImageToProject(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }
        File destDir = new File("images");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName().replaceAll("\\s+", "_");
        File destFile = new File(destDir, fileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return "images/" + fileName;
    }
}
