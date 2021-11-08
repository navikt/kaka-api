create table kaka.saksdata
(
    id                                        UUID PRIMARY KEY,
    klager                                    TEXT,
    sakstype_id                               TEXT,
    tema_id                                   TEXT,
    dato_mottatt_vedtaksinstans               DATE,
    vedtaksinstans_enhet                      TEXT,
    dato_mottatt_klageinstans                 DATE,
    utfall_id                                 TEXT,
    utfoerende_saksbehandlerident             TEXT,
    dato_saksdata_avsluttet_av_saksbehandler TIMESTAMP,
    created                                   TIMESTAMP NOT NULL,
    modified                                  TIMESTAMP NOT NULL
);

create table kaka.kvalitetsvurdering
(
    id                                                       UUID PRIMARY KEY,

    klageforberedelsen_radio_valg                            TEXT,

    sakens_dokumenter                                        BOOLEAN,
    oversittet_klagefrist_ikke_kommentert                    BOOLEAN,
    klagerens_relevante_anfoersler_ikke_kommentert           BOOLEAN,
    begrunnelse_for_hvorfor_avslag_opprettholdes             BOOLEAN,
    konklusjonen                                             BOOLEAN,
    oversendelsesbrevets_innhold_ikke_i_samsvar_med_tema     BOOLEAN,

    utredningen_radio_valg                                   TEXT,

    utredningen_av_medisinske_forhold                        BOOLEAN,
    utredningen_av_medisinske_forhold_text                   TEXT,
    utredningen_av_inntektsforhold                           BOOLEAN,
    utredningen_av_inntektsforhold_text                      TEXT,
    utredningen_av_arbeid                                    BOOLEAN,
    utredningen_av_arbeid_text                               TEXT,
    arbeidsrettet_brukeroppfoelging                          BOOLEAN,
    arbeidsrettet_brukeroppfoelging_text                     TEXT,
    utredningen_av_andre_aktuelle_forhold_i_saken            BOOLEAN,
    utredningen_av_andre_aktuelle_forhold_i_saken_text       TEXT,
    utredningen_av_eoes_problematikk                         BOOLEAN,
    utredningen_av_eoes_problematikk_text                    TEXT,
    veiledning_fra_nav                                       BOOLEAN,
    veiledning_fra_nav_text                                  TEXT,

    bruk_av_raadgivende_lege_radio_valg                      TEXT,

    raadgivende_lege_er_ikke_brukt                           BOOLEAN,
    raadgivende_lege_er_brukt_feil_spoersmaal                BOOLEAN,
    raadgivende_lege_har_uttalt_seg_utover_trygdemedisin     BOOLEAN,
    raadgivende_lege_er_brukt_mangelfull_dokumentasjon       BOOLEAN,

    vedtaket_radio_valg                                      TEXT,
    det_er_ikke_brukt_riktig_hjemmel                         BOOLEAN,
    innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrevet BOOLEAN,
    rettsregelen_er_benyttet_feil                            BOOLEAN,
    vurdering_av_faktum_er_mangelfull                        BOOLEAN,
    det_er_feil_i_konkret_rettsanvendelse                    BOOLEAN,
    begrunnelsen_er_ikke_konkret_og_individuell              BOOLEAN,
    spraaket_er_ikke_tydelig                                 BOOLEAN,

    nye_opplysninger_mottatt                                 BOOLEAN,
    bruk_i_opplaering                                        BOOLEAN,
    bruk_i_opplaering_text                                   TEXT,
    betydelig_avvik                                          BOOLEAN,
    betydelig_avvik_text                                     TEXT,

    created                                                  TIMESTAMP NOT NULL,
    modified                                                 TIMESTAMP NOT NULL,
    utfoerende_saksbehandlerident                            TEXT
);

CREATE TABLE kaka.hjemmel
(
    id           TEXT NOT NULL,
    saksdata_id UUID NOT NULL,
    PRIMARY KEY (id, saksdata_id),
    CONSTRAINT fk_hjemmel_saksdata
        FOREIGN KEY (saksdata_id)
            REFERENCES kaka.saksdata (id)
);

ALTER TABLE kaka.saksdata
    ADD kvalitetsvurdering_id UUID REFERENCES kaka.kvalitetsvurdering (id);
