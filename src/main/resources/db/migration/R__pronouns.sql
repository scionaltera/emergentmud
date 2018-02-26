CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO pronoun (id, name, subject, object, possessive, possessive_pronoun, reflexive)
VALUES (uuid_generate_v1mc(), 'male', 'he', 'him', 'his', 'his', 'himself') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, subject=EXCLUDED.subject, object=EXCLUDED.object, possessive=EXCLUDED.possessive, possessive_pronoun=EXCLUDED.possessive_pronoun, reflexive=EXCLUDED.reflexive;

INSERT INTO pronoun (id, name, subject, object, possessive, possessive_pronoun, reflexive)
VALUES (uuid_generate_v1mc(), 'female', 'she', 'her', 'her', 'hers', 'herself') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, subject=EXCLUDED.subject, object=EXCLUDED.object, possessive=EXCLUDED.possessive, possessive_pronoun=EXCLUDED.possessive_pronoun, reflexive=EXCLUDED.reflexive;

INSERT INTO pronoun (id, name, subject, object, possessive, possessive_pronoun, reflexive)
VALUES (uuid_generate_v1mc(), 'neutral', 'they', 'them', 'their', 'theirs', 'themself') ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, subject=EXCLUDED.subject, object=EXCLUDED.object, possessive=EXCLUDED.possessive, possessive_pronoun=EXCLUDED.possessive_pronoun, reflexive=EXCLUDED.reflexive;