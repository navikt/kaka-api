package no.nav.klage.kaka.api.view

import java.util.*

data class AnonymizedFinishedVurderingV3(
    /** unique and static id */
    val id: UUID,
    val tilknyttetEnhet: String,
    val hjemmelIdList: List<String>,
    val avsluttetAvSaksbehandler: Date,
    val ytelseId: String,
    val utfallId: String,
    val sakstypeId: String,
    val mottattVedtaksinstans: Date?,
    val vedtaksinstansEnhet: String,
    val vedtaksinstansgruppe: Int,
    val mottattKlageinstans: Date,
    val tilbakekreving: Boolean,

    // Kvalitetsavvik i forvaltningen av særregelverket
    var saerregelverkAutomatiskVedtak: Boolean,
    var saerregelverk: String? = null,
    var saerregelverkLovenErTolketEllerAnvendtFeil: Boolean,
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean,
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: List<String>? = null,
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean,
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: List<String>? = null,
    var saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean,
    var saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: List<String>? = null,

    // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
    var saksbehandlingsregler: String? = null,
    var saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean,
    var saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean,
    var saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean,
    var saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean,
    var saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean,
    var saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean,
    var saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean,
    var saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean,
    var saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean,
    var saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean,
    var saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean,
    var saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean,
    var saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean,
    var saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean,
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean,
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean,
    var saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean,
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean,
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean,

    // Kvalitetsavvik i saker med trygdemedisin
    var brukAvRaadgivendeLege: String? = null,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class AnonymizedFinishedVurderingWithoutEnheterV3(
    /** unique and static id */
    val id: UUID,
    val hjemmelIdList: List<String>,
    val avsluttetAvSaksbehandler: Date,
    val ytelseId: String,
    val utfallId: String,
    val sakstypeId: String,
    val mottattVedtaksinstans: Date?,
    val mottattKlageinstans: Date,
    val tilbakekreving: Boolean,

    // Kvalitetsavvik i forvaltningen av særregelverket
    var saerregelverkAutomatiskVedtak: Boolean,
    var saerregelverk: String? = null,
    var saerregelverkLovenErTolketEllerAnvendtFeil: Boolean,
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean,
    var saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: List<String>? = null,
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean,
    var saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: List<String>? = null,
    var saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean,
    var saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: List<String>? = null,

    // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
    var saksbehandlingsregler: String? = null,
    var saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean,
    var saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean,
    var saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean,
    var saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean,
    var saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean,
    var saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean,
    var saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean,
    var saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean,
    var saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: List<String>? = null,
    var saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean,
    var saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean,
    var saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean,
    var saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean,
    var saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean,
    var saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean,
    var saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean,
    var saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean,
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean,
    var saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean,
    var saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean,
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean,
    var saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean,

    // Kvalitetsavvik i saker med trygdemedisin
    var brukAvRaadgivendeLege: String? = null,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class ManagerResponseV3(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV3>,
    val saksbehandlere: Map<String, List<AnonymizedFinishedVurderingV3>>?,
    val mine: List<AnonymizedFinishedVurderingV3>,
    val rest: List<AnonymizedFinishedVurderingV3>,
)

data class TotalResponseV3(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV3>,
    val rest: List<AnonymizedFinishedVurderingV3>,
)

data class MyResponseV3(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV3>,
    val mine: List<AnonymizedFinishedVurderingV3>,
    val rest: List<AnonymizedFinishedVurderingV3>,
)

data class OpenResponseWithoutEnheterV3(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
)

data class VedtaksinstanslederResponseV3(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
    val mine: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
)