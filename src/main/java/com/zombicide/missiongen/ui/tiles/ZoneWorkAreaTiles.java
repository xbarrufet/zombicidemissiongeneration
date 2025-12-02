package com.zombicide.missiongen.ui.tiles;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Tile;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.board.BaseBoard;
import com.zombicide.missiongen.ui.components.ZoneDrawPanel;
import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;
import com.zombicide.missiongen.ui.components.ZoneWorkAreaPanel;
import com.zombicide.missiongen.ui.interfaces.BoardChangeListener;
import com.zombicide.missiongen.ui.interfaces.BoardSelectionListener;
import com.zombicide.missiongen.ui.interfaces.PanelSelectionListener;

/**
 * Container panel that holds ZoneDraw and ZoneProperties.
 * Can be replaced as a whole unit.
 */
public class ZoneWorkAreaTiles extends ZoneWorkAreaPanel
        implements BoardSelectionListener, BoardChangeListener, PanelSelectionListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneWorkAreaTiles.class);

    private ZoneTileDraw zoneDraw;
    private ZoneProperties zoneProperties;

    public ZoneWorkAreaTiles() {
        super();
        zoneDraw = new ZoneTileDraw();
        zoneProperties = new ZoneProperties(zoneDraw); // Pass reference directly
        init(); // Initialize layout and components
    }

    private Runnable onSaveRequired;

    @Override
    public void init() {
        super.init();
        initComponents();
    }

    private void initComponents() {
        // Register as listener to zoneDraw
        if (zoneDraw != null) {
            zoneDraw.addBoardChangeListener(this);
        }
    }

    public void setOnSaveRequired(Runnable onSaveRequired) {
        this.onSaveRequired = onSaveRequired;
    }

    public ZoneTileDraw getZoneDraw() {
        return zoneDraw;
    }

    public ZoneProperties getZoneProperties() {
        return zoneProperties;
    }

    public void setBoard(BaseBoard board) {
        this.zoneDraw.setBoard(board);
        this.zoneProperties.setBoard(board);
    }

    // Selection handling is now done directly in ZoneProperties via ZoneTileDraw
    // reference
    @Override
    public void onSelectionChanged(List<BoardArea> selectedAreas) {
        logger.info("Selection changed: {} area(s) selected", selectedAreas.size());
    }

    @Override
    public void onAreasChanged() {
        // Repaint when areas are added or removed
        zoneDraw.repaint();
        logger.info("Board areas changed, repainting");
        if (onSaveRequired != null) {
            onSaveRequired.run();
        }
    }

    @Override
    public void onConnectionsChanged() {
        // Could add visual feedback for connections if needed
        logger.info("Board connections changed");
        if (onSaveRequired != null) {
            onSaveRequired.run();
        }
    }

    @Override
    protected ZonePropertiesPanel getZonePropertiesPanel() {
        return zoneProperties;
    }

    @Override
    protected ZoneDrawPanel getZoneDrawPanel() {
        return zoneDraw;
    }

    @Override
    public void onTileSelected(Tile tile) {
        logger.info("Tile selected: {}", tile.getTileName());
        this.setBoard(tile.getBoard());
    }

    @Override
    public void onEditionCollectionSelected(String edition, String collection) {
        // do nothing
    }

    // Split mode delegation is now handled directly by ZoneProperties via
    // ZoneTileDraw reference
}
