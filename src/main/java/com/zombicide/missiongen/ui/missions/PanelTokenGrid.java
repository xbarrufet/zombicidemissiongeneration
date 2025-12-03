package com.zombicide.missiongen.ui.missions;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.config.TokenLoader;
import com.zombicide.missiongen.model.tokens.TokenType;

public class PanelTokenGrid extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(PanelTokenGrid.class);
    
    private JComboBox<TokenType> tokenTypeCombo;
    private JPanel tokenGridPanel;
    private JPanel actualTokenGridContentPanel;
    private int lastPanelWidth = 0;
    private static final int RESIZE_THRESHOLD = 20;
    
    private float scale = 3;
    private int margin = 10;
    private int iconsPerRow = 2;
    
    public interface TokenSelectionListener {
        void onTokenTypeSelected(String type, String subtype);
    }
    
    private TokenSelectionListener tokenSelectionListener;
    
    public PanelTokenGrid() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setOpaque(false);
        
        tokenTypeCombo = new JComboBox<>(TokenType.values());
        tokenTypeCombo.addActionListener(e -> {
            lastPanelWidth = 0; // Force update when user changes token type
            updateTokenGrid((TokenType) tokenTypeCombo.getSelectedItem());
        });
        
        tokenGridPanel = new JPanel();
        tokenGridPanel.setLayout(new BorderLayout());
        tokenGridPanel.setOpaque(false);
        
        actualTokenGridContentPanel = new JPanel();
        actualTokenGridContentPanel.setLayout(new GridLayout(0, iconsPerRow, 5, 5));
        actualTokenGridContentPanel.setOpaque(false);
        tokenGridPanel.add(actualTokenGridContentPanel, BorderLayout.NORTH);
        
        // Add ComponentListener to handle resizing and initial layout
        tokenGridPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int currentWidth = tokenGridPanel.getWidth();
                if (currentWidth > 0 && tokenTypeCombo.getSelectedItem() != null) {
                    if (lastPanelWidth == 0 || Math.abs(currentWidth - lastPanelWidth) >= RESIZE_THRESHOLD) {
                        updateTokenGrid((TokenType) tokenTypeCombo.getSelectedItem());
                    }
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        add(new JLabel("Tokens:"), gbc);
        
        gbc.gridy++;
        add(tokenTypeCombo, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        JScrollPane scrollPane = new JScrollPane(tokenGridPanel);
        scrollPane.setPreferredSize(new Dimension(200, 300));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, gbc);
    }
    
    public void setTokenSelectionListener(TokenSelectionListener listener) {
        this.tokenSelectionListener = listener;
    }
    
    public void forceUpdate() {
        if (tokenTypeCombo.getSelectedItem() != null) {
            lastPanelWidth = 0;
            updateTokenGrid((TokenType) tokenTypeCombo.getSelectedItem());
        }
    }
    
    private void updateTokenGrid(TokenType type) {
        int currentWidth = tokenGridPanel.getWidth();
        
        if (currentWidth <= 0) {
            logger.debug("Skipping token grid update - panel width is 0");
            return;
        }
        
        lastPanelWidth = currentWidth;
        actualTokenGridContentPanel.removeAll();
        
        int[] dimension = TokenLoader.getInstance().getTokenDimension(type);
        int[] widthHeightGridCell = calculateGridCellWidthHeight(dimension);
        
        int numSubtypes = 0;
        if (type != null) {
            List<String> subtypes = TokenType.getSubtypes(type);
            numSubtypes = subtypes.size();
            
            for (String subtype : subtypes) {
                Image img = TokenLoader.getInstance().getTokenImage(type, subtype);
                if (img != null) {
                    
                    logger.info("Adding token icon to grid: width {} - height {}", img.getWidth(null), img.getHeight(null)     );
                    logger.info("                 scale to: width {} - height {}", widthHeightGridCell[0], widthHeightGridCell[1]     );
                    Image scaledImg = img.getScaledInstance(widthHeightGridCell[0], widthHeightGridCell[1],
                            Image.SCALE_SMOOTH);
                    
                    JLabel iconLabel = new JLabel(new ImageIcon(scaledImg));
                    iconLabel.setToolTipText(subtype);
                    //no border
                    iconLabel.setBorder(BorderFactory.createEmptyBorder());
                    iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    
                    final String finalSubtype = subtype;
                    final TokenType finalType = type;
                    iconLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                if (tokenSelectionListener != null) {
                                    tokenSelectionListener.onTokenTypeSelected(finalType.name(), finalSubtype);
                                }
                                logger.info("Token icon clicked: {} - {}", finalType.name(), finalSubtype);
                            }
                        }
                    });
                    
                    actualTokenGridContentPanel.add(iconLabel);
                }
            }
        }
        
        actualTokenGridContentPanel.setLayout(new GridLayout(0, iconsPerRow, 0, margin));
        
        int numRows = (int) Math.ceil((double) numSubtypes / iconsPerRow);
        int totalHeight = (numRows * widthHeightGridCell[1]) + ((numRows - 1) * margin);
        
        actualTokenGridContentPanel.setPreferredSize(new Dimension(currentWidth, totalHeight));
        
        logger.info("Panel preferred size set to: {}x{} ({} items)", currentWidth, totalHeight, numSubtypes);
        
        actualTokenGridContentPanel.revalidate();
        actualTokenGridContentPanel.repaint();
    }
    
    private int[] calculateGridCellWidthHeight(int[] dimension) {
        int tokenGridPanelWidth = tokenGridPanel.getWidth();
        
        if (tokenGridPanelWidth <= 0) {
            logger.warn("tokenGridPanel width is 0, using default cell size");
            return new int[] { 50, 50 };
        }
        
        // Account for borders (2 pixels per label) and gaps
        int gridCellWidth = tokenGridPanelWidth / iconsPerRow;
        float aspectRatio = (float) dimension[1] / (float) dimension[0];
        int gridCellHeight = (int) (gridCellWidth * aspectRatio);        
        return new int[] { gridCellWidth, gridCellHeight };
    }
}
