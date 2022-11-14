CREATE TABLE kaka.kvalitetsvurdering_registreringshjemmel
(
    id                    TEXT NOT NULL,
    kvalitetsvurdering_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_id),
    CONSTRAINT fk_registreringshjemmel_kvalitetsvurdering
        FOREIGN KEY (kvalitetsvurdering_id)
            REFERENCES kaka.kvalitetsvurdering (id)
);