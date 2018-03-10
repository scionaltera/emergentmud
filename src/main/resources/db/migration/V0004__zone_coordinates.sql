ALTER TABLE "zone" RENAME COLUMN bottom_leftx TO bottom_left_x;
ALTER TABLE "zone" RENAME COLUMN bottom_lefty TO bottom_left_y;
ALTER TABLE "zone" RENAME COLUMN top_rightx TO top_right_x;
ALTER TABLE "zone" RENAME COLUMN top_righty TO top_right_y;

ALTER TABLE "zone" ADD COLUMN bottom_left_z BIGINT;
ALTER TABLE "zone" ADD COLUMN top_right_z BIGINT;

UPDATE "zone" SET bottom_left_z = 0;
UPDATE "zone" SET top_right_z = 0;