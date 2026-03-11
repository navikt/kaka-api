-- -- Add new fields for forhåndsvarsling (advance notice) to kvalitetsvurdering_v3
-- ALTER TABLE kaka.kvalitetsvurdering_v3
--     ADD COLUMN sbr_brudd_paa_reglene_om_forhaandsvarsling BOOLEAN DEFAULT false;
--
-- ALTER TABLE kaka.kvalitetsvurdering_v3
--     ADD COLUMN sbr_forhaandsvarsling_parten_ikke_varslet_foer_vedtak BOOLEAN DEFAULT false;
--
-- ALTER TABLE kaka.kvalitetsvurdering_v3
--     ADD COLUMN sbr_forhaandsvarsling_parten_varslet_mangelfullt BOOLEAN DEFAULT false;
--
-- -- Add new field for klage rules not followed despite new decision
-- ALTER TABLE kaka.kvalitetsvurdering_v3
--     ADD COLUMN sbr_brudd_paa_klage_ikke_fulgt_regler_fattet_nytt_enkeltvedtak BOOLEAN DEFAULT false;
alter table kaka.kvalitetsvurdering_v3
    drop column sbr_brudd_paa_reglene_om_forhaandsvarsling;
alter table kaka.kvalitetsvurdering_v3
    drop column sbr_forhaandsvarsling_parten_ikke_varslet_foer_vedtak;
alter table kaka.kvalitetsvurdering_v3
    drop column sbr_forhaandsvarsling_parten_varslet_mangelfullt;
alter table kaka.kvalitetsvurdering_v3
    drop column sbr_brudd_paa_klage_ikke_fulgt_regler_fattet_nytt_enkeltvedtak;


