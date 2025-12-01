package com.zombicide.missiongen.model.board;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.UUID;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.Mission;
import com.zombicide.missiongen.panels.missionLayout.ZoneMissionGridCell;

public class MissionFactoryService {

    public static Mission createMission(String missionId, String edition, String collection, String missionName,
            MissionGrid grid) {

        MissionBoard missionBoard = createMissionBoard(grid);

        String imagePath = ConfigLoader.getInstance().getProperty("folders.editions") + "/"
                + edition + "/" + collection + "/"
                + ConfigLoader.getInstance().getProperty("folders.missionImages") + "/" + "mission_" + missionName
                + ".png";

        return new Mission(grid.getGridWidth(), grid.getGridHeight(),
                missionBoard.getWidth(),
                missionBoard.getHeight(), edition, collection,
                imagePath,
                missionName, missionBoard);
    }

    public static MissionBoard createMissionBoard(int width, int height) {
        MissionGrid grid = new MissionGrid(width, height);
        return createMissionBoard(grid);
    }

    public static MissionBoard createMissionBoard(MissionGrid grid) {
        Image image = createMissionBoardImage(grid);
        int tileWidth = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.width"));
        int tileHeight = Integer.parseInt(ConfigLoader.getInstance().getProperty("tile.height"));
        int missionBoardWidth = grid.getGridWidth() * tileWidth;
        int missionBoardHeight = grid.getGridHeight() * tileHeight;
        String missionBoardId = UUID.randomUUID().toString();
        MissionBoard missionBoard = new MissionBoard(missionBoardId, image, missionBoardWidth, missionBoardHeight);
        missionBoard = addAreasAndConnections(missionBoard, grid,
                tileWidth,
                tileHeight);

        return missionBoard;
    }

    // public Mission createMission(String missionId, String edition, String
    // collection, String missionName,
    // TileBoard[][] gridCells) {
    // MissionGrid grid = new MissionGrid(gridCells);
    // MissionBoard missionBoard = createMissionBoard(grid);

    // String imagePath = ConfigLoader.getInstance().getProperty("folders.editions")
    // + "/"
    // + edition + "/" + collection + "/"
    // + ConfigLoader.getInstance().getProperty("folders.missionImages") + "/" +
    // "mission_" + missionName
    // + ".png";

    // return new Mission(grid.getGridWidth(), grid.getGridHeight(),
    // missionBoard.getWidth(),
    // missionBoard.getHeight(), edition, collection,
    // imagePath,
    // missionName, missionBoard);
    // }

    private static MissionBoard addAreasAndConnections(MissionBoard missionBoard, MissionGrid grid, int tileWidth,
            int tileHeight) {

        grid = renewAreaIds(grid, tileWidth, tileHeight);
        missionBoard = copyAllAreasAndConnections(missionBoard, grid, tileWidth, tileHeight);

        return missionBoard;
    }

    private static MissionBoard copyAllAreasAndConnections(MissionBoard missionBoard, MissionGrid grid,
            int tileWidth,
            int tileHeight) {
        // add all areas and connections to the mission board
        for (int i = 0; i < grid.getGridWidth(); i++) {
            for (int j = 0; j < grid.getGridHeight(); j++) {
                TileBoard board = grid.getBoard(i, j);
                if (board != null) {
                    missionBoard.addAreas(board.getAreas());
                    missionBoard.addConnections(board.getConnections());
                }
            }
        }
        return missionBoard;
    }

    private static MissionGrid renewAreaIds(MissionGrid grid, int tileWidth, int tileHeight) {
        // MissionGridCell[][] newGrid = new
        // MissionGridCell[grid.length][grid[0].length];

        for (int i = 0; i < grid.getGridWidth(); i++) {
            for (int j = 0; j < grid.getGridHeight(); j++) {
                TileBoard board = grid.getBoard(i, j);
                if (board != null) {
                    // Shift areas to the correct position in the mission board
                    board.shiftAreas(i * tileWidth, j * tileHeight);
                }
            }
        }
        return grid;
    }

    // private Map<Integer, List<Integer>>
    // getAllAreasToMerge(ZoneMissionGridCell[][] grid) {
    // Map<Integer, List<Integer>> areasToMerge = new HashMap<>();
    // for (int i = 0; i < grid.length; i++) {
    // for (int j = 0; j < grid[i].length; j++) {
    // ZoneMissionGridCell cell = grid[i][j];
    // if (cell != null && cell.getBoard() != null) {
    // getAreasToMerge(grid, i, j, areasToMerge);
    // }
    // }
    // }
    // return areasToMerge;
    // }

    // private Map<Integer, List<Integer>> getAreasToMerge(ZoneMissionGridCell[][]
    // grid, int x, int y,
    // Map<Integer, List<Integer>> areasToMerge) {
    // // read TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT, BOTTOM_MIDDLE and BOTTOM_LEFT
    // and
    // // check if the adjacent areas exits
    // // if the adjacent area exists, add it to the list

    // //
    // // BOTTOM_LEFT[x,y] adjacents are TOP_LEFT[x,y+1]
    // // prior to visit a corner we must check it has not been visited
    // List<Integer> visitedAreas = new ArrayList<>();
    // for (int i = 0; i < grid.length; i++) {
    // for (int j = 0; j < grid[i].length; j++) {
    // ZoneMissionGridCell cell = grid[i][j];
    // if (cell != null && cell.getBoard() != null) {
    // if (cell.getBoard().hasAreaLocation(AreaLocation.TOP_RIGHT_STREET)
    // && !visitedAreas.contains(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.TOP_RIGHT_STREET).getAreaId()))
    // {
    // List<Integer> adjacents = getTopRightAdjacents(grid, i, j);
    // visitedAreas
    // .add(cell.getBoard().getAreaByAreaLocation(AreaLocation.TOP_RIGHT_STREET).getAreaId());
    // visitedAreas.addAll(adjacents);
    // areasToMerge.put(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.TOP_RIGHT_STREET).getAreaId(),
    // adjacents);

    // }
    // if (cell.getBoard().hasAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET) &&
    // !visitedAreas.contains(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET).getAreaId()))
    // {
    // List<Integer> adjacents = getMiddleRightAdjacents(grid, i, j);
    // areasToMerge.put(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET).getAreaId(),
    // adjacents);
    // visitedAreas.add(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.MIDDLE_RIGHT_STREET).getAreaId());
    // visitedAreas.addAll(adjacents);
    // }
    // if (cell.getBoard().hasAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET) &&
    // !visitedAreas.contains(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET).getAreaId()))
    // {
    // List<Integer> adjacents = getBottomRightAdjacents(grid, i, j);
    // visitedAreas.add(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET).getAreaId());
    // visitedAreas.addAll(adjacents);
    // areasToMerge.put(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET).getAreaId(),
    // adjacents);
    // }
    // if (cell.getBoard().hasAreaLocation(AreaLocation.BOTTOM_MIDDLE_STREET) &&
    // !visitedAreas.contains(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_MIDDLE_STREET).getAreaId()))
    // {
    // List<Integer> adjacents = getBottomMiddleAdjacents(grid, i, j);
    // visitedAreas.add(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_MIDDLE_STREET).getAreaId());
    // visitedAreas.addAll(adjacents);
    // areasToMerge.put(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_MIDDLE_STREET).getAreaId(),
    // adjacents);
    // }
    // if (cell.getBoard().hasAreaLocation(AreaLocation.BOTTOM_LEFT_STREET) &&
    // !visitedAreas.contains(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId()))
    // {
    // List<Integer> adjacents = getBottomLeftAdjacents(grid, i, j);
    // visitedAreas.add(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId());
    // visitedAreas.addAll(adjacents);
    // areasToMerge.put(
    // cell.getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId(),
    // adjacents);
    // }

    // }
    // }
    // }
    // return areasToMerge;
    // }

    // private List<Integer> getBottomLeftAdjacents(ZoneMissionGridCell[][] grid,
    // int i, int j) {
    // // BOTTOM_LEFT[x+1,y],TOP_LEFT[x+1,y+1],TOP_RIGHT[x,y+1]
    // List<Integer> adjacents = new ArrayList<>();
    // if (i + 1 < grid.length && grid[i +
    // 1][j].getBoard().hasAreaLocation(AreaLocation.BOTTOM_LEFT_STREET)) {
    // adjacents.add(grid[i +
    // 1][j].getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId());
    // }
    // if (j + 1 < grid[0].length && grid[i][j +
    // 1].getBoard().hasAreaLocation(AreaLocation.TOP_LEFT_STREET)) {
    // adjacents.add(grid[i][j +
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.TOP_LEFT_STREET).getAreaId());
    // }
    // if (i + 1 < grid.length && j + 1 < grid[0].length
    // && grid[i + 1][j +
    // 1].getBoard().hasAreaLocation(AreaLocation.TOP_RIGHT_STREET)) {
    // adjacents.add(
    // grid[i + 1][j +
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.TOP_RIGHT_STREET).getAreaId());
    // }
    // return adjacents;
    // }

    // private List<Integer> getBottomMiddleAdjacents(ZoneMissionGridCell[][] grid,
    // int i, int j) {
    // // BOTTOM_MIDDLE[x,y] adjacents are TOP_MIDDLE[x,y+1]
    // List<Integer> adjacents = new ArrayList<>();
    // if (j + 1 < grid[0].length && grid[i][j +
    // 1].getBoard().hasAreaLocation(AreaLocation.TOP_MIDDLE_STREET)) {
    // adjacents.add(grid[i][j +
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.TOP_MIDDLE_STREET).getAreaId());
    // }
    // return adjacents;
    // }

    // private List<Integer> getBottomRightAdjacents(ZoneMissionGridCell[][] grid,
    // int i, int j) {
    // // BOTTOM_RIGHT[x,y] adjacents are
    // // BOTTOM_LEFT[x+1,y],TOP_LEFT[x+1,y+1],TOP_RIGHT[x,y+1]
    // List<Integer> adjacents = new ArrayList<>();
    // if (i + 1 < grid.length && grid[i +
    // 1][j].getBoard().hasAreaLocation(AreaLocation.BOTTOM_LEFT_STREET)) {
    // adjacents.add(grid[i +
    // 1][j].getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId());
    // }
    // if (j + 1 < grid[0].length && grid[i][j +
    // 1].getBoard().hasAreaLocation(AreaLocation.TOP_LEFT_STREET)) {
    // adjacents.add(grid[i][j +
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.TOP_LEFT_STREET).getAreaId());
    // }
    // if (i + 1 < grid.length && j + 1 < grid[0].length
    // && grid[i + 1][j +
    // 1].getBoard().hasAreaLocation(AreaLocation.TOP_RIGHT_STREET)) {
    // adjacents.add(
    // grid[i + 1][j +
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.TOP_RIGHT_STREET).getAreaId());
    // }
    // return adjacents;
    // }

    // private List<Integer> getMiddleRightAdjacents(ZoneMissionGridCell[][] grid,
    // int i, int j) {
    // // MIDDLE_RIGHT[x,y] adjacents are MIDDLE_LETF[x+1,y]
    // List<Integer> adjacents = new ArrayList<>();
    // if (i + 1 < grid.length && grid[i +
    // 1][j].getBoard().hasAreaLocation(AreaLocation.MIDDLE_LEFT_STREET)) {
    // adjacents.add(grid[i +
    // 1][j].getBoard().getAreaByAreaLocation(AreaLocation.MIDDLE_LEFT_STREET).getAreaId());
    // }
    // return adjacents;
    // }

    // private List<Integer> getTopRightAdjacents(ZoneMissionGridCell[][] grid, int
    // x, int y) {
    // // TOP_RIGHT[x,y] adjacents are BOTOM_RIGHT[x,y-1], BOTTOM_LEFT[x+1,y-1],
    // List<Integer> adjacents = new ArrayList<>();
    // if (y - 1 > 0 && grid[x][y -
    // 1].getBoard().hasAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET)) {
    // adjacents
    // .add(grid[x][y -
    // 1].getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_RIGHT_STREET).getAreaId());
    // }
    // if (x + 1 < grid.length && grid[x +
    // 1][y].getBoard().hasAreaLocation(AreaLocation.BOTTOM_LEFT_STREET)) {
    // adjacents
    // .add(grid[x +
    // 1][y].getBoard().getAreaByAreaLocation(AreaLocation.BOTTOM_LEFT_STREET).getAreaId());
    // }
    // return adjacents;
    // }

    // public String buildMissionBoardImagePath(String edition, String collection,
    // String missionName) {
    // String missionImagesPath = configLoader.getProperty("folders.editions") + "/"
    // + edition + "/" + collection + "/"
    // + configLoader.getProperty("folders.missionImages");
    // String missionImagePath = missionImagesPath + "/" + "mission_" + missionName
    // + ".png";
    // return missionImagePath;
    // }

    // // ***** Private Methods ***** */

    // *** createMissionBoardImage *** */
    /**
     * Creates an image of the mission board by concatenating the different tiles.
     *
     * @param rows      The number of rows in the mission board
     * @param cols      The number of columns in the mission board
     * @param tiles     The tiles to be used in the mission board
     * @param rotations The rotations of the tiles
     */
    private static Image createMissionBoardImage(MissionGrid grid) {
        if (grid == null || grid.getGridWidth() == 0 || grid.getGridHeight() == 0) {
            return null;
        }

        int tileWidth = grid.getBoard(0, 0).getWidth();
        int tileHeight = grid.getBoard(0, 0).getHeight();
        int totalWidth = grid.getGridWidth() * tileWidth;
        int totalHeight = grid.getGridHeight() * tileHeight;

        BufferedImage missionImage = new BufferedImage(totalWidth, totalHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = missionImage.createGraphics();

        for (int i = 0; i < grid.getGridHeight(); i++) { // i = row index
            for (int j = 0; j < grid.getGridWidth(); j++) { // j = col index
                TileBoard board = grid.getBoard(i, j);
                if (board != null) {
                    Image img = board.getImage();

                    int x = j * tileWidth; // column determines x position
                    int y = i * tileHeight; // row determines y position

                    g2d.drawImage(img, x, y, tileWidth, tileHeight, null);
                }
            }
        }
        g2d.dispose();
        return missionImage;
    }

}
