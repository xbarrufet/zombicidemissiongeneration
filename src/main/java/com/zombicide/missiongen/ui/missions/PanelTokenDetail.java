package com.zombicide.missiongen.ui.missions;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zombicide.missiongen.model.tokens.Token;

public class PanelTokenDetail extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(PanelTokenDetail.class);
    
    private JButton addNewTokenButton;
    private JLabel tokenIdLabel;
    private JLabel tokenTypeLabel;
    private JLabel tokenSubtypeLabel;
    private JButton deleteTokenButton;
    private Token currentToken;
    
    public interface TokenActionListener {
        void onAddTokenRequested();
        void onDeleteTokenRequested(Token token);
    }
    
    private TokenActionListener tokenActionListener;
    
    public PanelTokenDetail() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        setOpaque(false);
        
        addNewTokenButton = new JButton("Add New Token");
        addNewTokenButton.addActionListener(e -> {
            if (tokenActionListener != null) {
                tokenActionListener.onAddTokenRequested();
            }
        });
        
        tokenIdLabel = new JLabel();
        tokenTypeLabel = new JLabel();
        tokenSubtypeLabel = new JLabel();
        
        deleteTokenButton = new JButton("Delete Token");
        deleteTokenButton.addActionListener(e -> {
            if (currentToken != null && tokenActionListener != null) {
                String tokenId = currentToken.getId().toString();
                tokenActionListener.onDeleteTokenRequested(currentToken);
                logger.info("Delete token requested: {}", tokenId);
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
        
        add(addNewTokenButton, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(new JLabel("Token ID:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(tokenIdLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(new JLabel("Type:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(tokenTypeLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(new JLabel("Subtype:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(tokenSubtypeLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(deleteTokenButton, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        add(new JPanel(), gbc); // Spacer
    }
    
    public void setTokenActionListener(TokenActionListener listener) {
        this.tokenActionListener = listener;
    }
    
    public void setToken(Token token) {
        this.currentToken = token;
        
        if (token != null) {
            tokenIdLabel.setText(token.getId().toString());
            tokenTypeLabel.setText(token.getType().name());
            tokenSubtypeLabel.setText(token.getSubtype());
            
            logger.info("Token detail panel updated for token: {}", token.getId());
        } else {
            tokenIdLabel.setText("");
            tokenTypeLabel.setText("");
            tokenSubtypeLabel.setText("");
        }
    }
    
    public Token getCurrentToken() {
        return currentToken;
    }
}
