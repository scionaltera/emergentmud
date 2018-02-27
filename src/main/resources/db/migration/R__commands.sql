CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Capabilities

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'SUPER', 'use every command', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'TELEPORT', 'use teleport commands', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'CMDEDIT', 'use command editor', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'EMOTEEDIT', 'use emote editor', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'CAPEDIT', 'use capability editor', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'DATA', 'use data commands', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'LOG', 'see log messages', 1, 1) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'CHAR_NEW', 'create characters', 0, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'CHAR_PLAY', 'play the game', 0, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'BASIC', 'use basic commands', 1, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'MOVE', 'use movement commands', 1, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'SEE', 'use sight commands', 1, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'EMOTE', 'use emotes', 1, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

INSERT INTO capability (id, name, description, object, scope)
VALUES (uuid_generate_v1mc(), 'TALK', 'use speech commands', 1, 0) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, description=EXCLUDED.description, object=EXCLUDED.object, scope=EXCLUDED.scope;

-- Commands

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'north', 'northCommand', 10, (SELECT id FROM capability WHERE name='MOVE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'east', 'eastCommand', 10, (SELECT id FROM capability WHERE name='MOVE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'south', 'southCommand', 10, (SELECT id FROM capability WHERE name='MOVE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'west', 'westCommand', 10, (SELECT id FROM capability WHERE name='MOVE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'goto', 'gotoCommand', 15, (SELECT id FROM capability WHERE name='TELEPORT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'teleport', 'teleportCommand', 15, (SELECT id FROM capability WHERE name='TELEPORT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'transfer', 'transferCommand', 15, (SELECT id FROM capability WHERE name='TELEPORT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'look', 'lookCommand', 100, (SELECT id FROM capability WHERE name='SEE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'say', 'sayCommand', 200, (SELECT id FROM capability WHERE name='TALK')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'shout', 'shoutCommand', 205, (SELECT id FROM capability WHERE name='TALK')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'gossip', 'gossipCommand', 210, (SELECT id FROM capability WHERE name='TALK')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'tell', 'tellCommand', 215, (SELECT id FROM capability WHERE name='TALK')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'who', 'whoCommand', 220, (SELECT id FROM capability WHERE name='SEE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'emote', 'emoteCommand', 250, (SELECT id FROM capability WHERE name='TALK')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'info', 'infoCommand', 300, (SELECT id FROM capability WHERE name='DATA')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'map', 'mapCommand', 400, (SELECT id FROM capability WHERE name='SEE')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'help', 'helpCommand', 500, (SELECT id FROM capability WHERE name='BASIC')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'cmdedit', 'commandEditCommand', 1000, (SELECT id FROM capability WHERE name='CMDEDIT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'emoteedit', 'emoteEditCommand', 1000, (SELECT id FROM capability WHERE name='EMOTEEDIT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'capedit', 'capabilityEditCommand', 1000, (SELECT id FROM capability WHERE name='CAPEDIT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'data', 'dataCommand', 1000, (SELECT id FROM capability WHERE name='DATA')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'exile', 'exileCommand', 1900, (SELECT id FROM capability WHERE name='CAPEDIT')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;

INSERT INTO command_metadata (id, name, bean_name, priority, capability_id)
VALUES (uuid_generate_v1mc(), 'quit', 'quitCommand', 2000, (SELECT id FROM capability WHERE name='BASIC')) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, bean_name=EXCLUDED.bean_name, priority=EXCLUDED.priority, capability_id=EXCLUDED.capability_id;