ALTER TABLE kaka.saksdata
    ADD COLUMN tilknyttet_enhet TEXT;

UPDATE kaka.saksdata
SET tilknyttet_enhet = '4295';

ALTER TABLE kaka.saksdata
    ALTER COLUMN tilknyttet_enhet SET NOT NULL;