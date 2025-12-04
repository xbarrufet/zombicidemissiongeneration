package com.zombicide.missiongen.ui.missions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.ui.components.ZonePropertiesPanel;

import com.zombicide.missiongen.ui.interfaces.MissionPropertiesListener;
import com.zombicide.missiongen.ui.interfaces.MissionTokenSelectionListener;

public class ZoneMissionProperties extends ZonePropertiesPanel implements MissionTokenSelectionListener {

    private javax.swing.JTextField missionNameField;
    private MissionPropertiesListener listener;
    private static final Logger logger = LoggerFactory.getLogger(ZoneMissionProperties.class);

    // The three main panels
    private PanelTokenTree panelTokenTree;
    private PanelTokenGrid panelTokenGrid;
    private PanelTokenDetail panelTokenDetail;
    
    private MissionBoard missionBoard;

    public ZoneMissionProperties(MissionPropertiesListener listener) {
        super();
        this.listener = listener;
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        missionNameField = new javax.swing.JTextField("No mission loaded");
        missionNameField.setColumns(20);
        missionNameField.addActionListener(e -> {
            // Notify listener when mission name is changed
            if (listener != null) {
                listener.onMissionNameUpdated(missionNameField.getText());
            }
        });
        
        // Initialize the three panels
        panelTokenTree = new PanelTokenTree();
        panelTokenTree.setAddTokenListener(() -> showAddModeState());
        
        panelTokenGrid = new PanelTokenGrid();
        panelTokenGrid.setTokenSelectionListener((type, subtype) -> {
            if (listener != null) {
                listener.onTokenSelected(type, subtype);
            }
        });
        
        panelTokenDetail = new PanelTokenDetail();
        panelTokenDetail.setTokenActionListener(new PanelTokenDetail.TokenActionListener() {
            @Override
            public void onAddTokenRequested() {
                showAddModeState();
            }
            
            @Override
            public void onDeleteTokenRequested(Token token) {
                if (listener != null) {
                    listener.onTokenDeleted(token);
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
        add(missionNameField, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        
        // Add all three panels to the same cell
        add(panelTokenTree, gbc);
        add(panelTokenGrid, gbc);
        add(panelTokenDetail, gbc);

        showDefaultState();
    }

    private void showDefaultState() {
        panelTokenTree.setVisible(true);
        panelTokenGrid.setVisible(false);
        panelTokenDetail.setVisible(false);
        
        setComponentZOrder(panelTokenTree, 0);

        if (missionBoard != null) {
            panelTokenTree.setMissionBoard(missionBoard);
            panelTokenTree.updateTokenTree();
        }

        revalidate();
        repaint();
    }

    private void showAddModeState() {
        panelTokenTree.setVisible(false);
        panelTokenGrid.setVisible(true);
        panelTokenDetail.setVisible(false);
        
        setComponentZOrder(panelTokenGrid, 0);
        
        // Force update of token grid
        panelTokenGrid.forceUpdate();

        revalidate();
        repaint();
    }

    private void showTokenSelectedState() {
        panelTokenTree.setVisible(false);
        panelTokenGrid.setVisible(false);
        panelTokenDetail.setVisible(true);
        
        setComponentZOrder(panelTokenDetail, 0);

        revalidate();
        repaint();
    }

    public void setMissionBoard(MissionBoard missionBoard) {
        this.missionBoard = missionBoard;
        panelTokenTree.setMissionBoard(missionBoard);
    }

    public void setMissionInfo(String missionName, int rows, int cols, int areasCount) {
        missionNameField.setText(missionName);
    }

    public void clearMissionInfo() {
        missionNameField.setText("No mission loaded");
    }

    @Override
    public void onTokenSelected(Token token) {
        panelTokenDetail.setToken(token);
        showTokenSelectedState();
        logger.info("Token info panel shown for token: {}", token.getId());
    }

    @Override
    public void onTokenUnSelected() {
        panelTokenDetail.setToken(null);
        showDefaultState();
        logger.info("Token info panel hidden, grid selection visible");
    }

}
