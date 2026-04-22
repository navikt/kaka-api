-- Add new field: "Alle relevante dokumenter i saken er ikke oversendt klageinstansen (journalført i godkjent arkiv)"
-- Under "Brudd på reglene om klage og klageforberedelse" (only applicable to Klage type)
ALTER TABLE kaka.kvalitetsvurdering_v3
    ADD COLUMN sbr_brudd_paa_klage_alle_rel_dok_ikke_oversendt_klageinstansen BOOLEAN DEFAULT false;

