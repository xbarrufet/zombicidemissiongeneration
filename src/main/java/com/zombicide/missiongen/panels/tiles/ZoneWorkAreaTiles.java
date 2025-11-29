package com.zombicide.missiongen.panels.tiles;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Board;
import com.zombicide.missiongen.model.BoardArea;
import com.zombicide.missiongen.panels.components.ZoneDrawPanel;
import com.zombicide.missiongen.panels.components.ZonePropertiesPanel;
import com.zombicide.missiongen.panels.components.ZoneWorkAreaPanel;
import com.zombicide.missiongen.panels.interfaces.BoardChangeListener;
import com.zombicide.missiongen.panels.interfaces.BoardSelectionListener;

/**
 * Container panel that holds ZoneDraw and ZoneProperties.
 * Can be replaced as a whole unit.
 */
public class ZoneWorkAreaTiles extends ZoneWorkAreaPanel implements BoardSelectionListener, BoardChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneWorkAreaTiles.class);

    private ZoneTileDraw zoneDraw;
    private ZoneProperties zoneProperties;

    public ZoneWorkAreaTiles() {
        super();
        zoneDraw = new ZoneTileDraw();
        zoneProperties = new ZoneProperties();
        init(); // Initialize layout and components
    }

    @Override
    public void init() {
        super.init();
        initComponents();
    }

    private void initComponents() {

        // Register as listener for selection changes
        zoneDraw.addSelectionListener(this);

        // Register as listener for board changes (areas/connections)
        zoneProperties.addBoardChangeListener(this);
    }

    public ZoneTileDraw getZoneDraw() {
        return zoneDraw;
    }

    public ZoneProperties getZoneProperties() {
        return zoneProperties;
    }

    public void setBoard(Board board) {
        this.zoneDraw.setBoard(board);
        this.zoneProperties.setBoard(board);
    }

    @Override
    public void onSelectionChanged(List<BoardArea> selectedAreas) {
        logger.info("Selection changed: {} area(s) selected", selectedAreas.size());

        // Pass all selected areas to ZoneProperties
        zoneProperties.updateAreasSelection(selectedAreas);

        // Log selected area IDs
        for (BoardArea area : selectedAreas) {
            logger.info("  - Area ID: {}", area.getId());
        }
    }

    @Override
    public void onAreasChanged() {
        // Repaint when areas are added or removed
        zoneDraw.repaint();
        logger.info("Board areas changed, repainting");
    }

    @Override
    public void onConnectionsChanged() {
        // Could add visual feedback for connections if needed
        logger.info("Board connections changed");
    }

    @Override
    protected ZonePropertiesPanel getZonePropertiesPanel() {
        return zoneProperties;
    }

    @Override
    protected ZoneDrawPanel getZoneDrawPanel() {
        return zoneDraw;
    }

}
