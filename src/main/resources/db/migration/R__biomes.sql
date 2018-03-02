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

-- Biome Descriptions

DELETE FROM biome_description;

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Snow'), 'Rolling white snow drifts rise and fall across the ground here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Snow'), 'A wide plain of mostly undisturbed snow gleams around you.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Snow'), 'The white snow crunches softly beneath your feet. Everything here is covered in white.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tundra'), 'The frozen ground here is scattered with rocks. Occasional moss is the only living thing you see.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tundra'), 'The ground is solid permafrost that crunches under your feet.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tundra'), 'Rocky outcrops protrude from the frozen ground here and there. No trees or bushes can be seen anywhere nearby.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Bare'), 'Bare dirt is all you can see around you. No vegetation can survive in this harsh climate, mostly devoid of nutrients.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Bare'), 'A thin layer of lichen clings tenuously to the thin layer of dirt here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Bare'), 'Some sickly moss has grown up here, but most of the ground is simply dirt, packed and barren.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Scorched'), 'The oppressive heat year after year has stifled nearly all forms of vegetation that could grow here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Scorched'), 'The shifting sands in this area shimmer in the heat.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Scorched'), 'Nothing but sand exists here. No other vegetation can survive.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Taiga'), 'Tall, dark coniferous trees surround you. The dried needles crunch softly beneath your feet.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Taiga'), 'Evergreen trees blot out the sky around you.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Taiga'), 'Massive trees grow here, blocking the view of the sky. The ground is covered in dried up needles that choke any other plants that dare to grow here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Shrubland'), 'There are no trees, but many kinds of shrubs and bushes cover the ground here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Shrubland'), 'The ground is covered in small trees and shrubs. Evidence of some small animals can be seen here and there.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Shrubland'), 'Small shrubs and tall grasses dominate the landscape here, making travel difficult in some directions.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Desert'), 'The ground here is sandy, with a moderate sprinkling of shrubs and cacti.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Desert'), 'Several forms of cactus are intermingled with the small shrubs here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Desert'), 'The sandy ground here supports only a few species of shrubs and tall grasses.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Rain Forest'), 'Massive evergreen trees covered in moss are intermingled with large-leafed deciduous species here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Rain Forest'), 'Downed limbs and massive tree trunks cover the ground here, themselves sprouting moss and various kinds of mushroom.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Rain Forest'), 'Thick moss covers every tree trunk and limb here. The air itself almost drips with moisture.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Deciduous Forest'), 'A dense growth of deciduous trees surrounds you, making it hard to see very far.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Deciduous Forest'), 'The ground is choked with a layer of thick moss as you find your way through the trees.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Temperate Deciduous Forest'), 'A cluster of ferns grows beside the path here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Grassland'), 'The rolling hills are covered with various types of grass.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Grassland'), 'Wildflowers provide a few accents of color among the greens and yellows of the grass here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Grassland'), 'Tall grasses grow as far as you can see here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Subtropical Desert'), 'There is nothing but sand to see here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Subtropical Desert'), 'A lone palm tree emerges from the harsh sand.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Subtropical Desert'), 'Some small bushes eke out a thirsty existence here.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Rain Forest'), 'Thick growth forms a canopy over the forest floor, making it dark and hard to pick your way through.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Rain Forest'), 'Tree limbs crisscross your path. Everything here seems to be alive in one way or another.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Rain Forest'), 'The sounds of animals and insects reverberate through this place as much as the trees and their limbs interject into the path you are trying to follow.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Seasonal Forest'), 'All kinds of trees can be found in this mixed forest.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Seasonal Forest'), 'The jungle is thick with life of all kinds.');

INSERT INTO biome_description (biome_id, description)
VALUES ((SELECT id FROM biome WHERE name='Tropical Seasonal Forest'), 'You carefully pick your way through the dense jungle.');