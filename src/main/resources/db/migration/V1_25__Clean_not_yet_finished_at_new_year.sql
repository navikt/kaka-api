--All that we forgot at new year.
UPDATE kaka.kvalitetsvurdering_v2
SET modified                                                        = created,
    klageforberedelsen_sakens_dokumenter                            = false,
    klageforberedelsen_sakens_dokumenter_relevante_opplysninger_fra = false,
    klageforberedelsen_sakens_dokumenter_journalfoerte_dokumenter_f = false,
    klageforberedelsen_sakens_dokumenter_mangler_fysisk_saksmappe   = false,
    klageforberedelsen                                              = null,
    klageforberedelsen_underinstans_ikke_sendt_alle_relevante_saksd = false,
    klageforberedelsen_oversittet_klagefrist_ikke_kommentert        = false,
    klageforberedelsen_klagers_relevante_anfoersler_ikke_tilstrekke = false,
    klageforberedelsen_oversendelsesbrevets_innhold_er_ikke_i_samsv = false,
    klageforberedelsen_oversendelsesbrev_ikke_sendt_kopi_til_parten = false,
    utredningen                                                     = null,
    utredningen_av_medisinske_forhold                               = false,
    utredningen_av_inntektsforhold                                  = false,
    utredningen_av_arbeidsaktivitet                                 = false,
    utredningen_av_eoes_utenlandsproblematikk                       = false,
    utredningen_av_andre_aktuelle_forhold_i_saken                   = false,
    vedtaket_lovbestemmelsen_tolket_feil                            = false,
    vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikk = false,
    vedtaket_feil_konkret_rettsanvendelse                           = false,
    vedtaket_ikke_konkret_individuell_begrunnelse                   = false,
    vedtaket_ikke_konkret_individuell_begrunnelse_faktum            = false,
    vedtaket_ikke_konkret_individuell_begrunnelse_rettsregel        = false,
    vedtaket_ikke_konkret_individuell_begrunnelse_mye_standardtekst = false,
    vedtaket_automatisk_vedtak                                      = false,
    vedtaket                                                        = null,
    vedtaket_innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrev = false,
    vedtaket_det_er_lagt_til_grunn_feil_faktum                      = false,
    vedtaket_spraak_og_formidling_er_ikke_tydelig                   = false,
    raadgivende_lege_ikkebrukt                                      = false,
    raadgivende_lege_mangelfull_bruk_av_raadgivende_lege            = false,
    raadgivende_lege_uttalt_seg_om_tema_utover_trygdemedisin        = false,
    raadgivende_lege_begrunnelse_mangelfull_eller_ikke_dokumentert  = false,
    bruk_av_raadgivende_lege                                        = null,
    annet_fritekst                                                  = null,
    klageforberedelsen_feil_ved_begrunnelsen_for_hvorfor_avslag_opp = false,
    klageforberedelsen_uuk                                          = false,
    klageforberedelsen_uuk_khbuoaino                                = false,
    klageforberedelsen_uuk_khbuoaino_fritekst                       = null,
    klageforberedelsen_uuk_khsino                                   = false,
    klageforberedelsen_uuk_khsino_fritekst                          = null,
    utredningen_av_sivilstand_boforhold                             = false,
    vedtaket_brukt_feil_hjemmel                                     = false,
    vedtaket_alle_relevante_hjemler_er_ikke_vurdert                 = false
WHERE id NOT IN (SELECT s.kvalitetsvurdering_id
                 FROM kaka.saksdata s);

--saksbehandler must fix these manually afterwards.
UPDATE kaka.kvalitetsvurdering_v2
SET vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikk = false
WHERE id IN (
             '93c5a99a-b180-4564-91c6-0cb0729e0bdc',
             'c88368b3-93f3-49d9-80fa-158f228bffb8',
             '3338a722-37d9-4e25-b246-9f9d71aaaad0',
             '7875f65f-f5fe-4c97-8e24-34c06205b233',
             'efa37ddc-43a9-4b1e-a7ad-c4353175b485'
    );