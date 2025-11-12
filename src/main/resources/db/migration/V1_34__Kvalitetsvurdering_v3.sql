CREATE TABLE kaka.kvalitetsvurdering_v3
(
    id                                                             UUID PRIMARY KEY,
    created                                                        TIMESTAMP NOT NULL,
    modified                                                       TIMESTAMP NOT NULL,

    -- Kvalitetsavvik i forvaltningen av særregelverket
    saerregelverk_automatisk_vedtak                                BOOLEAN,
    saerregelverk                                                  TEXT,
    saerregelverk_loven_er_tolket_eller_anvendt_feil               BOOLEAN,
    saerregelverk_vedtaket_bygger_paa_feil_hjemmel_el_lovtolk      BOOLEAN,
    saerregelverk_vedtaket_bygger_paa_feil_konkret_rettsanv        BOOLEAN,
    saerregelverk_det_er_lagt_til_grunn_feil_faktum                BOOLEAN,

    -- Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
    sbr                                                            TEXT,

    -- Brudd på veiledningsplikten
    sbr_brudd_paa_veiledningsplikten                               BOOLEAN,
    sbr_veiledningsplikten_parten_ikke_fatt_svar                   BOOLEAN,
    sbr_veiledningsplikten_nav_ikke_gitt_god_nok                   BOOLEAN,

    -- Brudd på utredningsplikten
    sbr_brudd_paa_utredningsplikten                                BOOLEAN,
    sbr_utredningsplikten_utredningen_av_medisinske                BOOLEAN,
    sbr_utredningsplikten_utredningen_av_inntekts                  BOOLEAN,
    sbr_utredningsplikten_utredningen_av_eoes                      BOOLEAN,
    sbr_utredningsplikten_utredningen_av_sivilstand                BOOLEAN,
    sbr_utredningsplikten_utredningen_av_samvaer                   BOOLEAN,
    sbr_utredningsplikten_utredningen_av_andre_forhold             BOOLEAN,

    -- Brudd på foreleggelsesplikten
    sbr_brudd_paa_foreleggelsesplikten                             BOOLEAN,
    sbr_foreleggelsesplikten_uttalelse_fra_rl                      BOOLEAN,
    sbr_foreleggelsesplikten_andre_opplysninger                    BOOLEAN,

    -- Brudd på begrunnelsesplikten
    sbr_brudd_paa_begrunnelsesplikten                              BOOLEAN,
    sbr_begrunnelsesplikten_begrunnelsen_viser_ikke                BOOLEAN,
    sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_faktum        BOOLEAN,
    sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_hensyn        BOOLEAN,

    -- Brudd på reglene om klage og klageforberedelse
    sbr_brudd_paa_klage_og_klageforberedelse                       BOOLEAN,
    sbr_brudd_paa_klage_klagefristen_eller_oppreisning             BOOLEAN,
    sbr_brudd_paa_klage_ikke_soerget_for_retting                   BOOLEAN,
    sbr_brudd_paa_klage_under_klageforberedelsen                   BOOLEAN,

    -- Brudd på reglene om omgjøring
    sbr_brudd_paa_omgjoering                                       BOOLEAN,
    sbr_omgjoering_ugyldighet_og_omgjoering                        BOOLEAN,
    sbr_omgjoering_fattet_vedtak_istedenfor_beslutning             BOOLEAN,

    -- Brudd på journalføringsplikten
    sbr_brudd_paa_journalforingsplikten                            BOOLEAN,
    sbr_journalforingsplikten_relevante_opplysninger_ikke          BOOLEAN,
    sbr_journalforingsplikten_relevante_opplysninger_ikke_god_nok  BOOLEAN,

    -- Brudd på plikten til å kommunisere på et klart språk
    sbr_brudd_paa_klart_spraak                                     BOOLEAN,
    sbr_brudd_paa_klart_spraak_spraket_i_vedtaket_ikke_klart_nok   BOOLEAN,
    sbr_brudd_paa_klart_spraak_spraket_i_osbrev_ikke_klart_nok     BOOLEAN,

    -- Kvalitetsavvik i saker med trygdemedisin
    bruk_av_raadgivende_lege                                       TEXT,
    raadgivende_lege_ikkebrukt                                     BOOLEAN,
    raadgivende_lege_mangelfull_bruk_av_raadgivende_lege           BOOLEAN,
    raadgivende_lege_uttalt_seg_om_tema_utover_trygdemedisin       BOOLEAN,
    raadgivende_lege_begrunnelse_mangelfull_eller_ikke_dokumentert BOOLEAN,

    -- Annet
    annet_fritekst                                                 TEXT
);

-- Hjemler list for saerregelverk_vedtaket_bygger_paa_feil_hjemmel_el_lovtolk
CREATE TABLE kaka.r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);

-- Hjemler list for saerregelverk_vedtaket_bygger_paa_feil_konkret_rettsanv
CREATE TABLE kaka.r_k_v3_saerregelverk_vbpfkr_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_saerregelverk_vbpfkr_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);

-- Hjemler list for saerregelverk_det_er_lagt_til_grunn_feil_faktum
CREATE TABLE kaka.r_k_v3_saerregelverk_deltgff_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_saerregelverk_deltgff_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);

-- Hjemler list for sbr_begrunnelsesplikten_begrunnelsen_viser_ikke
CREATE TABLE kaka.r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);

-- Hjemler list for sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_faktum
CREATE TABLE kaka.r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);

-- Hjemler list for sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_hensyn
CREATE TABLE kaka.r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v3_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v3_id),
    CONSTRAINT fk_r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list
        FOREIGN KEY (kvalitetsvurdering_v3_id)
            REFERENCES kaka.kvalitetsvurdering_v3 (id)
);