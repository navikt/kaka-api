package no.nav.klage.kaka.domain.kvalitetsvurdering.v3

import jakarta.persistence.*
import no.nav.klage.kaka.domain.RegistreringshjemmelConverter
import no.nav.klage.kaka.domain.raadgivendeLegeYtelser
import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.ytelse.Ytelse
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "kvalitetsvurdering_v3", schema = "kaka")
@DynamicUpdate
class KvalitetsvurderingV3(
    @Id
    val id: UUID = UUID.randomUUID(),

    // Kvalitetsavvik i forvaltningen av særregelverket
    @Column(name = "saerregelverk_automatisk_vedtak")
    var saerregelverkAutomatiskVedtak: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(name = "saerregelverk")
    var saerregelverk: Radiovalg? = null,

    // Loven er tolket eller anvendt feil i vedtaket
    @Column(name = "saerregelverk_loven_er_tolket_eller_anvendt_feil")
    var saerregelverkLovenErTolketEllerAnvendtFeil: Boolean = false,

    @Column(name = "saerregelverk_vedtaket_bygger_paa_feil_hjemmel_el_lovtolk")
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: Set<Registreringshjemmel>? = null,

    // Vedtaket bygger på feil konkret rettsanvendelse eller skjønnsutøvelse
    @Column(name = "saerregelverk_vedtaket_bygger_paa_feil_konkret_rettsanv")
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_saerregelverk_vbpfkr_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: Set<Registreringshjemmel>? = null,

    // Det er lagt til grunn feil faktum i vedtaket
    @Column(name = "saerregelverk_det_er_lagt_til_grunn_feil_faktum")
    var saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_saerregelverk_deltgff_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: Set<Registreringshjemmel>? = null,

    // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene

    @Enumerated(EnumType.STRING)
    @Column(name = "sbr")
    var saksbehandlingsregler: Radiovalg? = null,

    // Brudd på veiledningsplikten, forvaltningsloven § 11
    @Column(name = "sbr_brudd_paa_veiledningsplikten")
    var saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean = false,

    @Column(name = "sbr_veiledningsplikten_parten_ikke_fatt_svar")
    var saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean = false,

    @Column(name = "sbr_veiledningsplikten_nav_ikke_gitt_god_nok")
    var saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean = false,

    // Brudd på utredningsplikten, forvaltningsloven § 17
    @Column(name = "sbr_brudd_paa_utredningsplikten")
    var saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_medisinske")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_inntekts")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_eoes")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_sivilstand")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_samvaer")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean = false,

    @Column(name = "sbr_utredningsplikten_utredningen_av_andre_forhold")
    var saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean = false,

    // Brudd på foreleggelsesplikten, forvaltningsloven §§ 17 og 18 til 19
    @Column(name = "sbr_brudd_paa_foreleggelsesplikten")
    var saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean = false,

    @Column(name = "sbr_foreleggelsesplikten_uttalelse_fra_rl")
    var saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean = false,

    @Column(name = "sbr_foreleggelsesplikten_andre_opplysninger")
    var saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean = false,

    // Brudd på begrunnelsesplikten, forvaltningsloven §§ 24 og 25
    @Column(name = "sbr_brudd_paa_begrunnelsesplikten")
    var saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean = false,

    @Column(name = "sbr_begrunnelsesplikten_begrunnelsen_viser_ikke")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: Set<Registreringshjemmel>? = null,

    @Column(name = "sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_faktum")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: Set<Registreringshjemmel>? = null,

    @Column(name = "sbr_begrunnelsesplikten_begrunnelsen_nevner_ikke_hensyn")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean = false,

    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v3_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: Set<Registreringshjemmel>? = null,

    // Brudd på reglene om klage og klageforberedelse, forvaltningsloven §§ 28 til 33
    @Column(name = "sbr_brudd_paa_klage_og_klageforberedelse")
    var saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean = false,

    @Column(name = "sbr_brudd_paa_klage_klagefristen_eller_oppreisning")
    var saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean = false,

    @Column(name = "sbr_brudd_paa_klage_ikke_soerget_for_retting")
    var saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean = false,

    @Column(name = "sbr_brudd_paa_klage_under_klageforberedelsen")
    var saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean = false,

    // Brudd på reglene om omgjøring utenfor ordinær klage- og ankesaksbehandling, forvaltningsloven § 35
    @Column(name = "sbr_brudd_paa_omgjoering")
    var saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean = false,

    @Column(name = "sbr_omgjoering_ugyldighet_og_omgjoering")
    var saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean = false,

    @Column(name = "sbr_omgjoering_fattet_vedtak_istedenfor_beslutning")
    var saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean = false,

    // Brudd på journalføringsplikten, arkivloven §§ 6 og 12 og forskrift §§ 9 følgende
    @Column(name = "sbr_brudd_paa_journalforingsplikten")
    var saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean = false,

    @Column(name = "sbr_journalforingsplikten_relevante_opplysninger_ikke")
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean = false,

    @Column(name = "sbr_journalforingsplikten_relevante_opplysninger_ikke_god_nok")
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean = false,

    // Brudd på plikten til å kommunisere på et klart språk, språklova § 9
    @Column(name = "sbr_brudd_paa_klart_spraak")
    var saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean = false,

    @Column(name = "sbr_brudd_paa_klart_spraak_spraket_i_vedtaket_ikke_klart_nok")
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean = false,

    @Column(name = "sbr_brudd_paa_klart_spraak_spraket_i_osbrev_ikke_klart_nok")
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean = false,

    // Kvalitetsavvik i saker med trygdemedisin
    @Enumerated(EnumType.STRING)
    @Column(name = "bruk_av_raadgivende_lege")
    var brukAvRaadgivendeLege: RadiovalgRaadgivendeLege? = null,

    @Column(name = "raadgivende_lege_ikkebrukt")
    var raadgivendeLegeIkkebrukt: Boolean = false,

    @Column(name = "raadgivende_lege_mangelfull_bruk_av_raadgivende_lege")
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean = false,

    @Column(name = "raadgivende_lege_uttalt_seg_om_tema_utover_trygdemedisin")
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean = false,

    @Column(name = "raadgivende_lege_begrunnelse_mangelfull_eller_ikke_dokumentert")
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean = false,

    // Annet
    @Column(name = "annet_fritekst")
    var annetFritekst: String? = null,

    @Column(name = "created")
    val created: LocalDateTime = LocalDateTime.now(),

    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KvalitetsvurderingV3

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun resetFieldsUnusedInAnke() {
        // Reset klage-specific fields in saksbehandlingsregler
        //TODO more fields? These fields?
        saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = false
        saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = false
        saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = false
        saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = false
    }

    fun cleanup() {
        // Cleanup Særregelverk
        if (saerregelverk == Radiovalg.BRA) {
            saerregelverkLovenErTolketEllerAnvendtFeil = false
            saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = false
            saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = null
            saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = false
            saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = null
            saerregelverkDetErLagtTilGrunnFeilFaktum = false
            saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = null
        } else {
            if (!saerregelverkLovenErTolketEllerAnvendtFeil) {
                // If parent is not checked, reset all children
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = false
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = null
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = false
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = null
            } else {
                // Parent is checked, clean up hjemler lists if checkboxes are not checked
                if (!saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning) {
                    saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = null
                }
                if (!saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn) {
                    saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = null
                }
            }

            // detErLagtTilGrunnFeilFaktum is at same level as lovenErTolketEllerAnvendtFeil
            if (!saerregelverkDetErLagtTilGrunnFeilFaktum) {
                saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = null
            }
        }

        // Cleanup Saksbehandlingsregler
        if (saksbehandlingsregler == Radiovalg.BRA) {
            saksbehandlingsreglerBruddPaaVeiledningsplikten = false
            saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = false
            saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = false

            saksbehandlingsreglerBruddPaaUtredningsplikten = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = false
            saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = false

            saksbehandlingsreglerBruddPaaForeleggelsesplikten = false
            saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = false
            saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = false

            saksbehandlingsreglerBruddPaaBegrunnelsesplikten = false
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = false
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = null
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = false
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = null
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = false
            saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = null

            saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = false
            saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = false
            saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = false
            saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = false

            saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = false
            saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = false
            saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = false

            saksbehandlingsreglerBruddPaaJournalfoeringsplikten = false
            saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = false
            saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = false

            saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = false
            saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = false
            saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = false
        } else {
            if (!saksbehandlingsreglerBruddPaaVeiledningsplikten) {
                saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = false
                saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = false
            }
            if (!saksbehandlingsreglerBruddPaaUtredningsplikten) {
                saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = false
                saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = false
                saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = false
                saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = false
                saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = false
                saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = false
            }
            if (!saksbehandlingsreglerBruddPaaForeleggelsesplikten) {
                saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = false
                saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = false
            }
            if (!saksbehandlingsreglerBruddPaaBegrunnelsesplikten) {
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = false
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = null
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = false
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = null
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = false
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = null
            } else {
                if (!saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket) {
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = null
                }
                if (!saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum) {
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = null
                }
                if (!saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn) {
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = null
                }
            }
            if (!saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse) {
                saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = false
                saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = false
                saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = false
            }
            if (!saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke) {
                saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = false
                saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = false
            }
            if (!saksbehandlingsreglerBruddPaaJournalfoeringsplikten) {
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = false
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = false
            }
            if (!saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak) {
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = false
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = false
            }
        }

        // Cleanup Trygdemedisin
        if (brukAvRaadgivendeLege != RadiovalgRaadgivendeLege.MANGELFULLT) {
            raadgivendeLegeIkkebrukt = false
            raadgivendeLegeMangelfullBrukAvRaadgivendeLege = false
            raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = false
            raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = false
        }
    }

    fun getInvalidProperties(ytelse: Ytelse?, type: Type): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        result += getCommonInvalidProperties(ytelse)

        if (type == Type.KLAGE) {
            result += getSpecificInvalidPropertiesForKlage()
        }

        return result
    }

    private fun getCommonInvalidProperties(ytelse: Ytelse?): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        // Validate Særregelverk
        if (saerregelverk == null) {
            result.add(createRadioValgValidationError(::saerregelverk.name))
        } else if (saerregelverk == Radiovalg.MANGELFULLT) {
            // Level 1: Must choose at least one main option
            if (!saerregelverkLovenErTolketEllerAnvendtFeil &&
                !saerregelverkDetErLagtTilGrunnFeilFaktum
            ) {
                result.add(createMissingChecksValidationError(::saerregelverk.name + "Group"))
            }

            // Level 2: If "loven er tolket eller anvendt feil" is chosen, must choose at least one sub-option
            if (saerregelverkLovenErTolketEllerAnvendtFeil) {
                if (!saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning &&
                    !saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn
                ) {
                    result.add(createMissingChecksValidationError(::saerregelverkLovenErTolketEllerAnvendtFeil.name + "Group"))
                }
            }

            // Validate hjemler lists
            if (saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning &&
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList.isNullOrEmpty()
            ) {
                result.add(createMissingChecksValidationError(::saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList.name))
            }

            if (saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn &&
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList.isNullOrEmpty()
            ) {
                result.add(createMissingChecksValidationError(::saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList.name))
            }

            if (saerregelverkDetErLagtTilGrunnFeilFaktum &&
                saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList.isNullOrEmpty()
            ) {
                result.add(createMissingChecksValidationError(::saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList.name))
            }
        }

        // Validate Saksbehandlingsregler
        if (saksbehandlingsregler == null) {
            result.add(createRadioValgValidationError(::saksbehandlingsregler.name))
        } else if (saksbehandlingsregler == Radiovalg.MANGELFULLT) {
            // Level 1: Must choose at least one main category
            if (!saksbehandlingsreglerBruddPaaVeiledningsplikten &&
                !saksbehandlingsreglerBruddPaaUtredningsplikten &&
                !saksbehandlingsreglerBruddPaaForeleggelsesplikten &&
                !saksbehandlingsreglerBruddPaaBegrunnelsesplikten &&
                !saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse &&
                !saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke &&
                !saksbehandlingsreglerBruddPaaJournalfoeringsplikten &&
                !saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak
            ) {
                result.add(createMissingChecksValidationError(::saksbehandlingsregler.name + "Group"))
            }

            // Level 2: Validate subgroups - must choose at least one sub-option if parent is checked
            if (saksbehandlingsreglerBruddPaaVeiledningsplikten) {
                if (!saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser &&
                    !saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaVeiledningsplikten.name + "Group"))
                }
            }

            if (saksbehandlingsreglerBruddPaaUtredningsplikten) {
                if (!saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok &&
                    !saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok &&
                    !saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok &&
                    !saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok &&
                    !saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok &&
                    !saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaUtredningsplikten.name + "Group"))
                }
            }

            if (saksbehandlingsreglerBruddPaaForeleggelsesplikten) {
                if (!saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten &&
                    !saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaForeleggelsesplikten.name + "Group"))
                }
            }

            if (saksbehandlingsreglerBruddPaaBegrunnelsesplikten) {
                if (!saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket &&
                    !saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum &&
                    !saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaBegrunnelsesplikten.name + "Group"))
                }

                // Validate hjemler lists for begrunnelsesplikten
                if (saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket &&
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList.isNullOrEmpty()
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList.name))
                }

                if (saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum &&
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList.isNullOrEmpty()
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList.name))
                }

                if (saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn &&
                    saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList.isNullOrEmpty()
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList.name))
                }
            }

            if (saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke) {
                if (!saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert &&
                    !saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke.name + "Group"))
                }
            }

            if (saksbehandlingsreglerBruddPaaJournalfoeringsplikten) {
                if (!saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert &&
                    !saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaJournalfoeringsplikten.name + "Group"))
                }
            }

            if (saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak) {
                if (!saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok &&
                    !saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak.name + "Group"))
                }
            }
        }

        // Validate Trygdemedisin
        if (ytelse in raadgivendeLegeYtelser) {
            if (brukAvRaadgivendeLege == null) {
                result.add(createRadioValgValidationError(::brukAvRaadgivendeLege.name))
            } else if (brukAvRaadgivendeLege == RadiovalgRaadgivendeLege.MANGELFULLT) {
                if (!raadgivendeLegeIkkebrukt &&
                    !raadgivendeLegeMangelfullBrukAvRaadgivendeLege &&
                    !raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin &&
                    !raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert
                ) {
                    result.add(createMissingChecksValidationError(::brukAvRaadgivendeLege.name + "Group"))
                }
            }
        }

        return result
    }

    //is this correct?
    private fun getSpecificInvalidPropertiesForKlage(): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()
        if (saksbehandlingsregler == Radiovalg.MANGELFULLT) {
            if (saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse) {
                if (!saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert &&
                    !saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold &&
                    !saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser
                ) {
                    result.add(createMissingChecksValidationError(::saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse.name + "Group"))
                }
            }
        }

        return result
    }

    private fun createMissingChecksValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg minst én."
        )
    }

    private fun createRadioValgValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg et alternativ."
        )
    }

    enum class Radiovalg {
        BRA,
        MANGELFULLT
    }

    enum class RadiovalgRaadgivendeLege {
        IKKE_AKTUELT,
        BRA,
        MANGELFULLT
    }
}