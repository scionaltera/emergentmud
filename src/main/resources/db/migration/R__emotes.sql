CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'nod',
  100,
  'You nod.',
  '%self% nods.',
  'You nod to %target%.',
  '%self% nods to you.',
  '%self% nods to %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'shake',
  100,
  'You shake your head.',
  '%self% shakes %his% head.',
  'You shake your head at %target%.',
  '%self% shakes %his% head at you.',
  '%self% shakes %his% head at %target%.',
  'You shake your arms and legs a little to loosen up.',
  '%self% shakes %his% arms and legs a little to loosen up.'
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'smile',
  100,
  'You smile.',
  '%self% smiles.',
  'You smile at %target%.',
  '%self% smiles at you.',
  '%self% smiles at %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'grin',
  100,
  'You grin.',
  '%self% grins.',
  'You grin at %target%.',
  '%self% grins at you.',
  '%self% grins at %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'frown',
  100,
  'You frown.',
  '%self% frowns.',
  'You frown at %target%.',
  '%self% frowns at you.',
  '%self% frowns at %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'laugh',
  100,
  'You laugh.',
  '%self% laughs.',
  'You point and laugh at %target%.',
  '%self% points and laughs at you.',
  '%self% points and laughs at %target%.',
  'You laugh at your own ridiculousness.',
  '%self% laughs at %his% own ridiculousness.'
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'cry',
  100,
  'You cry.',
  '%self% cries.',
  'You look at %target% and begin to cry.',
  '%self% looks at you and begins to cry.',
  '%self% looks at %target% and begins to cry.',
  'You cry, lost in your own misfortune.',
  '%self% cries, lost in %his% own misfortune.'
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'wink',
  100,
  'You wink.',
  '%self% winks.',
  'You wink at %target%.',
  '%self% winks at you.',
  '%self% winks at %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'blink',
  100,
  'You blink.',
  '%self% blinks.',
  'You look at %target%, blinking in disbelief.',
  '%self% looks at you, blinking in disbelief.',
  '%self% looks at %target%, blinking in disbelief.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'burp',
  100,
  'You burp.',
  '%self% burps.',
  'You move in close to %target% and burp in %his% face.',
  '%self% moves in close to you and burps in your face.',
  '%self% moves in close to %target% and burps in %his% face.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'fart',
  100,
  'You fart.',
  '%self% farts.',
  'You move in close to %target% and let out a fart.',
  '%self% moves in close to you and lets out a fart.',
  '%self% moves in close to %target% and lets out a fart.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'yawn',
  100,
  'You yawn.',
  '%self% yawns.',
  'You look at %target% and let out a big yawn.',
  '%self% looks at you and lets out a big yawn.',
  '%self% looks at %target% and lets out a big yawn.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'roll',
  100,
  'You roll your eyes.',
  '%self% rolls %his% eyes.',
  'You roll your eyes at %target%.',
  '%self% rolls %his% eyes at you.',
  '%self% rolls %his% eyes at %target%.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'brow',
  100,
  'You raise an eyebrow.',
  '%self% raises an eyebrow.',
  'You look at %target%, raising one eyebrow.',
  '%self% looks at you, raising one eyebrow.',
  '%self% looks at %target%, raising one eyebrow.',
  NULL,
  NULL
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;

INSERT INTO emote_metadata (id, name, priority, to_self_untargeted, to_room_untargeted, to_self_with_target, to_target, to_room_with_target, to_self_as_target, to_room_targeting_self)
VALUES (
  uuid_generate_v1mc(),
  'wave',
  100,
  'You wave.',
  '%self% waves.',
  'You wave to %target%.',
  '%self% waves to you.',
  '%self% waves to %target%.',
  'You move your body from side to side, channeling the ocean waves.',
  '%self% moves %his% body from side to side, channeling the ocean waves.'
) ON CONFLICT (name) DO
UPDATE SET name=EXCLUDED.name, priority=EXCLUDED.priority, to_self_untargeted=EXCLUDED.to_self_untargeted, to_room_untargeted=EXCLUDED.to_room_untargeted, to_self_with_target=EXCLUDED.to_self_with_target, to_target=EXCLUDED.to_target, to_room_with_target=EXCLUDED.to_room_with_target, to_self_as_target=EXCLUDED.to_self_as_target, to_room_targeting_self=EXCLUDED.to_room_targeting_self;
