package com.zombicide.missiongen.DTO;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zombicide.missiongen.model.areas.BoardArea;
import com.zombicide.missiongen.model.areas.BoardArea.AreaType;
import com.zombicide.missiongen.model.areas.AreaLocation;

public class BoardAreaDTOTest {

    @Test
    public void testBoardAreaSerialization() throws Exception {
        // Create a BoardArea
        UUID id = UUID.randomUUID();
        BoardArea area = new BoardArea(id, new Point(10, 20), 100, 100, AreaType.INDOOR_LIGHT.toString(),
                AreaLocation.TOP_LEFT_STREET);

        // Convert to DTO
        BoardAreaDTO dto = BoardAreaDTO.fromBoardArea(area);

        // Verify DTO fields
        assertEquals(id.toString(), dto.id);
        assertEquals(10, dto.x);
        assertEquals(20, dto.y);
        assertEquals(100, dto.width);
        assertEquals(100, dto.height);
        assertEquals("INDOOR_LIGHT", dto.areaType);
        assertEquals("TOP_LEFT_STREET", dto.areaLocation);

        // Serialize to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(dto);

        // Verify JSON contains fields
        assertTrue(json.contains("\"id\" : \"" + id.toString() + "\""));
        assertTrue(json.contains("\"x\" : 10"));
        assertTrue(json.contains("\"y\" : 20"));
        assertTrue(json.contains("\"width\" : 100"));
        assertTrue(json.contains("\"height\" : 100"));
        assertTrue(json.contains("\"areaType\" : \"INDOOR_LIGHT\""));
        assertTrue(json.contains("\"areaLocation\" : \"TOP_LEFT_STREET\""));

        // Deserialize back
        BoardAreaDTO deserializedDto = mapper.readValue(json, BoardAreaDTO.class);
        assertEquals(id.toString(), deserializedDto.id);
        assertEquals(10, deserializedDto.x);
        assertEquals(20, deserializedDto.y);
        assertEquals(100, deserializedDto.width);
        assertEquals(100, deserializedDto.height);
        assertEquals("INDOOR_LIGHT", deserializedDto.areaType);
        assertEquals("TOP_LEFT_STREET", deserializedDto.areaLocation);

        // Convert back to BoardArea
        BoardArea deserializedArea = BoardArea.fromBoardAreaDTO(deserializedDto);
        assertEquals(id, deserializedArea.getAreaId());
        assertEquals(10, deserializedArea.getTopLeft().x);
        assertEquals(20, deserializedArea.getTopLeft().y);
        assertEquals(100, deserializedArea.getWidth());
        assertEquals(100, deserializedArea.getHeight());
        assertEquals(AreaType.INDOOR_LIGHT, deserializedArea.getAreaType());
        assertEquals(AreaLocation.TOP_LEFT_STREET, deserializedArea.getAreaLocation());
    }
}
