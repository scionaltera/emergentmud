CREATE TABLE biome_description (
  biome_id UUID,
  description CHARACTER VARYING(255),
  CONSTRAINT fk_biome_description_biome FOREIGN KEY (biome_id) REFERENCES "biome" ("id")
);