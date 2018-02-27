CREATE TABLE account (
  id                UUID NOT NULL,
  social_network    CHARACTER VARYING(255),
  social_network_id CHARACTER VARYING(255),
  PRIMARY KEY (id)
);
CREATE TABLE capability (
  id          UUID NOT NULL,
  description CHARACTER VARYING(255),
  name        CHARACTER VARYING(255) UNIQUE,
  object      INTEGER,
  scope       INTEGER,
  PRIMARY KEY (id)
);
CREATE TABLE account_capabilities (
  account_id      UUID NOT NULL,
  capabilities_id UUID NOT NULL,
  PRIMARY KEY (account_id, capabilities_id),
  CONSTRAINT fkp03vfikv7gsa526ocnilk4j6 FOREIGN KEY (capabilities_id) REFERENCES "capability" ("id"),
  CONSTRAINT fkpuh7k5n0p773pygqdm3qd1ckb FOREIGN KEY (account_id) REFERENCES "account" ("id")
);
CREATE TABLE biome (
  id                      UUID NOT NULL,
  cell_selection_strategy CHARACTER VARYING(255),
  color                   INTEGER,
  name                    CHARACTER VARYING(255) UNIQUE,
  PRIMARY KEY (id)
);
CREATE TABLE command_metadata (
  id            UUID NOT NULL,
  bean_name     CHARACTER VARYING(255),
  name          CHARACTER VARYING(255) UNIQUE,
  priority      INTEGER,
  capability_id UUID,
  PRIMARY KEY (id),
  CONSTRAINT fkps7ikb9n998pa02s7s3wjsfak FOREIGN KEY (capability_id) REFERENCES "capability" ("id")
);
CREATE TABLE emote_metadata (
  id                     UUID NOT NULL,
  name                   CHARACTER VARYING(255) UNIQUE,
  priority               INTEGER,
  to_room_targeting_self CHARACTER VARYING(255),
  to_room_untargeted     CHARACTER VARYING(255),
  to_room_with_target    CHARACTER VARYING(255),
  to_self_as_target      CHARACTER VARYING(255),
  to_self_untargeted     CHARACTER VARYING(255),
  to_self_with_target    CHARACTER VARYING(255),
  to_target              CHARACTER VARYING(255),
  PRIMARY KEY (id)
);
CREATE TABLE pronoun (
  id                      UUID NOT NULL,
  name                    CHARACTER VARYING(255) UNIQUE,
  subject                 CHARACTER VARYING(255),
  object                  CHARACTER VARYING(255),
  possessive              CHARACTER VARYING(255),
  possessive_pronoun      CHARACTER VARYING(255),
  reflexive               CHARACTER VARYING(255),
  PRIMARY KEY (id)
);
CREATE TABLE entity (
  id               UUID NOT NULL,
  creation_date    BIGINT,
  last_login_date  BIGINT,
  name             CHARACTER VARYING(255),
  remote_addr      CHARACTER VARYING(255),
  stomp_session_id CHARACTER VARYING(255),
  stomp_username   CHARACTER VARYING(255),
  user_agent       CHARACTER VARYING(255),
  x                BIGINT,
  y                BIGINT,
  z                BIGINT,
  account_id       UUID,
  gender_id        UUID,
  PRIMARY KEY (id),
  CONSTRAINT fk3uhpujqgis0u08wa5k3vmbis5 FOREIGN KEY (account_id) REFERENCES "account" ("id"),
  CONSTRAINT fkqt5l8hg2e4r00w79nozinncxb FOREIGN KEY (gender_id) REFERENCES "pronoun" ("id")
);
CREATE TABLE entity_capabilities (
  entity_id       UUID NOT NULL,
  capabilities_id UUID NOT NULL,
  PRIMARY KEY (entity_id, capabilities_id),
  CONSTRAINT fkgk70ukgr6gla0v6djmd64jqgb FOREIGN KEY (capabilities_id) REFERENCES "capability" ("id"),
  CONSTRAINT fkj5nxiusrdmbhh58bs9u9mrse3 FOREIGN KEY (entity_id) REFERENCES "entity" ("id")
);
CREATE TABLE zone (
  id           UUID NOT NULL,
  bottom_leftx BIGINT,
  bottom_lefty BIGINT,
  elevation    INTEGER,
  moisture     INTEGER,
  top_rightx   BIGINT,
  top_righty   BIGINT,
  biome_id     UUID,
  PRIMARY KEY (id),
  CONSTRAINT fk9ldxdh9uxss4exdxxurbctc6m FOREIGN KEY (biome_id) REFERENCES "biome" ("id")
);
CREATE TABLE room (
  id      UUID NOT NULL,
  x       BIGINT,
  y       BIGINT,
  z       BIGINT,
  zone_id UUID,
  PRIMARY KEY (id),
  CONSTRAINT fkns42xo8y6v8s1su4gy13vws46 FOREIGN KEY (zone_id) REFERENCES "zone" ("id")
);
CREATE TABLE whittaker_grid_location (
  id        UUID NOT NULL,
  elevation INTEGER,
  moisture  INTEGER,
  biome_id  UUID,
  PRIMARY KEY (id),
  UNIQUE (elevation, moisture),
  CONSTRAINT fks49vo599b6sj4gl21o6jewpqy FOREIGN KEY (biome_id) REFERENCES "biome" ("id")
);
