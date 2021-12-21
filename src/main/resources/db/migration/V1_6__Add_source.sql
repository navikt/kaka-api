ALTER TABLE kaka.saksdata
    ADD COLUMN source_id TEXT;

UPDATE kaka.saksdata
SET source_id = '1';

ALTER TABLE kaka.saksdata
    ALTER COLUMN source_id SET NOT NULL;