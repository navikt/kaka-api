ALTER TABLE kaka.kvalitetsvurdering_v2
    DROP COLUMN klageforberedelsen_mangelfull_begrunnelse_for_hvorfor_vedtaket_;

--new field?
ALTER TABLE kaka.kvalitetsvurdering_v2
    ADD COLUMN klageforberedelsen_feil_ved_begrunnelsen_for_hvorfor_avslag_opp BOOLEAN default false;

--Add prefix
ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN sakens_dokumenter to klageforberedelsen_sakens_dokumenter;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN sakens_dokumenter_relevante_opplysninger_fra_andre_fagsystemer_ to klageforberedelsen_sakens_dokumenter_relevante_opplysninger_fra;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN sakens_dokumenter_journalfoerte_dokumenter_feil_navn to klageforberedelsen_sakens_dokumenter_journalfoerte_dokumenter_f;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN sakens_dokumenter_mangler_fysisk_saksmappe to klageforberedelsen_sakens_dokumenter_mangler_fysisk_saksmappe;


--Add prefix
ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN vedtaket_ikke_godt_nok_frem_faktum to vedtaket_ikke_konkret_individuell_begrunnelse_faktum;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN vedtaket_ikke_godt_nok_frem_hvordan_rettsregelen_er_anvendt_paa to vedtaket_ikke_konkret_individuell_begrunnelse_rettsregel;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN vedtaket_mye_standardtekst to vedtaket_ikke_konkret_individuell_begrunnelse_mye_standardtekst;

--New name
ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN raadgivende_lege_begrunnelse_mangelfull_eller_ikke_skriftliggjo to raadgivende_lege_begrunnelse_mangelfull_eller_ikke_dokumentert;

ALTER TABLE kaka.kvalitetsvurdering_v2
    RENAME COLUMN vedtak_automatisk_vedtak to vedtaket_automatisk_vedtak;


create table kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholdet_i
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholde
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);
