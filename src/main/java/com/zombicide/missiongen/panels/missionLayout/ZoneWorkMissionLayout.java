package com.zombicide.missiongen.panels.missionLayout;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Board;
import com.zombicide.missiongen.model.BoardArea;
import com.zombicide.missiongen.panels.interfaces.BoardChangeListener;
import com.zombicide.missiongen.panels.interfaces.BoardSelectionListener;
import com.zombicide.missiongen.panels.tiles.ZoneProperties;
import com.zombicide.missiongen.panels.tiles.ZoneTileDraw;

/**
 * Container panel that holds ZoneDraw and ZoneProperties.
 * Can be replaced as a whole unit.
 */
public class ZoneWorkMissionLayout extends Panel implements BoardSelectionListener, BoardChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ZoneWorkMissionLayout.class);

    private ZoneTileDraw zoneDraw;
    private ZoneProperties zoneProperties;

    public ZoneWorkMissionLayout() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        zoneDraw = new ZoneTileDraw();
        zoneProperties = new ZoneProperties();

        // Register as listener for selection changes
        zoneDraw.addSelectionListener(this);

        // Register as listener for board changes (areas/connections)
        zoneProperties.addBoardChangeListener(this);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ZoneDraw - Left side (750x750)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(zoneDraw, gbc);

        // ZoneProperties - Right side (250x750)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(zoneProperties, gbc);
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

}
