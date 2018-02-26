CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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

