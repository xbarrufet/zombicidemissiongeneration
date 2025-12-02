package com.zombicide.missiongen.ui.components;

import javax.swing.JPanel;

import ch.qos.logback.core.subst.Token;

public class TokenIcon extends JPanel {

    private Token token;
    private float scale;

    public TokenIcon(Token token, float scale) {
        super();
        this.token = token;
        this.scale = scale;
    }

}
