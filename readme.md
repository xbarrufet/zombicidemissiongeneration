# Zombicide Mission Generation Tool

## Overview

The goal of this project is to have a tool to generate Zombicide style maps in a digital way, ready to be consumed by a future viewer or game. This tool provides a comprehensive editor for creating custom Zombicide missions with precise tile placement, area definitions, and token placement.

## Features

- **Tile Editor**: Define tiles with custom areas and connections
- **Mission Builder**: Combine multiple tiles to create complete mission layouts
- **Token Placement**: Add game elements (spawns, objectives, zombies, etc.) to your missions
- **Export**: Save missions as both JSON (data) and PNG (visual representation)

## How It Works

### 1. Tile Definition

Each Zombicide tile is divided into **areas** that represent distinct zones on the board. The tool allows you to:

- **Define Areas**: Create polygonal areas within a tile by clicking points on the tile image
- **Set Area Connections**: Mark which areas connect to each other (important for movement and line of sight)
- **Configure Edge Connections**: Define open connections on tile edges (North, South, East, West) to link with adjacent tiles

**Tile Structure:**
- Each tile has a reference image (stored in `tileImages/`)
- Areas are defined as polygons with multiple points
- Connections between areas are stored as relationships
- Edge openings allow tiles to connect seamlessly in missions

### 2. Mission Creation

Once tiles are defined, you can create missions by:

- **Selecting an Edition and Collection**: Choose from available Zombicide editions (2nd Edition, Black Plague, etc.)
- **Adding Tiles**: Place tiles on a grid to build your mission layout
- **Arranging Tiles**: Tiles automatically connect based on their edge definitions
- **Setting Properties**: Configure mission-specific settings

### 3. Token Placement

Add game elements to your mission:

- **Token Types**: Spawns, Objectives, Zombies, Doors, Exits, Vehicles, Numbers, Switches, etc.
- **Placement**: Click on the mission board to place selected tokens
- **Rotation**: Press `R` to rotate rectangular tokens
- **Deletion**: Select tokens and delete them from the detail panel
- **Token Tree**: View all placed tokens organized by type

### 4. Saving Missions

Missions are saved in two formats:

- **JSON File**: Contains all mission data (tiles, positions, tokens, areas)
  - Location: `assets/editions/[edition]/[collection]/missions/[missionName].json`
- **PNG Image**: Visual representation of the mission
  - Location: `assets/editions/[edition]/[collection]/missionImages/mission_[missionName].png`

## Project Structure

### Folders

```
MissionGeneration/
├── assets/
│   └── editions/
│       ├── 2ndEdition/
│       │   ├── 0_original/
│       │   │   ├── tiles/              # Tile JSON definitions
│       │   │   ├── tileImages/         # Tile image assets (excluded from git)
│       │   │   ├── missions/           # Saved mission JSON files
│       │   │   └── missionImages/      # Generated mission images (excluded from git)
│       │   ├── high_school/
│       │   └── original/
│       └── BlackPlague/
│           └── original/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/zombicide/missiongen/
│   │   │       ├── config/             # Configuration and loaders
│   │   │       ├── DTO/                # Data transfer objects
│   │   │       ├── model/              # Core domain models
│   │   │       ├── observers/          # Event listeners
│   │   │       ├── services/           # Business logic
│   │   │       └── ui/                 # Swing UI components
│   │   └── resources/
│   │       ├── application.properties  # Token paths and dimensions
│   │       ├── logback.xml            # Logging configuration
│   │       └── images/
│   │           └── tokens/            # Token image assets
│   │               ├── Doors/
│   │               ├── Exit/
│   │               ├── Numbers/
│   │               ├── Objectives/
│   │               ├── PimpWeaponCrate/
│   │               ├── Spawn/
│   │               ├── Start/
│   │               ├── Switches/
│   │               ├── Vehicles/
│   │               └── Zombies/
│   └── test/
├── logs/                               # Application logs
├── memorybank/                         # Design notes and documentation
├── pom.xml                            # Maven project configuration
└── readme.md
```

### Required Assets

#### Tile Images
- **Location**: `assets/editions/[edition]/[collection]/tileImages/`
- **Format**: PNG or JPG
- **Naming**: Matches tile ID (e.g., `1R.png`, `2V.png`)
- **Purpose**: Visual representation of tiles for the editor

#### Tile Definitions
- **Location**: `assets/editions/[edition]/[collection]/tiles/`
- **Format**: JSON
- **Contains**: Area definitions, connections, edge openings
- **Created By**: The tile editor within the application

#### Token Images
- **Location**: `src/main/resources/images/tokens/[tokenType]/`
- **Format**: PNG or JPG
- **Configured In**: `application.properties`
- **Token Types**:
  - `DOOR`: Door tokens (closed/open states, various colors)
  - `EXIT`: Exit markers
  - `NUMBER`: Numbered markers (1-9, different colors)
  - `OBJECTIVE`: Objective markers (various colors)
  - `PIMPWEAPONCRATE`: Pimp weapon crate markers
  - `SPAWN`: Spawn zone markers (various colors, inverted variants)
  - `START`: Starting position marker
  - `SWITCH`: Switch tokens (various colors)
  - `VEHICLES`: Vehicle tokens (cars, helicopters)
  - `ZOMBIE`: Zombie tokens (walkers, runners, fatties, abominations, various types)

## Configuration

### application.properties

This file defines:

- **Token Dimensions**: Size specifications for each token type
  - `SQUARE` tokens: width x height (e.g., `30x25`)
  - `CIRCLE` tokens: diameter x diameter (e.g., `30x30`)
- **Token Image Paths**: Mapping of token subtypes to image files
- **Folder Paths**: Directory structure configuration

Example:
```properties
# Token dimensions
image.tokendimension.DOOR = 30x25
image.tokendimension.ZOMBIE = 30x30

# Token image paths
image.token.DOOR.DoorBlueClosed = /images/tokens/Doors/DoorBlueClosed.jpg
image.token.ZOMBIE.ZombieStandardWalkerToken = /images/tokens/Zombies/ZombieStandardWalkerToken.png
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.x

### Running the Application

1. Clone the repository
2. Ensure required assets are in place (tile images, token images)
3. Build and run:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.zombicide.missiongen.App"
   ```

### Workflow

1. **Create/Edit Tiles**:
   - Select edition and collection
   - Load a tile image
   - Define areas by clicking points
   - Mark area connections
   - Set edge openings
   - Save tile definition

2. **Create Missions**:
   - Go to mission editor
   - Select tiles and add them to the board
   - Arrange tiles in the desired layout
   - Add tokens (spawns, objectives, zombies, etc.)
   - Save mission (creates JSON + PNG)

3. **Manage Missions**:
   - Load existing missions from the mission list
   - Edit and update missions
   - Delete missions (removes both JSON and image)

## Technical Details

- **Framework**: Java Swing for UI
- **Build Tool**: Maven
- **Data Format**: JSON for persistence
- **Image Processing**: Java AWT/ImageIO for rendering
- **Logging**: SLF4J with Logback

## Notes

- Tile images and mission images are excluded from git (see `.gitignore`)
- Token images are included in the repository
- Mission JSON files can be version controlled for sharing custom missions
- The tool supports multiple Zombicide editions and custom collections
