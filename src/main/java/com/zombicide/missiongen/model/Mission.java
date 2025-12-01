package com.zombicide.missiongen.model;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.DTO.BoardAreaConnectionDTO;
import com.zombicide.missiongen.DTO.BoardAreaDTO;
import com.zombicide.missiongen.DTO.MissionDTO;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardAreaConnection;
import com.zombicide.missiongen.model.board.MissionBoard;

public class Mission {

    private int rows;
    private int cols;
    private int width;
    private int height;
    private String edition;
    private String collection;
    private String imagePath;
    private String missionName;
    private MissionBoard missionBoard;

    private static final Logger logger = LoggerFactory.getLogger(Mission.class);

    public Mission(int rows, int cols, int width, int height, String edition, String collection,
            String imagePath, String missionName, MissionBoard missionBoard) {
        this.rows = rows;
        this.cols = cols;
        this.width = width;
        this.height = height;
        this.edition = edition;
        this.collection = collection;
        this.imagePath = imagePath;
        this.missionName = missionName;
        this.missionBoard = missionBoard;
        initBoard();
    }

    private void initBoard() {
        String missionBoardId = edition + "." + collection + "." + missionName;
        Image backgroundImage;
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
            this.missionBoard = new MissionBoard(missionBoardId, backgroundImage,
                    width,
                    height);
        } catch (IOException e) {
            logger.error("Error reading image: {}", imagePath, e);
            return;
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public String getMissionName() {
        return missionName;
    }

    public MissionBoard getMissionBoard() {
        return missionBoard;
    }

    public static Mission fromMissionDTO(MissionDTO missionDTO) {
        // Create an empty MissionBoard (no image will be loaded from DTO)
        Mission mission = new Mission(missionDTO.rows, missionDTO.cols, missionDTO.width, missionDTO.height,
                missionDTO.edition, missionDTO.collection, missionDTO.imagePath, missionDTO.missionName, null);

        // Add areas from DTO
        if (missionDTO.areas != null) {
            for (BoardAreaDTO areaDTO : missionDTO.areas) {
                mission.getMissionBoard().addArea(BoardArea.fromBoardAreaDTO(areaDTO));
            }
        }
        // Add connections from DTO
        if (missionDTO.connections != null) {
            for (BoardAreaConnectionDTO connectionDTO : missionDTO.connections) {
                mission.getMissionBoard().addConnection(BoardAreaConnection.fromAreaConnectionDTO(connectionDTO));
            }
        }

        return mission;
    }
}
