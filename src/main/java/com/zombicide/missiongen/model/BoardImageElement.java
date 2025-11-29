package com.zombicide.missiongen.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BoardImageElement extends BoardElement {

    private Image image;

    public BoardImageElement(int id, Point origin, String type, Image image, int width, int height) {
        super(id, origin, type, width, height);
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    /**
     * Rotates the image by 90 degrees clockwise.
     * Updates the image and swaps the width and height dimensions.
     */
    public void rotateImage() {
        int currentWidth = image.getWidth(null);
        int currentHeight = image.getHeight(null);

        // Create a new BufferedImage with swapped dimensions
        BufferedImage rotatedImage = new BufferedImage(currentHeight, currentWidth, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotatedImage.createGraphics();

        // Create rotation transform (90 degrees clockwise)
        AffineTransform transform = new AffineTransform();
        transform.translate(currentHeight, 0);
        transform.rotate(Math.PI / 2);

        // Draw the rotated image
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        // Update the image and swap dimensions in parent class
        this.image = rotatedImage;
        this.width = currentHeight;
        this.height = currentWidth;
    }

}
