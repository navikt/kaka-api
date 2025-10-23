package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3

data class KvalitetsvurderingV3View(
    // Kvalitetsavvik i forvaltningen av særregelverket
    val saerregelverkAutomatiskVedtak: Boolean,
    val saerregelverk: KvalitetsvurderingV3.Radiovalg?,
    val saerregelverkLovenErTolketEllerAnvendtFeil: Boolean,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: Set<String>?,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: Set<String>?,
    val saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean,
    val saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: Set<String>?,

    // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
    val saksbehandlingsregler: KvalitetsvurderingV3.Radiovalg?,
    val saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean,
    val saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean,
    val saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean,
    val saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean,
    val saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean,
    val saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean,
    val saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: Set<String>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: Set<String>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: Set<String>?,
    val saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean,
    val saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean,
    val saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean,
    val saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean,
    val saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean,
    val saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean,
    val saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean,
    val saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean,
    val saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean,

    // Kvalitetsavvik i saker med trygdemedisin
    val brukAvRaadgivendeLege: KvalitetsvurderingV3.RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,

    // Annet
    val annetFritekst: String?,
)

fun KvalitetsvurderingV3.toKvalitetsvurderingV3View(): KvalitetsvurderingV3View {
    return KvalitetsvurderingV3View(
        // Kvalitetsavvik i forvaltningen av særregelverket
        saerregelverkAutomatiskVedtak = saerregelverkAutomatiskVedtak,
        saerregelverk = saerregelverk,
        saerregelverkLovenErTolketEllerAnvendtFeil = saerregelverkLovenErTolketEllerAnvendtFeil,
        saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning,
        saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList?.map { it.id }
            ?.toSet(),
        saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn,
        saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList?.map { it.id }
            ?.toSet(),
        saerregelverkDetErLagtTilGrunnFeilFaktum = saerregelverkDetErLagtTilGrunnFeilFaktum,
        saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList?.map { it.id }
            ?.toSet(),

        // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
        saksbehandlingsregler = saksbehandlingsregler,
        saksbehandlingsreglerBruddPaaVeiledningsplikten = saksbehandlingsreglerBruddPaaVeiledningsplikten,
        saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
        saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,
        saksbehandlingsreglerBruddPaaUtredningsplikten = saksbehandlingsreglerBruddPaaUtredningsplikten,
        saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,
        saksbehandlingsreglerBruddPaaForeleggelsesplikten = saksbehandlingsreglerBruddPaaForeleggelsesplikten,
        saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
        saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,
        saksbehandlingsreglerBruddPaaBegrunnelsesplikten = saksbehandlingsreglerBruddPaaBegrunnelsesplikten,
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket,
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList?.map { it.id }
            ?.toSet(),
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum,
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList?.map { it.id }
            ?.toSet(),
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn,
        saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList?.map { it.id }
            ?.toSet(),
        saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
        saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
        saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
        saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,
        saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
        saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
        saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,
        saksbehandlingsreglerBruddPaaJournalfoeringsplikten = saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
        saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
        saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,
        saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
        saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
        saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,

        // Kvalitetsavvik i saker med trygdemedisin
        brukAvRaadgivendeLege = brukAvRaadgivendeLege,
        raadgivendeLegeIkkebrukt = raadgivendeLegeIkkebrukt,
        raadgivendeLegeMangelfullBrukAvRaadgivendeLege = raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
        raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
        raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,

        // Annet
        annetFritekst = annetFritekst,
    )
}