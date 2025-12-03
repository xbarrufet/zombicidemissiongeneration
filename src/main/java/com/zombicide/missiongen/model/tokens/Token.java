package com.zombicide.missiongen.model.tokens;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

import com.zombicide.missiongen.config.TokenLoader;

public abstract class Token {

    UUID id;
    Point location; // The location of the center of the token in the are
    TokenType type; // The type of the token
    TokenShape shape; // The shape of the token
    Image image; // The image of the token
    TokenOrientation orientation; // The orientation of the token
    UUID areaId; // The id of the area the token is in
    String subtype; // The subtype of the token
    int rotation;

    public Token(TokenType type, TokenShape shape, TokenOrientation orientation,
            String subtype, Image image) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.shape = shape;
        this.subtype = subtype;
        this.orientation = orientation;
        this.image = image;
        this.rotation = 0;
    }

    public void setLocation(Point location, UUID areaId) {
        this.location = location;
        this.areaId = areaId;
    }

    public Point getLocation() {
        return location;
    }

    public UUID getAreaId() {
        return areaId;
    }

    public TokenType getType() {
        return type;
    }

    public TokenShape getShape() {
        return shape;
    }

    public TokenOrientation getOrientation() {
        return orientation;
    }

    public UUID getId() {
        return id;
    }

    public String getSubtype() {
        return subtype;
    }

    public Image getImage() {
        return image;
    }

    public int getRotation() {
        return rotation;
    }

    public void rotate() {
        if (this.orientation == TokenOrientation.Horizontal) {
            this.orientation = TokenOrientation.Vertical;
        } else {
            this.orientation = TokenOrientation.Horizontal;
        }
        this.rotation = (this.rotation + 90) % 360;

        this.getShape().rotate();
        this.image = rotateImage();
    }

    // rotate the image of the token 90 degrees clockwise
    private Image rotateImage() {
        BufferedImage originalImage = toBufferedImage(this.image);
        // 1. Get original dimensions
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 2. Create the new BufferedImage with swapped dimensions
        // The width of the new image is the height of the original, and vice-versa.
        BufferedImage rotatedImage = new BufferedImage(height, width, originalImage.getType());

        // 3. Get the Graphics2D context for drawing onto the new image
        Graphics2D g2d = rotatedImage.createGraphics();

        // 4. Create the rotation transformation
        AffineTransform transform = new AffineTransform();

        // Step 4a: Rotate 90 degrees (Math.PI / 2 radians)
        // The rotation is centered at (0, 0) for the coordinate system.
        transform.rotate(Math.toRadians(90));

        // Step 4b: Translate the image to the correct position
        // After rotating 90 clockwise, the original image's top-left corner (0,0)
        // moves to (new_width, 0), which is (height, 0).
        // To bring it into the visible area of the new image, we translate by `height`
        // in the X direction (or new image's width).
        transform.translate(0, -width); // The correct translation is often a bit tricky.
        // If we use the rotation center at (0,0), a 90-degree clockwise rotation
        // moves the original point (x, y) to (y, -x).
        // The image now has a negative y-coordinate. We must translate it back.
        // A simpler approach is:
        g2d.translate(height, 0); // Move origin to top-right corner of the new canvas
        g2d.rotate(Math.toRadians(90)); // Rotate 90 clockwise

        // 5. Draw the original image onto the new image using the transformation
        g2d.drawImage(originalImage, 0, 0, null);

        // 6. Dispose the Graphics2D context
        g2d.dispose();

        return rotatedImage;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    public boolean isPointInShape(Point point) {
        return this.shape.isPointInShape(this.location, point);
    }
}
