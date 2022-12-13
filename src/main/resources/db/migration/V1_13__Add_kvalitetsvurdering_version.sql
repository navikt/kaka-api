ALTER TABLE kaka.saksdata
    ADD COLUMN kvalitetsvurdering_version INT NOT NULL DEFAULT 1;

ALTER TABLE kaka.saksdata
    DROP CONSTRAINT saksdata_kvalitetsvurdering_id_fkey;