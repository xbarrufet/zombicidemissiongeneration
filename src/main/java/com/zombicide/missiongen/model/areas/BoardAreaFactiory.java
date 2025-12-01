package com.zombicide.missiongen.model.areas;

import java.awt.Point;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.areas.BoardArea.AreaType;
import com.zombicide.missiongen.services.PersistanceService;

public final class BoardAreaFactiory {

    private static final Logger logger = LoggerFactory.getLogger(PersistanceService.class);

    public static BoardArea createBoardBorderArea(AreaLocation areaLocation) {
        BoardArea area = null;
        switch (areaLocation) {
            case TOP_LEFT_STREET:
                area = createTopLeftBoardArea();
                break;
            case TOP_MIDDLE_STREET:
                area = createTopMiddleBoardArea();
                break;
            case TOP_RIGHT_STREET:
                area = createTopRightBoardArea();
                break;
            case MIDDLE_LEFT_STREET:
                area = createMiddleLeftBoardArea();
                break;
            case MIDDLE_RIGHT_STREET:
                area = createMiddleRightBoardArea();
                break;
            case BOTTOM_LEFT_STREET:
                area = createBottomLeftBoardArea();
                break;
            case BOTTOM_MIDDLE_STREET:
                area = createBottomMiddleBoardArea();
                break;
            case BOTTOM_RIGHT_STREET:
                area = createBottomRightBoardArea();
                break;
            default:
                area = null;
        }
        logger.info("Created board area: " + area + " with vertext at " + area.getTopLeft() + " and size "
                + area.getWidth() + " x " + area.getHeight());
        return area;

    }

    public static BoardArea createBoardMergeArea(Point topLeft, Point bottomRight) {
        return new BoardArea(UUID.randomUUID(), topLeft, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y,
                AreaType.STREET_MERGE);
    }

    public static BoardArea createBoardIndoorArea(Point topLeft, Point bottomRight) {
        return new BoardArea(UUID.randomUUID(), topLeft, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y,
                AreaType.INDOOR_LIGHT);
    }

    private static BoardArea createTopLeftBoardArea() {
        return new BoardArea(UUID.randomUUID(), new Point(0, 0), getBorderAreaWidth(), getBorderAreaWidth(),
                AreaLocation.TOP_LEFT_STREET);
    }

    private static BoardArea createTopMiddleBoardArea() {
        return new BoardArea(UUID.randomUUID(), new Point(getBorderAreaWidth(), 0), getMiddleAreaWidth(),
                getBorderAreaWidth(),
                AreaLocation.TOP_MIDDLE_STREET);
    }

    private static BoardArea createTopRightBoardArea() {
        return new BoardArea(UUID.randomUUID(), new Point(getBorderAreaWidth() + getMiddleAreaWidth(), 0),
                getBorderAreaWidth(),
                getBorderAreaWidth(), AreaLocation.TOP_RIGHT_STREET);
    }

    private static BoardArea createMiddleLeftBoardArea() {
        return new BoardArea(UUID.randomUUID(), new Point(0, getBorderAreaWidth()), getBorderAreaWidth(),
                getMiddleAreaWidth(),
                AreaLocation.MIDDLE_LEFT_STREET);
    }

    private static BoardArea createMiddleRightBoardArea() {
        return new BoardArea(UUID.randomUUID(),
                new Point(getBorderAreaWidth() + getMiddleAreaWidth(), getBorderAreaWidth()),
                getBorderAreaWidth(), getMiddleAreaWidth(), AreaLocation.MIDDLE_RIGHT_STREET);
    }

    private static BoardArea createBottomLeftBoardArea() {
        return new BoardArea(UUID.randomUUID(), new Point(0, getBorderAreaWidth() + getMiddleAreaWidth()),
                getBorderAreaWidth(),
                getBorderAreaWidth(), AreaLocation.BOTTOM_LEFT_STREET);
    }

    private static BoardArea createBottomMiddleBoardArea() {
        return new BoardArea(UUID.randomUUID(),
                new Point(getBorderAreaWidth(), getBorderAreaWidth() + getMiddleAreaWidth()),
                getMiddleAreaWidth(), getBorderAreaWidth(), AreaLocation.BOTTOM_MIDDLE_STREET);
    }

    private static BoardArea createBottomRightBoardArea() {
        return new BoardArea(UUID.randomUUID(),
                new Point(getBorderAreaWidth() + getMiddleAreaWidth(), getBorderAreaWidth() + getMiddleAreaWidth()),
                getBorderAreaWidth(), getBorderAreaWidth(), AreaLocation.BOTTOM_RIGHT_STREET);
    }

    public static int getBorderAreaWidth() {
        return ConfigLoader.getInstance().getPropertyAsInt("tile.area.corner_width");
    }

    public static int getMiddleAreaWidth() {
        return ConfigLoader.getInstance().getPropertyAsInt("tile.area.middle_width");
    }

    public static int getTileWidth() {
        return ConfigLoader.getInstance().getPropertyAsInt("tile.width");
    }

    public static Point[] getExploringPoints() {
        // retrun the middle point of the 9 areas that we can divied a tile
        return new Point[] {
                new Point(37, 37),
                new Point(122, 37),
                new Point(220, 37),
                new Point(37, 122),
                new Point(122, 122),
                new Point(220, 122),
                new Point(37, 220),
                new Point(122, 220),
                new Point(220, 220)
        };
    }
}
