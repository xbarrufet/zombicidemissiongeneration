package com.zombicide.missiongen.ui.interfaces;

import com.zombicide.missiongen.model.tokens.Token;
public interface MissionTokenSelectionListener {

    void onTokenSelected(Token token);
    void onTokenUnSelected();
}
