package com.zombicide.missiongen.ui.missions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.TokenLoader;
import com.zombicide.missiongen.model.tokens.TokenType;
import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;
import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;

public class ZoneMissionProperties extends ZonePropertiesPanel {

    private javax.swing.JLabel missionNameLabel;
    private javax.swing.JLabel dimensionsLabel;
    private javax.swing.JLabel areasCountLabel;

    private javax.swing.JRadioButton showAreasRadio;
    private javax.swing.JRadioButton hideAreasRadio;

    private MissionPropertiesListener listener;

    private float scale = 3;
    private int margin = 10;
    private int iconsPerRow = 2;

    private static final Logger logger = LoggerFactory.getLogger(ZoneMissionProperties.class);

  
    private javax.swing.JComboBox<com.zombicide.missiongen.model.tokens.TokenType> tokenTypeCombo;
    private javax.swing.JPanel tokenGridPanel;
    private javax.swing.JPanel actualTokenGridContentPanel;
    private int lastPanelWidth = 0;
    private static final int RESIZE_THRESHOLD = 20; // Only update if width changes by more than this

    public ZoneMissionProperties(MissionPropertiesListener listener) {
        super();
        this.listener = listener;
        initComponents();
        setupLayout();
    }


    private void initComponents() {
        missionNameLabel = new javax.swing.JLabel("No mission loaded");
        dimensionsLabel = new javax.swing.JLabel("");
        areasCountLabel = new javax.swing.JLabel("");

        showAreasRadio = new javax.swing.JRadioButton("Show Areas", true);
        hideAreasRadio = new javax.swing.JRadioButton("Hide Areas", false);

        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        group.add(showAreasRadio);
        group.add(hideAreasRadio);

        showAreasRadio.addActionListener(e -> fireOnBoardAreasVisibilityUpdated(true));
        hideAreasRadio.addActionListener(e -> fireOnBoardAreasVisibilityUpdated(false));

        tokenTypeCombo = new javax.swing.JComboBox<>(com.zombicide.missiongen.model.tokens.TokenType.values());
        tokenTypeCombo.addActionListener(e -> {
            lastPanelWidth = 0; // Force update when user changes token type
            updateTokenGrid((com.zombicide.missiongen.model.tokens.TokenType) tokenTypeCombo.getSelectedItem());
        });

        tokenGridPanel = new javax.swing.JPanel();
        tokenGridPanel.setLayout(new java.awt.BorderLayout()); // Use BorderLayout to control alignment
        tokenGridPanel.setOpaque(false); // Make transparent to match parent background

        actualTokenGridContentPanel = new javax.swing.JPanel();
        actualTokenGridContentPanel.setLayout(new java.awt.GridLayout(0, iconsPerRow, 0, 5)); // 2 columns, auto rows,
                                                                                              // no horizontal
        // gap
        actualTokenGridContentPanel.setOpaque(false); // Make transparent to match parent background
        tokenGridPanel.add(actualTokenGridContentPanel, java.awt.BorderLayout.NORTH); // Add the content panel to the
                                                                                      // top

        // Add ComponentListener to handle resizing and initial layout
        tokenGridPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int currentWidth = tokenGridPanel.getWidth();
                // Only update if width changed significantly or this is the first time
                if (currentWidth > 0 && tokenTypeCombo.getSelectedItem() != null) {
                    if (lastPanelWidth == 0 || Math.abs(currentWidth - lastPanelWidth) >= RESIZE_THRESHOLD) {
                        updateTokenGrid((TokenType) tokenTypeCombo.getSelectedItem());
                    }
                }
            }
        });
    }

    private void setupLayout() {
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Mission Name
        add(new javax.swing.JLabel("Mission:"), gbc);
        gbc.gridy++;
        add(missionNameLabel, gbc);

        // Area Visibility Toggle
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Area Visibility:"), gbc);

        javax.swing.JPanel radioPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        radioPanel.add(showAreasRadio);
        radioPanel.add(hideAreasRadio);

        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(radioPanel, gbc);

        // Token Selection
        gbc.gridy++;
        gbc.insets = new java.awt.Insets(15, 5, 5, 5);
        add(new javax.swing.JLabel("Tokens:"), gbc);

        gbc.gridy++;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(tokenTypeCombo, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.anchor = java.awt.GridBagConstraints.NORTH;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(tokenGridPanel);
        scrollPane.setPreferredSize(new java.awt.Dimension(200, 300));
        scrollPane.setBorder(null); // Remove scroll pane border
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent
        add(scrollPane, gbc);

        // Initialize grid with first item (will be updated by ComponentListener when
        // panel is sized)
        if (tokenTypeCombo.getItemCount() > 0 && tokenGridPanel.getWidth() > 0) {
            updateTokenGrid((TokenType) tokenTypeCombo.getSelectedItem());
        }
    }

    private void updateTokenGrid(TokenType type) {
        int currentWidth = tokenGridPanel.getWidth();

        // Skip update if panel doesn't have valid dimensions yet
        if (currentWidth <= 0) {
            logger.debug("Skipping token grid update - panel width is 0");
            return;
        }
        // Update last width to prevent redundant reloads
        lastPanelWidth = currentWidth;

        actualTokenGridContentPanel.removeAll();

        int[] dimension = TokenLoader.getInstance().getTokenDimension(type);
        int[] widthHeightGridCell = calculateGridCellWidthHeight(dimension);
        actualTokenGridContentPanel.setLayout(new java.awt.GridLayout(0, iconsPerRow, 0, margin));

        int numSubtypes = 0;
        if (type != null) {
            java.util.List<String> subtypes = com.zombicide.missiongen.model.tokens.TokenType.getSubtypes(type);
            numSubtypes = subtypes.size();

            for (String subtype : subtypes) {
                java.awt.Image img = com.zombicide.missiongen.config.TokenLoader.getInstance().getTokenImage(type,
                        subtype);
                if (img != null) {
                    // Scale image for display icon maintaining aspect ratio
                    java.awt.Image scaledImg = img.getScaledInstance(widthHeightGridCell[0], widthHeightGridCell[1],
                            java.awt.Image.SCALE_SMOOTH);
                    javax.swing.JLabel iconLabel = new javax.swing.JLabel(new javax.swing.ImageIcon(scaledImg));
                    iconLabel.setToolTipText(subtype);
                    iconLabel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY));
                    
                    // Add hand cursor to indicate clickable
                    iconLabel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                    
                    // Add click listener to fire token selection event
                    final String finalSubtype = subtype;
                    final TokenType finalType = type;
                    iconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                                fireTokenSelectedEvent(finalType.name(), finalSubtype);
                                logger.info("Token icon clicked: {} - {}", finalType.name(), finalSubtype);
                            }
                        }
                    });

                    actualTokenGridContentPanel.add(iconLabel);
                }
            }
        }

        // Calculate and set preferred size based on content
        int numRows = (int) Math.ceil((double) numSubtypes / iconsPerRow);
        int totalWidth = currentWidth;
        int totalHeight = (numRows * widthHeightGridCell[1]) + ((numRows - 1) * margin);

        actualTokenGridContentPanel.setPreferredSize(new java.awt.Dimension(totalWidth, totalHeight));
        logger.info("Panel preferred size set to: {}x{} ({} rows, {} items)", totalWidth, totalHeight, numRows,
                numSubtypes);

        actualTokenGridContentPanel.revalidate();
        actualTokenGridContentPanel.repaint();
    }

    private int[] calculateGridCellWidthHeight(int[] dimension) {
        int tokenGridPanelWidth = tokenGridPanel.getWidth();
        logger.info("Grid cell tokenGridPanelWidth: {}", tokenGridPanelWidth);

        // Prevent division by zero
        if (tokenGridPanelWidth <= 0) {
            logger.warn("tokenGridPanel width is 0, using default cell size");
            return new int[] { 50, 50 }; // Default fallback size
        }

        // Calculate available width per cell considering margins between cells
        // Total margins = (iconsPerRow - 1) * margin
        int totalMargins = (iconsPerRow - 1) * margin;
        int availableWidth = tokenGridPanelWidth - totalMargins;
        int gridCellWidth = availableWidth / iconsPerRow;

        // Calculate height maintaining aspect ratio
        // ratio = original_height / original_width
        float aspectRatio = (float) dimension[1] / (float) dimension[0];
        int gridCellHeight = (int) (gridCellWidth * aspectRatio);

        logger.info("Grid cell width: {} height: {} (aspect ratio: {})", gridCellWidth, gridCellHeight, aspectRatio);
        return new int[] { gridCellWidth, gridCellHeight };
    }

    private void fireOnBoardAreasVisibilityUpdated(boolean visible) {
        if (listener != null) {
            listener.onBoardAreasVisibilityUpdated(visible);
        }
    }

    public void setMissionInfo(String missionName, int rows, int cols, int areasCount) {
        missionNameLabel.setText(missionName);
        dimensionsLabel.setText(rows + " x " + cols);
        scale = scale / Math.max(rows, cols);
        areasCountLabel.setText(String.valueOf(areasCount));
    }

    public void clearMissionInfo() {
        missionNameLabel.setText("No mission loaded");
        dimensionsLabel.setText("");
        areasCountLabel.setText("");
    }

    private void fireTokenSelectedEvent(String type, String subtype) {
        if (listener != null) {
            listener.onTokenSelected(type, subtype);
        }
    }
}
