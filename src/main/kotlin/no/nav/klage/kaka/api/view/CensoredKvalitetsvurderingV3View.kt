package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3

data class CensoredKvalitetsvurderingV3View(
    // Særregelverket
    val saerregelverkAutomatiskVedtak: Boolean,
    val saerregelverk: KvalitetsvurderingV3.Radiovalg?,
    val saerregelverkLovenErTolketEllerAnvendtFeil: Boolean,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: Set<String>?,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: Set<String>?,
    val saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean,
    val saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: Set<String>?,

    // Saksbehandlingsregler
    val saksbehandlingsregler: KvalitetsvurderingV3.Radiovalg?,

    // Veiledningsplikten
    val saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean,
    val saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean,
    val saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean,

    // Utredningsplikten
    val saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean,

    // Foreleggelsesplikten
    val saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean,
    val saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean,
    val saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean,

    // Begrunnelsesplikten
    val saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: Set<String>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: Set<String>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: Set<String>?,

    // Klage og klageforberedelse
    val saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean,
    val saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean,
    val saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean,
    val saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean,

    // Omgjøring
    val saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean,
    val saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean,
    val saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean,

    // Journalføringsplikten
    val saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean,

    // Klart språk
    val saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean,

    // Trygdemedisin
    val brukAvRaadgivendeLege: KvalitetsvurderingV3.RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,
)

fun KvalitetsvurderingV3.toCensoredKvalitetsvurderingV3View(): CensoredKvalitetsvurderingV3View {
    return CensoredKvalitetsvurderingV3View(
        // Særregelverket
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

        // Saksbehandlingsregler
        saksbehandlingsregler = saksbehandlingsregler,

        // Veiledningsplikten
        saksbehandlingsreglerBruddPaaVeiledningsplikten = saksbehandlingsreglerBruddPaaVeiledningsplikten,
        saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
        saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,

        // Utredningsplikten
        saksbehandlingsreglerBruddPaaUtredningsplikten = saksbehandlingsreglerBruddPaaUtredningsplikten,
        saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
        saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,

        // Foreleggelsesplikten
        saksbehandlingsreglerBruddPaaForeleggelsesplikten = saksbehandlingsreglerBruddPaaForeleggelsesplikten,
        saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
        saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,

        // Begrunnelsesplikten
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

        // Klage og klageforberedelse
        saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
        saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
        saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
        saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,

        // Omgjøring
        saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
        saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
        saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,

        // Journalføringsplikten
        saksbehandlingsreglerBruddPaaJournalfoeringsplikten = saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
        saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
        saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,

        // Klart språk
        saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
        saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
        saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,

        // Trygdemedisin
        brukAvRaadgivendeLege = brukAvRaadgivendeLege,
        raadgivendeLegeIkkebrukt = raadgivendeLegeIkkebrukt,
        raadgivendeLegeMangelfullBrukAvRaadgivendeLege = raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
        raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
        raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
    )
}