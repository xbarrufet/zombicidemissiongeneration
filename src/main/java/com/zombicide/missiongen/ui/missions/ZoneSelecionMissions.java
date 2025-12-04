package com.zombicide.missiongen.ui.missions;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.Mission;
import com.zombicide.missiongen.model.board.MissionFactoryService;
import com.zombicide.missiongen.model.board.MissionGrid;
import com.zombicide.missiongen.services.PersistanceService;
import com.zombicide.missiongen.ui.interfaces.LayoutChangeListener;
import com.zombicide.missiongen.ui.interfaces.MissionLayoutUpdate;
import com.zombicide.missiongen.ui.interfaces.MissionSelectionListener;
import com.zombicide.missiongen.ui.interfaces.PanelSelectionListener;
import com.zombicide.missiongen.ui.components.styled.StyledButton;
import com.zombicide.missiongen.ui.components.styled.StyledComboBox;
import com.zombicide.missiongen.ui.components.styled.StyledLabel;
import com.zombicide.missiongen.ui.components.styled.MissionListCellRenderer;
import com.zombicide.missiongen.ui.theme.UIConstants;
import com.zombicide.missiongen.ui.components.notification.ToastManager;

public class ZoneSelecionMissions extends JPanel implements MissionLayoutUpdate {
    private static final Logger logger = LoggerFactory.getLogger(ZoneSelecionMissions.class);

    private PersistanceService persistanceService;
    private MissionFactoryService missionFactoryService;

    private StyledComboBox<String> editionsChoice;
    private StyledComboBox<String> collectionsChoice;

    private StyledButton newMissionButton;
    private StyledButton saveMissionButton;
    private StyledButton exportImageButton;
    private StyledButton deleteMissionButton;

    private JList<String> missionList;
    private DefaultListModel<String> missionListModel;

    private String selectedEdition;
    private String selectedCollection;
    private final java.util.List<MissionSelectionListener> missionSelectionListeners;
    private final java.util.List<com.zombicide.missiongen.ui.interfaces.PanelSelectionListener> collectionSelectionListeners;
    private LayoutChangeListener layoutChangeListener;

    private MissionGrid grid;
    private String newMissionName;
    private Mission currentMission;

    public ZoneSelecionMissions(MissionFactoryService missionFactoryService,
            LayoutChangeListener layoutChangeListener) {
        this.layoutChangeListener = layoutChangeListener;
        this.missionFactoryService = missionFactoryService;
        persistanceService = new PersistanceService();
        missionSelectionListeners = new java.util.ArrayList<>();
        collectionSelectionListeners = new java.util.ArrayList<>();

        setBackground(UIConstants.BACKGROUND);
        Dimension fixedSize = new Dimension(UIConstants.SELECTION_PANEL_WIDTH, UIConstants.DRAW_PANEL_SIZE);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        // Add modern border
        setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER),
                javax.swing.BorderFactory.createEmptyBorder(
                    UIConstants.SPACING_MD,
                    UIConstants.SPACING_MD,
                    UIConstants.SPACING_MD,
                    UIConstants.SPACING_MD
                )));

        initComponents();
        setupLayout();
        loadEditions();
    }

    private void initComponents() {
        // Editions dropdown
        editionsChoice = new StyledComboBox<>();
        editionsChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditionSelected();
            }
        });

        // Collections dropdown
        collectionsChoice = new StyledComboBox<>();
        collectionsChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCollectionSelected();
            }
        });

        // Missions list with custom renderer
        missionListModel = new DefaultListModel<>();
        missionList = new JList<>(missionListModel);
        missionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        missionList.setVisibleRowCount(20);
        missionList.setCellRenderer(new MissionListCellRenderer());
        missionList.setBackground(UIConstants.PANEL_BACKGROUND);
        missionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    onMissionSelected();
                }
            }
        });

        // New Mission button (Primary style)
        newMissionButton = new StyledButton("Add New Mission", StyledButton.ButtonStyle.PRIMARY);
        newMissionButton.setEnabled(true);
        newMissionButton.addActionListener(e -> onGenerateMission());

        // Save Mission button (Success style)
        saveMissionButton = new StyledButton("Save Mission", StyledButton.ButtonStyle.SUCCESS);
        saveMissionButton.setEnabled(false);
        saveMissionButton.addActionListener(e -> onSaveMission());
        
        // Export Image button (Secondary style)
        exportImageButton = new StyledButton("Export Image", StyledButton.ButtonStyle.SECONDARY);
        exportImageButton.setEnabled(false);
        exportImageButton.addActionListener(e -> onExportImage());
        
        // Delete Mission button (Danger style)
        deleteMissionButton = new StyledButton("Delete Mission", StyledButton.ButtonStyle.DANGER);
        deleteMissionButton.setEnabled(false);
        deleteMissionButton.addActionListener(e -> onDeleteMission());
    }

    private void onGenerateMission() {
        layoutChangeListener.onMissionLayoutSelected();
        saveMissionButton.setEnabled(false);
        exportImageButton.setEnabled(false);
        deleteMissionButton.setEnabled(false);
        this.currentMission = null; // Reset current mission when creating new
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Editions label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new StyledLabel("Ediciones:", StyledLabel.LabelStyle.LABEL), gbc);

        // Editions dropdown
        gbc.gridy = 1;
        add(editionsChoice, gbc);

        // Collections label
        gbc.gridy = 2;
        add(new StyledLabel("Colecciones:", StyledLabel.LabelStyle.LABEL), gbc);

        // Collections dropdown
        gbc.gridy = 3;
        add(collectionsChoice, gbc);

        // New Mission button
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(newMissionButton, gbc);

        // Missions label
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(new StyledLabel("Misiones:", StyledLabel.LabelStyle.LABEL), gbc);

        // Missions list (wrapped in scroll pane)
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JScrollPane(missionList), gbc);

        // Save Mission button
        gbc.gridy = 7;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(saveMissionButton, gbc);
        
        // Export Image button
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(exportImageButton, gbc);
        
        // Delete Mission button
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(deleteMissionButton, gbc);
    }

    private void loadEditions() {
        editionsChoice.removeAllItems();
        java.util.List<String> editions = persistanceService.getEditions();

        if (editions.isEmpty()) {
            editionsChoice.addItem("(No editions found)");
            editionsChoice.setEnabled(false);
            logger.info("No editions found");
        } else {
            for (String edition : editions) {
                editionsChoice.addItem(edition);
            }
            editionsChoice.setEnabled(true);
            logger.info("Loaded {} editions", editions.size());

            // Auto-select first edition
            if (editionsChoice.getItemCount() > 0) {
                editionsChoice.setSelectedIndex(0);
                // Listener will trigger loadCollections
            }
        }
    }

    private void loadCollections() {
        collectionsChoice.removeAllItems();
        missionListModel.clear();

        if (selectedEdition == null || selectedEdition.equals("(No editions found)")) {
            collectionsChoice.addItem("(Select edition first)");
            collectionsChoice.setEnabled(false);
            return;
        }

        java.util.List<String> collections = persistanceService.getCollections(selectedEdition);

        if (collections.isEmpty()) {
            collectionsChoice.addItem("(No collections found)");
            collectionsChoice.setEnabled(false);
            logger.info("No collections found for edition: {}", selectedEdition);
        } else {
            for (String collection : collections) {
                collectionsChoice.addItem(collection);
            }
            collectionsChoice.setEnabled(true);
            logger.info("Loaded {} collections for edition: {}", collections.size(), selectedEdition);

            // Auto-select first collection
            if (collectionsChoice.getItemCount() > 0) {
                collectionsChoice.setSelectedIndex(0);
                // Listener will trigger loadMissions
            }
        }
    }

    private void loadMissions() {
        missionListModel.clear();

        if (selectedEdition == null || selectedCollection == null) {
            logger.warn("Cannot load missions: edition or collection not selected");
            return;
        }

        java.util.List<String> missions = persistanceService.getMissions(selectedEdition, selectedCollection);

        if (missions.isEmpty()) {
            missionListModel.addElement("(No missions found)");
            logger.info("No missions found for edition: {}, collection: {}", selectedEdition, selectedCollection);
        } else {
            for (String mission : missions) {
                missionListModel.addElement(mission);
            }
            logger.info("Loaded {} missions for edition: {}, collection: {}",
                    missions.size(), selectedEdition, selectedCollection);
        }
    }

    private void onEditionSelected() {
        Object selected = editionsChoice.getSelectedItem();
        if (selected != null) {
            selectedEdition = (String) selected;
            logger.info("Edition selected: {}", selectedEdition);
            clearWorkArea();
            loadCollections();
        }
    }

    private void onCollectionSelected() {
        Object selected = collectionsChoice.getSelectedItem();
        if (selected != null) {
            selectedCollection = (String) selected;
            logger.info("Collection selected: {}", selectedCollection);
            clearWorkArea();
            loadMissions();
            notifyEditionCollectionSelected(selectedEdition, selectedCollection);
        }
    }

    public void notifyEditionCollectionSelected(String edition, String collection) {
        for (PanelSelectionListener listener : collectionSelectionListeners) {
            listener.onEditionCollectionSelected(edition, collection);
        }
    }

    public void addCollectionSelectionListener(
            PanelSelectionListener listener) {
        if (listener != null) {
            collectionSelectionListeners.add(listener);
            if (selectedEdition != null && selectedCollection != null) {
                notifyEditionCollectionSelected(selectedEdition, selectedCollection);
            }
        }
    }

    public void addMissionSelectionListener(MissionSelectionListener listener) {
        if (listener != null && !missionSelectionListeners.contains(listener)) {
            missionSelectionListeners.add(listener);
        }
    }

    public void removeMissionSelectionListener(MissionSelectionListener listener) {
        missionSelectionListeners.remove(listener);
    }

    private void notifyMissionSelected(Mission mission) {
        for (MissionSelectionListener listener : missionSelectionListeners) {
            listener.onMissionSelected(mission);
        }
    }

    private void onMissionSelected() {
        String missionName = missionList.getSelectedValue();
        if (missionName != null && !missionName.equals("(No missions found)")) {
            logger.info("Mission selected: {}", missionName);

            // First, notify the layout change listener to switch to mission viewer
            if (layoutChangeListener != null) {
                layoutChangeListener.onMissionSelected();
            }

            // Then load and notify mission selection after view is switched
            Mission mission = persistanceService.getMission(selectedEdition, selectedCollection, missionName);
            if (mission != null) {
                this.currentMission = mission;
                this.saveMissionButton.setEnabled(true); // Always enabled when editing a mission
                this.exportImageButton.setEnabled(true); // Always enabled when editing a mission
                this.deleteMissionButton.setEnabled(true);
                notifyMissionSelected(mission);
            }
        } else {
            this.saveMissionButton.setEnabled(false);
            this.exportImageButton.setEnabled(false);
            this.deleteMissionButton.setEnabled(false);
        }
    }

    private void onSaveMission() {
        logger.info("Saving mission layout");
        if (this.newMissionName == null || this.newMissionName.isEmpty()) {
            logger.warn("No mission name");
            // auto generate mission name, random string of 10 characters
            this.newMissionName = "Mission-" + String.valueOf((int) (Math.random() *
                    100));
        }
        
        try {
            if (this.currentMission != null) {
                // We are editing an existing mission
                // Update mission name if changed
                this.currentMission.setMissionName(this.newMissionName);

                // Persist the existing mission object which has the updated board (tokens) and name
                persistanceService.persistMission(currentMission);
                ToastManager.getInstance().showSuccess("Mission saved successfully!");
            } else {
                // We are creating a new mission from grid
                String missionId = selectedEdition + "." + selectedCollection + "." +
                        newMissionName;
                Mission mission = MissionFactoryService.createMission(missionId, selectedEdition, selectedCollection,
                        newMissionName, grid);

                persistanceService.persistMission(mission);
                this.currentMission = mission; // Set as current mission after creation
                ToastManager.getInstance().showSuccess("Mission '" + newMissionName + "' created successfully!");
            }
            // Keep buttons enabled after saving
            this.saveMissionButton.setEnabled(true);
            this.exportImageButton.setEnabled(true);
            this.deleteMissionButton.setEnabled(true);
            // Reload missions list to show the newly saved mission
            loadMissions();
            
            // Switch to mission view after saving
            if (layoutChangeListener != null) {
                layoutChangeListener.onMissionSelected();
            }
            // Notify listeners with the saved mission
            notifyMissionSelected(currentMission);
            
            // Ensure buttons remain enabled after all operations
            this.saveMissionButton.setEnabled(true);
            this.exportImageButton.setEnabled(true);
            this.deleteMissionButton.setEnabled(true);
        } catch (Exception e) {
            logger.error("Failed to save mission", e);
            ToastManager.getInstance().showError("Failed to save mission: " + e.getMessage());
        }
    }

    // Getters for external access
    public String getSelectedEdition() {
        return selectedEdition;
    }

    public String getSelectedCollection() {
        return selectedCollection;
    }

    @Override
    public void onMissionGridUpdated(MissionGrid grid) {
        this.grid = grid;
        // Save button is enabled only when grid is complete and valid (MissionLayout mode)
        // In Mission editing mode, save button is always enabled (set in onMissionSelected)
        if (this.currentMission == null) {
            // We are in MissionLayout mode (creating new mission)
            saveMissionButton.setEnabled(grid.isCompleteAndValid());
        }
        // If currentMission != null, we're editing an existing mission, keep save button enabled
    }

    @Override
    public void onMissionNameUpdated(String missionName) {
        this.newMissionName = missionName;
    }

    private void onDeleteMission() {
        if (currentMission == null) {
            logger.warn("No mission selected to delete");
            return;
        }

        String missionName = currentMission.getMissionName();
        
        // Show confirmation dialog
        int confirmation = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que quieres eliminar la misión '" + missionName + "'?",
            "Confirmar eliminación",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == javax.swing.JOptionPane.YES_OPTION) {
            logger.info("Deleting mission: {}", missionName);
            
            boolean deleted = persistanceService.deleteMission(selectedEdition, selectedCollection, missionName);
            
            if (deleted) {
                logger.info("Mission deleted successfully: {}", missionName);
                
                // Clear current mission
                currentMission = null;
                deleteMissionButton.setEnabled(false);
                exportImageButton.setEnabled(false);
                saveMissionButton.setEnabled(false);
                
                // Reload missions list
                loadMissions();
                
                // Show success toast
                ToastManager.getInstance().showSuccess("Mission '" + missionName + "' deleted successfully!");
            } else {
                logger.error("Failed to delete mission: {}", missionName);
                ToastManager.getInstance().showError("Failed to delete mission");
            }
        } else {
            logger.info("Mission deletion cancelled by user");
        }
    }

    /**
     * Clears the work area and resets all buttons when edition or collection changes
     */
    private void clearWorkArea() {
        // Clear current mission
        currentMission = null;
        grid = null;
        newMissionName = null;
        
        // Disable all action buttons
        saveMissionButton.setEnabled(false);
        exportImageButton.setEnabled(false);
        deleteMissionButton.setEnabled(false);
        
        // Clear mission list selection
        missionList.clearSelection();
        
        // Notify listeners to clear their displays
        for (MissionSelectionListener listener : missionSelectionListeners) {
            if (listener instanceof ZoneWorkAreaMissions) {
                ((ZoneWorkAreaMissions) listener).clear();
            }
        }
        
        logger.info("Work area cleared");
    }

    private void onExportImage() {
        if (currentMission == null) {
            logger.warn("No mission selected to export");
            return;
        }

        String missionName = currentMission.getMissionName();
        
        // Create file chooser
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Exportar imagen de la misión");
        fileChooser.setSelectedFile(new java.io.File(missionName + ".png"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure .png extension
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".png");
            }
            
            logger.info("Exporting mission image to: {}", fileToSave.getAbsolutePath());
            
            boolean exported = persistanceService.exportMissionImage(currentMission, fileToSave.getAbsolutePath());
            
            if (exported) {
                logger.info("Mission image exported successfully");
                ToastManager.getInstance().showSuccess("Mission image exported to " + fileToSave.getName());
            } else {
                logger.error("Failed to export mission image");
                ToastManager.getInstance().showError("Failed to export mission image");
            }
        } else {
            logger.info("Export cancelled by user");
        }
    }

}
