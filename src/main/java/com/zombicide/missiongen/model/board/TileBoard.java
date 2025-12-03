package com.zombicide.missiongen.model.board;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.zombicide.missiongen.config.ConfigLoader;

public class TileBoard extends BaseBoard {

    public TileBoard(String boardId, Image backgroundImage, int width,String imagePath) {
        super(boardId, backgroundImage, width, width, imagePath);
    }

    public TileBoard(TileBoard boardToCopy) {
        super(boardToCopy);
    }

    public static TileBoard createEmptyBoard(String edition, String collection) {
        int tileWidth = ConfigLoader.getInstance().getPropertyAsInt("tile.width");
        // carga la imagen de fondo desde classparh resources
        Image backgroundImage;
        try {
            backgroundImage = ImageIO.read(TileBoard.class.getResource("/images/no_tile.png"));
        } catch (IOException e) {
            backgroundImage = new BufferedImage(tileWidth, tileWidth, BufferedImage.TYPE_INT_ARGB);
            e.printStackTrace();
        }
        return new TileBoard(UUID.randomUUID().toString(), backgroundImage, tileWidth,"/images/no_tile.png");
    }
}
