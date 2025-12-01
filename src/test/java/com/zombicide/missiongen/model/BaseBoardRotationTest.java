package com.zombicide.missiongen.model;

import static org.junit.Assert.assertEquals;
import java.awt.Point;
import java.awt.image.BufferedImage;
import org.junit.Test;

import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.board.TileBoard;

public class BaseBoardRotationTest {

    @Test
    public void testRotateBoardAreas() {
        // Create a dummy image
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

        // Create a TileBoard (100x100)
        TileBoard board = new TileBoard("test-board-id", image, 100);

        // Add an area at (10, 20) with size 30x40
        // Top-Left: (10, 20)
        // Bottom-Right: (40, 60)
        BoardArea area = new BoardArea(java.util.UUID.randomUUID(), new Point(10, 20), 30, 40);
        board.addArea(area);

        // Rotate 90 degrees clockwise
        board.rotate();

        // Expected:
        // newX = 100 - (20 + 40) = 40
        // newY = 10
        // newWidth = 40
        // newHeight = 30

        assertEquals("X coordinate should be 40", 40, area.getTopLeft().x);
        assertEquals("Y coordinate should be 10", 10, area.getTopLeft().y);
        assertEquals("Width should be 40", 40, area.getWidth());
        assertEquals("Height should be 30", 30, area.getHeight());

        // Rotate again (180 degrees total)
        board.rotate();

        // Old (current): x=40, y=10, w=40, h=30
        // newX = 100 - (10 + 30) = 60
        // newY = 40
        // newWidth = 30
        // newHeight = 40

        assertEquals("X coordinate should be 60", 60, area.getTopLeft().x);
        assertEquals("Y coordinate should be 40", 40, area.getTopLeft().y);
        assertEquals("Width should be 30", 30, area.getWidth());
        assertEquals("Height should be 40", 40, area.getHeight());
    }
}
