CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

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