# Zombicide Mission Generation - AI Agent Guide

## Document Purpose
This document provides a comprehensive reverse-engineered overview of the Zombicide Mission Generation application to assist AI agents in understanding the codebase and fulfilling development requests effectively.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Core Domain Model](#core-domain-model)
5. [Data Flow & Architecture](#data-flow--architecture)
6. [Key Subsystems](#key-subsystems)
7. [Configuration Management](#configuration-management)
8. [File Formats & DTOs](#file-formats--dtos)
9. [UI Architecture](#ui-architecture)
10. [Common Development Tasks](#common-development-tasks)
11. [Build & Run](#build--run)
12. [Testing Strategy](#testing-strategy)
13. [Known Patterns & Conventions](#known-patterns--conventions)

---

## Project Overview

**Application Name**: Zombicide Mission Generation  
**Purpose**: A Java Swing desktop application for creating, editing, and managing Zombicide board game missions. It allows users to compose missions from tiles, place tokens, and visualize the game board layout.

**Primary Capabilities**:
- Load and manage game editions and collections (e.g., 2nd Edition, Black Plague)
- Display and edit individual tiles with areas and connections
- Create multi-tile mission layouts on a grid
- Place and manage various game tokens (doors, spawns, objectives, etc.)
- Persist tiles and missions as JSON files
- Generate visual representations of missions

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17 |
| **Build Tool** | Maven | 3.x |
| **UI Framework** | Java Swing | - |
| **Logging** | SLF4J + Logback | 2.0.7 / 1.4.11 |
| **JSON Processing** | Jackson (Databind) | 2.15.2 |
| **Testing** | JUnit 4 + Mockito | 4.13.2 / 5.14.2 |

**Maven Coordinates**:
- Group ID: `com.zombicide.missiongen`
- Artifact ID: `mission-generation`
- Version: `1.0-SNAPSHOT`

---

## Project Structure

```
MissionGeneration/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/com/zombicide/missiongen/
│   │   │   ├── App.java             # Application entry point
│   │   │   ├── MainWindow.java      # Main application window
│   │   │   ├── config/              # Configuration loaders
│   │   │   │   ├── ConfigLoader.java    # Singleton for app properties
│   │   │   │   └── TokenLoader.java     # Singleton for token config
│   │   │   ├── DTO/                 # Data Transfer Objects
│   │   │   │   ├── TileDTO.java
│   │   │   │   ├── MissionDTO.java
│   │   │   │   ├── BoardAreaDTO.java
│   │   │   │   └── BoardAreaConnectionDTO.java
│   │   │   ├── model/               # Core domain models
│   │   │   │   ├── Tile.java
│   │   │   │   ├── Mission.java
│   │   │   │   ├── areas/           # Area-related models
│   │   │   │   ├── board/           # Board implementations
│   │   │   │   ├── tokens/          # Token models
│   │   │   │   └── helpers/         # Utility classes
│   │   │   ├── services/            # Business logic services
│   │   │   │   └── PersistanceService.java
│   │   │   ├── ui/                  # UI components
│   │   │   │   ├── components/      # Reusable UI components
│   │   │   │   ├── interfaces/      # Listener interfaces
│   │   │   │   ├── tiles/           # Tile editor UI
│   │   │   │   ├── missions/        # Mission editor UI
│   │   │   │   ├── missionLayout/   # Mission layout UI
│   │   │   │   └── boardAssets/     # Board rendering assets
│   │   │   └── observers/           # Observer pattern implementations
│   │   └── resources/
│   │       ├── application.properties   # App configuration
│   │       ├── logback.xml             # Logging configuration
│   │       └── images/tokens/          # Token images
│   └── test/                        # Unit tests
├── assets/                          # Game data
│   └── editions/                    # Organized by edition/collection
│       ├── 2ndEdition/
│       │   └── 0_original/
│       │       ├── tiles/            # Tile JSON definitions
│       │       ├── tileImages/       # Tile background images
│       │       ├── missions/         # Mission JSON definitions
│       │       └── missionImages/    # Mission images
│       └── BlackPlague/
├── logs/                            # Application logs
├── memorybank/                      # Development notes & knowledge
│   ├── grid_token_panel.md
│   └── mission. board open connections.md
└── target/                          # Maven build output
```

---

## Core Domain Model

### Key Entities

#### 1. **Tile** (`model/Tile.java`)
Represents a single game board tile.

**Attributes**:
- `edition`: Edition identifier (e.g., "2ndEdition")
- `collection`: Collection name (e.g., "0_original")
- `imagePath`: Path to the tile's background image
- `tileName`: Unique tile identifier (e.g., "1R", "2V")
- `tileBoard`: Associated TileBoard instance

**Responsibilities**:
- Load tile data from JSON (via DTO conversion)
- Manage board areas and connections
- Provide access to tile metadata

#### 2. **Mission** (`model/Mission.java`)
Represents a complete mission composed of multiple tiles.

**Attributes**:
- `rows`, `cols`: Grid dimensions
- `width`, `height`: Pixel dimensions
- `edition`, `collection`: Metadata
- `missionName`: Unique identifier
- `missionBoard`: Associated MissionBoard instance

**Responsibilities**:
- Compose multiple tiles into a cohesive mission layout
- Manage mission-level areas and connections
- Generate mission images

#### 3. **BoardArea** (`model/areas/BoardArea.java`)
Represents a playable zone within a tile or mission.

**Attributes**:
- `id`: UUID identifier
- `x`, `y`, `width`, `height`: Bounding box
- `areaType`: OUTDOOR, INDOOR_LIGHT, INDOOR_DARK
- `areaLocation`: Position enum (e.g., TOP_LEFT_STREET, OTHER)

**Enums**:
```java
public enum AreaType {
    OUTDOOR, INDOOR_LIGHT, INDOOR_DARK
}
```

#### 4. **BoardAreaConnection** (`model/areas/BoardAreaConnection.java`)
Represents connections between areas (doors, passages, etc.).

**Attributes**:
- `areaA`: Source area (UUID or reference)
- `areaB`: Destination area (can be null for edge connections)
- `direction`: Direction enum (for edge connections)

**Connection Types**:
- **Internal**: Both areaA and areaB are defined (connections within a tile)
- **Edge**: areaB is null, direction specified (connections to adjacent tiles)

#### 5. **Token** (`model/tokens/Token.java`)
Abstract base class for game tokens.

**Token Types** (from `TokenType` enum):
- DOOR, EXIT, SPAWN, OBJECTIVE
- OBSTACLE, SWITCH, VEHICLE
- NUMBER, SKILL, COUNTER, TENTCARD
- DOORSPECIAL, OTHER, PIMPWEAPONCRATE

**Key Properties**:
- Position (x, y)
- Dimensions (width, height)
- Orientation (HORIZONTAL, VERTICAL)
- Shape (RECTANGULAR, CIRCULAR, etc.)
- Image path

### Boards Architecture

```
BaseBoard (abstract)
├── TileBoard
│   └── Represents single tile (250x250px)
└── MissionBoard
    └── Represents mission grid (NxM tiles)
```

**BaseBoard** provides:
- Area management (add, remove, get)
- Connection management
- Rendering logic
- Mouse interaction handling

---

## Data Flow & Architecture

### Application Startup Flow

```
App.main()
  ├── Initialize logging (SLF4J/Logback)
  ├── Set System Look & Feel
  ├── Initialize singletons
  │   ├── ConfigLoader.getInstance()
  │   └── TokenLoader.getInstance()
  └── MainWindow.display()
      ├── Load editions from assets/editions/
      ├── Initialize UI panels
      └── Set up event listeners
```

### Tile Loading Flow

```
User selects edition/collection
  └── PersistanceService.listTileFiles()
      └── Scans assets/editions/{edition}/{collection}/tiles/*.json
          └── For each JSON file:
              ├── Parse JSON → TileDTO
              ├── Convert DTO → Tile model
              │   ├── Load background image
              │   └── Create TileBoard
              └── Populate areas & connections
```

### Mission Creation Flow

```
User creates new mission
  └── MissionFactoryService.createMission()
      ├── Define grid dimensions (rows × cols)
      ├── Create MissionBoard
      ├── User places tiles in grid
      │   └── For each tile placement:
      │       ├── Copy tile areas to mission coordinates
      │       └── Create edge connections
      └── MissionFactoryService.resolveEdgeConnections()
          ├── Match edge connections between adjacent tiles
          │   ├── NORTH ↔ SOUTH
          │   ├── EAST ↔ WEST
          │   └── Corners (TOP_LEFT ↔ BOTTOM_RIGHT, etc.)
          └── Create BoardAreaConnection entries
```

### Token Placement Flow

```
User drags token from palette
  └── TokenTransferable (implements Transferable)
      └── Drop on board area
          ├── Calculate position within area
          ├── Create Token instance
          ├── Add to area's token list
          └── Repaint board
```

---

## Key Subsystems

### 1. Configuration Management

#### ConfigLoader (`config/ConfigLoader.java`)
**Pattern**: Singleton  
**Source**: `resources/application.properties`

**Key Properties**:
```properties
folders.editions=assets/editions
folders.tiles=tiles
folders.missions=missions
tile.width=250
tile.height=250
tile.area.corner_width=75
tile.area.corner_height=75
tile.area.middle_width=100
```

**Usage**:
```java
ConfigLoader config = ConfigLoader.getInstance();
String editionsPath = config.getProperty("folders.editions");
int tileWidth = Integer.parseInt(config.getProperty("tile.width"));
```

#### TokenLoader (`config/TokenLoader.java`)
**Pattern**: Singleton  
**Source**: `resources/application.properties` (token section)

**Key Properties**:
```properties
image.tokendimension.DOOR = 30x25
image.tokendimension.EXIT = 50x25
image.token.DOOR.DoorPinkClosed = /images/tokens/Doors/DoorPinkClosed.jpg
image.token.EXIT.ExitToken = /images/tokens/Exit/ExitToken.png
```

**Usage**:
```java
TokenLoader tokenLoader = TokenLoader.getInstance();
Dimension doorSize = tokenLoader.getTokenDimension(TokenType.DOOR);
Image exitImage = tokenLoader.getTokenImage(TokenType.EXIT, "ExitToken");
```

### 2. Persistence Service

#### PersistanceService (`services/PersistanceService.java`)
**Responsibilities**:
- List available editions and collections
- Load/save tiles from/to JSON
- Load/save missions from/to JSON
- File system operations

**Key Methods**:
```java
List<String> listEditions()
List<String> listCollections(String edition)
List<File> listTileFiles(String edition, String collection)
Tile loadTile(File tileFile)
void saveTile(Tile tile, File outputFile)
Mission loadMission(File missionFile)
void saveMission(Mission mission, File outputFile)
```

**JSON Mapping**: Uses Jackson `ObjectMapper` for serialization

### 3. Board Factory Service

#### MissionFactoryService (`model/board/MissionFactoryService.java`)
**Responsibilities**:
- Create missions from scratch
- Compose tiles into missions
- Resolve edge connections between tiles
- Handle coordinate transformations

**Key Methods**:
```java
Mission createMission(int rows, int cols, String edition, String collection)
void placeTileAt(Mission mission, Tile tile, int row, int col)
void resolveEdgeConnections(Mission mission)
```

**Edge Connection Resolution Algorithm**:
1. For each tile in the grid
2. For each edge connection with a direction
3. Find the adjacent tile in that direction
4. Find matching opposite direction in adjacent tile
5. Create connection between the two areas

### 4. Area Factory

#### BoardAreaFactory (`model/areas/BoardAreaFactory.java`)
**Responsibilities**:
- Create BoardArea instances from DTOs
- Create BoardAreaConnection instances
- Validate area configurations

---

## File Formats & DTOs

### Tile JSON Format

**Location**: `assets/editions/{edition}/{collection}/tiles/{tileName}.json`

**Schema**:
```json
{
  "edition": "2ndEdition",
  "collection": "0_original",
  "imagePath": "assets/editions/2ndEdition/0_original/tileImages/1R.png",
  "boardId": "2ndEdition.0_original.1R",
  "tileName": "1R",
  "areas": [
    {
      "id": "uuid",
      "x": 0,
      "y": 0,
      "width": 75,
      "height": 75,
      "areaType": "OUTDOOR",
      "areaLocation": "TOP_LEFT_STREET"
    }
  ],
  "connections": [
    {
      "areaA": "uuid-of-area-a",
      "areaB": "uuid-of-area-b",
      "direction": null
    },
    {
      "areaA": "uuid",
      "areaB": null,
      "direction": "SOUTH_CENTER"
    }
  ]
}
```

**DTO Mapping**: `TileDTO` → `Tile`

### Mission JSON Format

**Location**: `assets/editions/{edition}/{collection}/missions/{missionName}.json`

**Schema**:
```json
{
  "rows": 2,
  "cols": 1,
  "width": 500,
  "height": 250,
  "edition": "2ndEdition",
  "collection": "0_original",
  "imagePath": "assets/editions/2ndEdition/0_original/missionImages/mission_mission1.png",
  "missionName": "mission1",
  "areas": [],
  "connections": []
}
```

**Note**: Mission areas and connections are typically generated dynamically from constituent tiles.

---

## UI Architecture

### Main Window Structure

```
MainWindow (JFrame)
├── LayoutChangeListener implementation
├── menuBar
│   ├── File menu (New, Open, Save, Exit)
│   ├── Edit menu
│   └── Help menu
├── toolBar (top)
├── workAreaPanel (center) - swappable
│   ├── ZoneSelecionMissions (mission browser)
│   ├── ZoneWorkAreaMissionLayout (mission editor)
│   └── ZoneTileDraw (tile editor)
└── statusBar (bottom)
```

### Key UI Panels

#### 1. ZoneSelecionMissions
- Lists available missions
- Provides mission selection
- Triggers mission loading

#### 2. ZoneWorkAreaMissionLayout
- Main mission editing interface
- Grid-based tile placement
- Token palette
- Mission properties panel

#### 3. ZoneTileDraw
- Tile editing interface
- Area drawing/editing
- Connection management

#### 4. ZoneMissionDraw
- Visual mission board renderer
- Handles drag-and-drop for tokens
- Supports zoom and pan

### UI Patterns

**Observer Pattern**: Extensively used via custom listener interfaces
```java
// Common listener interfaces
LayoutChangeListener
BoardSelectionListener
TileGridListener
MissionPropertiesListener
PanelSelectionListener
AreaDrawerListener
MissionLayoutUpdate
```

**Drag & Drop**: Implemented for token placement
```java
TokenTransferable implements Transferable
// Used with Java's DnD framework
```

**Event Flow Example**:
```
User clicks tile in palette
  └── TileGridListener.onTileSelected()
      └── ZoneWorkAreaMissionLayout.handleTileSelection()
          └── Update selected tile in state
              └── Enable placement mode
```

---

## Configuration Management

### Application Properties Structure

The `application.properties` file is organized into sections:

1. **Folder Configuration**: Paths to assets
2. **Tile Dimensions**: Physical tile sizes
3. **Token Dimensions**: Size specifications per token type
4. **Token Images**: Paths to token image resources

**Example Configuration Blocks**:
```properties
# Folders
folders.editions=assets/editions
folders.tiles=tiles

# Tile geometry
tile.width=250
tile.height=250
tile.area.corner_width=75

# Token dimensions
image.tokendimension.DOOR = 30x25
image.tokendimension.SPAWN = 50x25

# Token image paths
image.token.DOOR.DoorPinkClosed = /images/tokens/Doors/DoorPinkClosed.jpg
```

### Logging Configuration

**Location**: `src/main/resources/logback.xml`

**Log Output**:
- Console appender (INFO level)
- File appender: `logs/application.log` (DEBUG level)

**Usage**:
```java
private static final Logger logger = LoggerFactory.getLogger(ClassName.class);
logger.info("Message");
logger.debug("Debug info");
logger.error("Error occurred", exception);
```

---

## Common Development Tasks

### Adding a New Token Type

1. **Add to TokenType enum** (`model/tokens/TokenType.java`)
   ```java
   public enum TokenType {
       // ... existing types
       NEW_TOKEN_TYPE
   }
   ```

2. **Add dimension to properties** (`application.properties`)
   ```properties
   image.tokendimension.NEW_TOKEN_TYPE = 40x30
   ```

3. **Add token images and paths**
   ```properties
   image.token.NEW_TOKEN_TYPE.TokenName = /images/tokens/NewType/TokenName.png
   ```

4. **Create token class** (if specialized behavior needed)
   ```java
   public class NewTokenType extends Token {
       // Custom implementation
   }
   ```

5. **Update TokenFactory** if custom class created

### Adding a New Area Location

1. **Add to AreaLocation enum** (`model/areas/AreaLocation.java`)
   ```java
   public enum AreaLocation {
       // ... existing locations
       NEW_LOCATION
   }
   ```

2. **Update area drawing logic** in UI components

3. **Update connection resolution** in `MissionFactoryService` if needed

### Creating a New Board Type

1. **Extend BaseBoard**
   ```java
   public class CustomBoard extends BaseBoard {
       @Override
       protected void specificMethod() {
           // Implementation
       }
   }
   ```

2. **Implement required abstract methods**

3. **Add factory method** in appropriate service

### Loading a Custom Edition

1. **Create directory structure**:
   ```
   assets/editions/{editionName}/{collectionName}/
   ├── tiles/
   ├── tileImages/
   ├── missions/
   └── missionImages/
   ```

2. **Add tile JSON files** following the schema

3. **Restart application** (or implement dynamic reload)

---

## Build & Run

### Maven Tasks

```bash
# Compile the project
mvn compile

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package

# Clean install (includes tests)
mvn clean install

# Run the application
mvn exec:java -Dexec.mainClass="com.zombicide.missiongen.App"
```

### VS Code Tasks

Pre-configured tasks available (see `.vscode/tasks.json`):
- `maven: compile`
- `maven: clean compile`
- `maven: test`
- `maven: clean test`
- `maven: package`
- `maven: clean install`
- `maven: run app`
- `maven: clean`

### Running from IDE

**Main Class**: `com.zombicide.missiongen.App`

**VM Arguments**: (none required)

**Program Arguments**: (none currently used)

---

## Testing Strategy

### Current Test Framework
- **JUnit 4.13.2** for test structure
- **Mockito 5.14.2** for mocking dependencies

### Test Structure
```
src/test/java/com/zombicide/missiongen/
└── (mirrors main package structure)
```

### Testing Recommendations

**Unit Tests**:
- Test DTOs for serialization/deserialization
- Test model classes for business logic
- Test service classes with mocked dependencies

**Integration Tests**:
- Test PersistanceService with actual JSON files
- Test MissionFactoryService with real tiles
- Test edge connection resolution

**UI Tests**:
- Manual testing recommended for Swing components
- Consider automated screenshot comparison for board rendering

**Example Test Pattern**:
```java
@Test
public void testTileLoadingFromJSON() {
    // Arrange
    PersistanceService service = new PersistanceService();
    File tileFile = new File("test-assets/test-tile.json");
    
    // Act
    Tile tile = service.loadTile(tileFile);
    
    // Assert
    assertNotNull(tile);
    assertEquals("2ndEdition", tile.getEdition());
    assertEquals(9, tile.getBoard().getAreas().size());
}
```

---

## Known Patterns & Conventions

### Naming Conventions

- **Classes**: PascalCase (e.g., `MissionBoard`, `TileDTO`)
- **Methods**: camelCase (e.g., `loadTile`, `getAreaById`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `TILE_WIDTH`, `MAX_ROWS`)
- **Packages**: lowercase (e.g., `com.zombicide.missiongen.model`)

### File Naming

- **Tiles**: `{number}{R|V}.json` (e.g., `1R.json`, `2V.json`)
  - R = Recto (front), V = Verso (back)
- **Missions**: `mission{number}.json` (e.g., `mission1.json`)
- **Images**: Match JSON names with `.png` extension

### Design Patterns Used

1. **Singleton**: ConfigLoader, TokenLoader
2. **Factory**: TokenFactory, BoardAreaFactory, MissionFactoryService
3. **Observer**: Multiple listener interfaces for UI events
4. **DTO (Data Transfer Object)**: For JSON serialization
5. **Abstract Base Class**: BaseBoard, Token

### Code Organization Principles

- **Package by Feature**: Related classes grouped in packages (areas, board, tokens)
- **Separation of Concerns**: Model, UI, Services clearly separated
- **Configuration Externalization**: Properties files for configurable values
- **Lazy Loading**: Tiles/missions loaded on-demand

---

## Memory Bank / Development Notes

The `memorybank/` directory contains development notes and design decisions:

### grid_token_panel.md
- **Topic**: Token grid panel implementation
- **Key Requirements**:
  - Display token images in a grid
  - `iconsPerRow` determines grid layout
  - Images maintain aspect ratio
  - Panel auto-sizes to content

### mission. board open connections.md
- **Topic**: Edge connection implementation for mission boards
- **Algorithm**:
  - Each edge connection has an area A and a direction
  - **Case 1**: Direction points to STREET_LOCATION → direct mapping
  - **Case 2**: Direction points to INDOOR → requires matching opposite direction

---

## AI Agent Development Guidelines

### When Modifying Code

1. **Read relevant DTOs first** to understand data structures
2. **Check ConfigLoader/TokenLoader** for configurable values
3. **Review existing patterns** before introducing new approaches
4. **Maintain consistency** with naming conventions
5. **Update corresponding DTO** if model changes
6. **Add logging** for debugging and traceability
7. **Handle exceptions** gracefully with proper error messages

### When Adding Features

1. **Identify affected layers**: Model, Service, UI
2. **Design DTO first** if persistence is involved
3. **Update properties file** for configuration
4. **Create/update factory methods** for object creation
5. **Implement listener interface** if UI events needed
6. **Add to menu/toolbar** if user-facing feature
7. **Write tests** for new functionality

### When Debugging

1. **Check logs** in `logs/` directory
2. **Verify JSON format** against DTO structure
3. **Inspect asset paths** in properties file
4. **Use logger.debug()** liberally during investigation
5. **Check for null connections** in area linking
6. **Validate coordinate transformations** in grid operations

### Common Pitfalls

- **Forgetting to load ConfigLoader/TokenLoader** before accessing properties
- **Not handling null areaB** in connections (edge connections)
- **Coordinate system confusion** (tile-local vs. mission-global)
- **Image path issues** (relative vs. absolute, classpath vs. filesystem)
- **Event listener registration** (forgetting to add/remove listeners)

---

## Quick Reference

### Key File Locations

| Resource | Path |
|----------|------|
| Main entry point | `src/main/java/com/zombicide/missiongen/App.java` |
| Configuration | `src/main/resources/application.properties` |
| Logging config | `src/main/resources/logback.xml` |
| Tile definitions | `assets/editions/{edition}/{collection}/tiles/` |
| Mission definitions | `assets/editions/{edition}/{collection}/missions/` |
| Token images | `src/main/resources/images/tokens/` |
| Build output | `target/` |
| Application logs | `logs/application.log` |

### Key Classes Quick Reference

| Class | Package | Purpose |
|-------|---------|---------|
| `App` | root | Application entry point |
| `MainWindow` | root | Main application window |
| `Tile` | model | Tile domain model |
| `Mission` | model | Mission domain model |
| `BoardArea` | model.areas | Playable zone model |
| `Token` | model.tokens | Token base class |
| `BaseBoard` | model.board | Abstract board |
| `TileBoard` | model.board | Single tile board |
| `MissionBoard` | model.board | Multi-tile mission board |
| `PersistanceService` | services | JSON load/save operations |
| `MissionFactoryService` | model.board | Mission creation & composition |
| `ConfigLoader` | config | Application configuration singleton |
| `TokenLoader` | config | Token configuration singleton |

---

## Version Information

**Document Version**: 1.0  
**Last Updated**: December 2, 2025  
**Application Version**: 1.0-SNAPSHOT  
**Java Version**: 17  
**Maven Version**: 3.x

---

## Conclusion

This guide provides a comprehensive reverse-engineered view of the Zombicide Mission Generation application. AI agents should use this document as a reference when:

- Understanding the codebase structure
- Implementing new features
- Debugging issues
- Refactoring code
- Answering user questions about the application

For additional context, refer to:
- Source code comments
- Memory bank notes in `memorybank/`
- Maven POM for dependencies
- Application properties for configuration options

**Remember**: Always verify assumptions by reading the actual code, as implementations may have evolved beyond this documentation.
