package com.zombicide.missiongen.ui.missionLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.model.board.MissionGrid;
import com.zombicide.missiongen.model.board.TileBoard;
import com.zombicide.missiongen.ui.components.ZoneDrawPanel;
import com.zombicide.missiongen.ui.interfaces.MissionLayoutUpdate;

public class ZoneMissionGrid extends ZoneDrawPanel
        implements com.zombicide.missiongen.ui.interfaces.GridClickListener {

    private int rows = 2; // height
    private int cols = 2; // width
    private JPanel gridPanel;
    private JPanel wrapperPanel;
    private TileBoard selectedTile;

    private ZoneMissionGridCell[][] gridCells;
    private MissionGrid missionGrid;

    private boolean missionLayoutValid = true;

    private static final Logger logger = LoggerFactory.getLogger(ZoneMissionGrid.class);

    private MissionLayoutUpdate listener;
    private final java.util.List<com.zombicide.missiongen.ui.interfaces.TileGridListener> tileGridListeners = new java.util.ArrayList<>();

    public ZoneMissionGrid(MissionLayoutUpdate listener) {
        super();
        this.listener = listener;
        initComponents();
        reset(cols, rows);
    }

    public void reset(int width, int height) {

        // remove all elements from gridCells
        if (this.gridCells != null) {
            for (int row = 0; row < this.gridCells.length; row++) {
                for (int col = 0; col < this.gridCells[row].length; col++) {
                    removeBoardFromGrid(col, row);
                }
            }
        }

        this.rows = height; // height = rows
        this.cols = width; // width = cols
        this.gridCells = new ZoneMissionGridCell[height][width]; // [rows][cols]
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.gridCells[row][col] = new ZoneMissionGridCell(row, col, this);
            }
        }
        resizeGrid(width, height);
        paintGrid();
        this.missionGrid = new MissionGrid(width, height);
        this.listener.onMissionGridUpdated(this.missionGrid);
    }

    private void initComponents() {
        // Wrapper for centering
        wrapperPanel = new JPanel(new GridBagLayout());
        // Actual grid panel
        gridPanel = new JPanel();
        gridPanel.setLayout(new java.awt.GridLayout(rows, cols)); // GridLayout(rows, cols)
        Color gridColor = (missionLayoutValid ? Color.BLACK : Color.RED);
        gridPanel.setBorder(new javax.swing.border.LineBorder(gridColor));
        wrapperPanel.add(gridPanel);
        setLayout(new java.awt.BorderLayout());
        add(wrapperPanel, java.awt.BorderLayout.CENTER);

        // Add component listener to handle resize events
        // necesario para que el grid se ajuste al tamaño de la ventana, esto se lanza
        // cuando se muestra la ventana -> panel width y height son 0 antes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setupGridPanelSize();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                setupGridPanelSize();
            }
        });
    }

    public void setGridSize(int width, int height) {
        resizeGrid(width, height);
        paintGrid();
    }

    private void resizeGrid(int width, int height) {
        this.rows = height; // height = rows
        this.cols = width; // width = cols
        setupGridPanelSize();
        this.gridCells = new ZoneMissionGridCell[height][width]; // [rows][cols]
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.gridCells[row][col] = new ZoneMissionGridCell(row, col, this);
            }
        }
    }

    private void paintGrid() {

        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(rows, cols)); // GridLayout(rows, cols)
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ZoneMissionGridCell cell = this.gridCells[row][col];
                gridPanel.add(cell);
            }
        }
        // pass validate value to grid cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ZoneMissionGridCell cell = this.gridCells[row][col];
                cell.setLayouValid(this.missionLayoutValid);
            }
        }
        revalidate();
        repaint();
    }

    private void setupGridPanelSize() {
        if (this.getWidth() == 0 || this.getHeight() == 0) {
            return;
        }

        int availableWidth = this.getWidth();
        int availableHeight = this.getHeight();

        int maxCellWidth = availableWidth / cols; // width / cols
        int maxCellHeight = availableHeight / rows; // height / rows

        // Use smaller dimension to keep cells square
        int cellSize = Math.min(maxCellWidth, maxCellHeight);

        int finalWidth = cellSize * cols;
        int finalHeight = cellSize * rows;

        gridPanel.setPreferredSize(new Dimension(finalWidth, finalHeight));
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    protected void setTile(TileBoard tile) {
        this.selectedTile = tile;
    }

    @Override
    public void onGridClick(int x, int y) {
        logger.info("Left click on cell {} {}", x, y);
        if (this.selectedTile == null) {
            return;
        }

        // Check if cell is already occupied? User said "only 1 tile of each type in the
        // grid".
        // If we place a tile, we should probably remove the old one first if any?
        // Or just overwrite. If overwrite, we should notify that the old one is
        // removed?

        TileBoard oldBoard = this.missionGrid.getBoard(x, y);

        if (oldBoard != null && oldBoard != this.selectedTile) {
            // If there was a board, notify removal so it goes back to list
            // But wait, getBoard might return empty board or null?
            // MissionGrid initializes with null or empty board?
            // MissionGrid constructor fills with null.
            if (oldBoard.getAreas() != null && !oldBoard.getAreas().isEmpty()) {
                fireTileRemoved(x, y, oldBoard);
            }
        }
        this.missionGrid.setBoard(x, y, this.selectedTile);
        this.missionLayoutValid = this.missionGrid.validate();
        logger.info("Mission layout valid: {}", this.missionLayoutValid);
        this.gridCells[y][x].setCellBackground(this.selectedTile.getImage());
        // Validation can be added later if needed
        // this.missionGrid.validate();
        paintGrid();
        this.listener.onMissionGridUpdated(this.missionGrid);

        fireTilePlaced(x, y, this.selectedTile);

        // Clear selected tile after placement?
        // User said: "Cada vez que se añada una tila al grid debe eliminarse de la
        // lista".
        // This implies the tile is "consumed".
        this.selectedTile = null;
    }

    @Override
    public void onGridDoubleClick(int x, int y) {
        logger.info("Double click on cell {} {}", x, y);
        removeBoardFromGrid(x, y);
    }

    private void removeBoardFromGrid(int x, int y) {
        TileBoard board = this.missionGrid.getBoard(x, y);
        if (board != null) {
            this.missionGrid.setBoard(x, y, null); // Remove from model
            this.gridCells[y][x].setCellBackground(null); // Remove visual
            this.missionLayoutValid = this.missionGrid.validate();
            paintGrid();
            this.listener.onMissionGridUpdated(this.missionGrid);
            fireTileRemoved(y, x, board);
            logger.info("After Remove: Mission layout valid: {}", this.missionLayoutValid);
        }

    }

    public void addTileGridListener(com.zombicide.missiongen.ui.interfaces.TileGridListener listener) {
        tileGridListeners.add(listener);
    }

    private void fireTilePlaced(int row, int col, TileBoard tileBoard) {
        for (com.zombicide.missiongen.ui.interfaces.TileGridListener listener : tileGridListeners) {
            listener.onTilePlaced(row, col, tileBoard);
        }
    }

    private void fireTileRemoved(int row, int col, TileBoard tileBoard) {
        for (com.zombicide.missiongen.ui.interfaces.TileGridListener listener : tileGridListeners) {
            listener.onTileRemoved(row, col, tileBoard);
        }
    }

    @Override
    public void onGridRightClick(int x, int y) {
        logger.info("Right click on cell {} {}", x, y);
        TileBoard selectedBoard = this.missionGrid.getBoard(x, y);
        if (selectedBoard != null) {
            selectedBoard.rotate();
            this.gridCells[y][x].setCellBackground(selectedBoard.getImage());
            this.missionLayoutValid = this.missionGrid.validate();
            logger.info("After Rotate: Mission layout valid: {}", this.missionLayoutValid);
            paintGrid();
            this.listener.onMissionGridUpdated(this.missionGrid);
        }
    }

}
