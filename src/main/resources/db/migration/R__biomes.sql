CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Biomes

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Snow', x'ffffff'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Tundra', x'bbbbaa'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Bare', x'888888'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Scorched', x'555555'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Taiga', x'99aa77'::int, 'middleCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Shrubland', x'889977'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Temperate Desert', x'c9d29b'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Temperate Rain Forest', x'448855'::int, 'halfNewestHalfRandomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Temperate Deciduous Forest', x'679459'::int, 'halfNewestHalfRandomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Grassland', x'88aa55'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Subtropical Desert', x'd2b98b'::int, 'randomCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Tropical Rain Forest', x'337755'::int, 'newestCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

INSERT INTO biome (id, name, color, cell_selection_strategy)
VALUES (uuid_generate_v1mc(), 'Tropical Seasonal Forest', x'559944'::int, 'newestCellSelectionStrategy') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, color=EXCLUDED.color, cell_selection_strategy=EXCLUDED.cell_selection_strategy;

-- Whittaker Grid Locations

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 1, (SELECT id FROM biome WHERE name='Subtropical Desert')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 2, (SELECT id FROM biome WHERE name='Grassland')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 3, (SELECT id FROM biome WHERE name='Tropical Seasonal Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 4, (SELECT id FROM biome WHERE name='Tropical Seasonal Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 5, (SELECT id FROM biome WHERE name='Tropical Rain Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 1, 6, (SELECT id FROM biome WHERE name='Tropical Rain Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 1, (SELECT id FROM biome WHERE name='Temperate Desert')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 2, (SELECT id FROM biome WHERE name='Grassland')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 3, (SELECT id FROM biome WHERE name='Grassland')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 4, (SELECT id FROM biome WHERE name='Temperate Deciduous Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 5, (SELECT id FROM biome WHERE name='Temperate Deciduous Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 2, 6, (SELECT id FROM biome WHERE name='Temperate Rain Forest')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 1, (SELECT id FROM biome WHERE name='Temperate Desert')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 2, (SELECT id FROM biome WHERE name='Temperate Desert')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 3, (SELECT id FROM biome WHERE name='Shrubland')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 4, (SELECT id FROM biome WHERE name='Shrubland')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 5, (SELECT id FROM biome WHERE name='Taiga')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 3, 6, (SELECT id FROM biome WHERE name='Taiga')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 1, (SELECT id FROM biome WHERE name='Scorched')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 2, (SELECT id FROM biome WHERE name='Bare')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 3, (SELECT id FROM biome WHERE name='Tundra')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 4, (SELECT id FROM biome WHERE name='Snow')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 5, (SELECT id FROM biome WHERE name='Snow')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;

INSERT INTO whittaker_grid_location (id, elevation, moisture, biome_id)
VALUES (uuid_generate_v1mc(), 4, 6, (SELECT id FROM biome WHERE name='Snow')) ON CONFLICT (elevation, moisture) DO
UPDATE SET elevation=EXCLUDED.elevation, moisture=EXCLUDED.moisture, biome_id=EXCLUDED.biome_id;