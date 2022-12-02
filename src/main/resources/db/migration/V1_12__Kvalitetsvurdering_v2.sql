create table kaka.kvalitetsvurdering_v2
(
    id                                                                                  UUID PRIMARY KEY,
    created                                                                             TIMESTAMP NOT NULL,
    modified                                                                            TIMESTAMP NOT NULL,
    sakens_dokumenter                                                                   BOOLEAN,
    sakens_dokumenter_relevante_opplysninger_fra_andre_fagsystemer_er_ikke_journalfoert BOOLEAN,
    sakens_dokumenter_journalfoerte_dokumenter_feil_navn                                BOOLEAN,
    sakens_dokumenter_mangler_fysisk_saksmappe                                          BOOLEAN,
    klageforberedelsen                                                                  TEXT,
    klageforberedelsen_underinstans_ikke_sendt_alle_relevante_saksdokumenter_til_parten BOOLEAN,
    klageforberedelsen_oversittet_klagefrist_ikke_kommentert                            BOOLEAN,
    klageforberedelsen_klagers_relevante_anfoersler_ikke_tilstrekkelig_imotegatt        BOOLEAN,
    klageforberedelsen_mangelfull_begrunnelse_for_hvorfor_vedtaket_opprettholdes        BOOLEAN,
    klageforberedelsen_oversendelsesbrevets_innhold_er_ikke_i_samsvar_med_sakens_tema   BOOLEAN,
    klageforberedelsen_oversendelsesbrev_ikke_sendt_kopi_til_parten_eller_feil_mottaker BOOLEAN,
    utredningen                                                                         TEXT,
    utredningen_av_medisinske_forhold                                                   BOOLEAN,
    utredningen_av_inntektsforhold                                                      BOOLEAN,
    utredningen_av_arbeidsaktivitet                                                     BOOLEAN,
    utredningen_av_eoes_utenlandsproblematikk                                           BOOLEAN,
    utredningen_av_andre_aktuelle_forhold_i_saken                                       BOOLEAN,
    vedtaket_lovbestemmelsen_tolket_feil                                                BOOLEAN,
    vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikke_vurdert            BOOLEAN,
    vedtaket_feil_konkret_rettsanvendelse                                               BOOLEAN,
    vedtaket_ikke_konkret_individuell_begrunnelse                                       BOOLEAN,
    vedtaket_ikke_godt_nok_frem_faktum                                                  BOOLEAN,
    vedtaket_ikke_godt_nok_frem_hvordan_rettsregelen_er_anvendt_paa_faktum              BOOLEAN,
    vedtaket_mye_standardtekst                                                          BOOLEAN,
    vedtak_automatisk_vedtak                                                            BOOLEAN,
    vedtaket                                                                            TEXT,
    vedtaket_innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrevet                   BOOLEAN,
    vedtaket_det_er_lagt_til_grunn_feil_faktum                                          BOOLEAN,
    vedtaket_spraak_og_formidling_er_ikke_tydelig                                       BOOLEAN,
    raadgivende_lege_ikkebrukt                                                          BOOLEAN,
    raadgivende_lege_mangelfull_bruk_av_raadgivende_lege                                BOOLEAN,
    raadgivende_lege_uttalt_seg_om_tema_utover_trygdemedisin                            BOOLEAN,
    raadgivende_lege_begrunnelse_mangelfull_eller_ikke_skriftliggjort                   BOOLEAN,
    bruk_av_raadgivende_lege                                                            TEXT,
    annet_fritekst                                                                      TEXT
);

create table kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemmelsen_tolket_feil_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemmelsen_tolket_feil_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);

create table kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikke_vurdert_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikke_vurdert_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);

create table kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkret_rettsanvendelse_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkret_rettsanvendelse_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);
