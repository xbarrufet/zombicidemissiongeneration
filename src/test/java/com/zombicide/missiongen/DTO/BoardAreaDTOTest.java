package com.zombicide.missiongen.DTO;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.elements.BoardGameAsset;

public class BoardAreaDTOTest {

    @Test
    public void testBoardGameAssetsSerialization() throws Exception {
        // Create a BoardArea with boardGameAssets
        BoardArea area = new BoardArea(java.util.UUID.randomUUID(), new Point(10, 20), 100, 100, "INDOOR");
        area.addBoardGameAsset(BoardGameAsset.GoalMarker);
        area.addBoardGameAsset(BoardGameAsset.PimpWeapon);

        // Convert to DTO
        BoardAreaDTO dto = BoardAreaDTO.fromBoardArea(area);

        // Verify DTO has the assets
        assertNotNull(dto.boardGameAssets);
        assertEquals(2, dto.boardGameAssets.size());
        assertTrue(dto.boardGameAssets.contains("GoalMarker"));
        assertTrue(dto.boardGameAssets.contains("PimpWeapon"));

        // Serialize to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(dto);

        // Verify JSON contains boardGameAssets
        assertTrue(json.contains("boardGameAssets"));
        assertTrue(json.contains("GoalMarker"));
        assertTrue(json.contains("PimpWeapon"));

        System.out.println("Serialized JSON:");
        System.out.println(json);

        // Deserialize back
        BoardAreaDTO deserializedDto = mapper.readValue(json, BoardAreaDTO.class);
        assertNotNull(deserializedDto.boardGameAssets);
        assertEquals(2, deserializedDto.boardGameAssets.size());

        // Convert back to BoardArea
        BoardArea deserializedArea = BoardArea.fromBoardAreaDTO(deserializedDto);
        assertNotNull(deserializedArea.getBoardGameAssets());
        assertEquals(2, deserializedArea.getBoardGameAssets().size());
        assertTrue(deserializedArea.getBoardGameAssets().contains(BoardGameAsset.GoalMarker));
        assertTrue(deserializedArea.getBoardGameAssets().contains(BoardGameAsset.PimpWeapon));
    }

    @Test
    public void testBoardGameAssetsDeserializationWithMissingField() throws Exception {
        // Simulate old JSON without boardGameAssets field
        String oldJson = "{"
                + "\"id\": \"550e8400-e29b-41d4-a716-446655440000\","
                + "\"x\": 10,"
                + "\"y\": 20,"
                + "\"width\": 100,"
                + "\"height\": 100,"
                + "\"areaType\": \"INDOOR\""
                + "}";

        ObjectMapper mapper = new ObjectMapper();
        BoardAreaDTO dto = mapper.readValue(oldJson, BoardAreaDTO.class);

        // boardGameAssets should be null when not present in JSON
        assertNull(dto.boardGameAssets);

        // Convert to BoardArea - should handle null gracefully
        BoardArea area = BoardArea.fromBoardAreaDTO(dto);
        assertNotNull(area.getBoardGameAssets());
        assertEquals(0, area.getBoardGameAssets().size());
    }
}
