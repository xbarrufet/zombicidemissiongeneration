package com.zombicide.missiongen.ui.missions;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.board.MissionBoard;
import com.zombicide.missiongen.model.tokens.Token;
import com.zombicide.missiongen.model.tokens.TokenType;

public class PanelTokenTree extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(PanelTokenTree.class);
    
    private JButton addNewTokenButton;
    private JTree tokenTree;
    private JScrollPane tokenTreeScrollPane;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private MissionBoard missionBoard;
    
    public interface AddTokenListener {
        void onAddTokenRequested();
    }
    
    private AddTokenListener addTokenListener;
    
    public PanelTokenTree() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setOpaque(false);
        
        addNewTokenButton = new JButton("Add New Token");
        addNewTokenButton.addActionListener(e -> {
            if (addTokenListener != null) {
                addTokenListener.onAddTokenRequested();
            }
        });
        
        rootNode = new DefaultMutableTreeNode("Tokens");
        treeModel = new DefaultTreeModel(rootNode);
        tokenTree = new JTree(treeModel);
        tokenTreeScrollPane = new JScrollPane(tokenTree);
        tokenTreeScrollPane.setBorder(null);
        tokenTreeScrollPane.setOpaque(false);
        tokenTreeScrollPane.getViewport().setOpaque(false);
        tokenTree.setOpaque(false);
        tokenTree.setRootVisible(false);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        add(addNewTokenButton, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(tokenTreeScrollPane, gbc);
    }
    
    public void setMissionBoard(MissionBoard missionBoard) {
        this.missionBoard = missionBoard;
        updateTokenTree();
    }
    
    public void setAddTokenListener(AddTokenListener listener) {
        this.addTokenListener = listener;
    }
    
    public void updateTokenTree() {
        rootNode.removeAllChildren();
        if (missionBoard != null) {
            Map<TokenType, Map<String, Integer>> tokenCounts = new HashMap<>();

            for (Token token : missionBoard.getTokens()) {
                tokenCounts.putIfAbsent(token.getType(), new HashMap<>());
                Map<String, Integer> subtypeCounts = tokenCounts.get(token.getType());
                subtypeCounts.put(token.getSubtype(), subtypeCounts.getOrDefault(token.getSubtype(), 0) + 1);
            }

            for (TokenType type : tokenCounts.keySet()) {
                DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(type.name());
                rootNode.add(typeNode);

                Map<String, Integer> subtypes = tokenCounts.get(type);
                for (Map.Entry<String, Integer> entry : subtypes.entrySet()) {
                    typeNode.add(new DefaultMutableTreeNode(entry.getKey() + " (" + entry.getValue() + ")"));
                }
            }
        }
        treeModel.reload();
        // Expand all rows
        for (int i = 0; i < tokenTree.getRowCount(); i++) {
            tokenTree.expandRow(i);
        }
        
        logger.debug("Token tree updated with {} tokens", missionBoard != null ? missionBoard.getTokens().size() : 0);
    }
}
