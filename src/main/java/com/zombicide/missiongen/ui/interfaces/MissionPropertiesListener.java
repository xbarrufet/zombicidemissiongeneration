package com.zombicide.missiongen.ui.interfaces;

import com.zombicide.missiongen.model.tokens.Token;

public interface MissionPropertiesListener {

    void onBoardAreasVisibilityUpdated(boolean visible);

    void onTokenSelected(String type, String subtype);

}
