package com.zombicide.missiongen.model.helpers;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.zombicide.missiongen.config.ConfigLoader;

public class ImageOperations {
    

    public static Image rotateImage(Image img, int angleDegrees) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth(null);
        int h = img.getHeight(null);

        java.awt.image.BufferedImage newImage = new java.awt.image.BufferedImage(w, h,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = newImage.createGraphics();

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angleDegrees), w / 2.0, h / 2.0);

        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    public static Image rotateImageBy90Degrees(Image img) {
      return rotateImage(img, 90);
    }

    public static Image loadTileImage(String tileId) throws IOException {
        String folderEdtions = ConfigLoader.getInstance().getProperty("folders.editions");
        String tileImages = ConfigLoader.getInstance().getProperty("folders.tileImages");
        String[] parts = tileId.split("\\.");
        String edition = parts[0];
        String collection = parts[1];
        String fileName = parts[2];
        String imagePath =  folderEdtions + "/" + edition + "/" + collection + "/" + tileImages + "/" + fileName + ".png";
        return ImageIO.read(new File(imagePath));
    }

    public static Image copyImage(Image img) {
        if (img == null) {
            return null;
        }

        int w = img.getWidth(null);
        int h = img.getHeight(null);

        java.awt.image.BufferedImage newImage = new java.awt.image.BufferedImage(w, h,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return newImage;
    }
}
