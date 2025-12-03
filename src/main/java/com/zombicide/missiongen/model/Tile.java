package com.zombicide.missiongen.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.DTO.BoardAreaConnectionDTO;
import com.zombicide.missiongen.DTO.BoardAreaDTO;
import com.zombicide.missiongen.DTO.TileDTO;
import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.board.TileBoard;

public class Tile {

    String edition;
    String collection;
    String imagePath;
    TileBoard tileBoard;
    String tileName;

    private static final Logger logger = LoggerFactory.getLogger(Tile.class);
    private ConfigLoader configLoader;

    public Tile(String edition, String collection, String imagePath, String tileName) {
        this.edition = edition;
        this.collection = collection;
        this.tileName = tileName;
        this.imagePath = imagePath;
        this.configLoader = ConfigLoader.getInstance();

        this.initBoard();
    }

    private void initBoard() {
        Image backgroundImage;
        String tileId = edition + "." + collection + "." + tileName;
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
            this.tileBoard = new TileBoard(tileId, backgroundImage, 
                    Integer.parseInt(configLoader.getProperty("tile.width")), imagePath);
        } catch (IOException e) {
            logger.error("Error reading image: {}", imagePath, e);
            return;
        }
    }

    public TileBoard getBoard() {
        return tileBoard;
    }

    public String getEdition() {
        return edition;
    }

    public String getCollection() {
        return collection;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTileName() {
        return tileName;
    }

    public static Tile fromTileDTO(TileDTO tileDTO) {
        Tile tile = new Tile(tileDTO.edition, tileDTO.collection, tileDTO.imagePath, tileDTO.tileName);
        for (BoardAreaDTO areaDTO : tileDTO.areas) {
            tile.getBoard().addArea(BoardArea.fromBoardAreaDTO(areaDTO));
        }
        for (BoardAreaConnectionDTO connectionDTO : tileDTO.connections) {
            tile.getBoard().addConnection(BoardAreaConnection.fromAreaConnectionDTO(connectionDTO));
        }
        return tile;
    }

}
