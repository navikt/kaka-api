package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3.Radiovalg
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3.RadiovalgRaadgivendeLege
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel

data class KvalitetsvurderingV3Input(
    // Kvalitetsavvik i forvaltningen av særregelverket
    val saerregelverkAutomatiskVedtak: Boolean?,
    val saerregelverk: Radiovalg?,

    // Loven er tolket eller anvendt feil i vedtaket
    val saerregelverkLovenErTolketEllerAnvendtFeil: Boolean?,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning: Boolean?,
    val saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList: Set<Registreringshjemmel>?,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn: Boolean?,
    val saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList: Set<Registreringshjemmel>?,
    val saerregelverkDetErLagtTilGrunnFeilFaktum: Boolean?,
    val saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList: Set<Registreringshjemmel>?,

    // Kvalitetsavvik i forvaltningen av saksbehandlingsreglene
    val saksbehandlingsregler: Radiovalg?,

    // Brudd på veiledningsplikten
    val saksbehandlingsreglerBruddPaaVeiledningsplikten: Boolean?,
    val saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser: Boolean?,
    val saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning: Boolean?,

    // Brudd på utredningsplikten
    val saksbehandlingsreglerBruddPaaUtredningsplikten: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok: Boolean?,
    val saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok: Boolean?,

    // Brudd på foreleggelsesplikten
    val saksbehandlingsreglerBruddPaaForeleggelsesplikten: Boolean?,
    val saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten: Boolean?,
    val saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten: Boolean?,

    // Brudd på begrunnelsesplikten
    val saksbehandlingsreglerBruddPaaBegrunnelsesplikten: Boolean?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket: Boolean?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList: Set<Registreringshjemmel>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum: Boolean?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList: Set<Registreringshjemmel>?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn: Boolean?,
    val saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList: Set<Registreringshjemmel>?,

    // Brudd på reglene om klage og klageforberedelse
    val saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse: Boolean?,
    val saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert: Boolean?,
    val saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold: Boolean?,
    val saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser: Boolean?,

    // Brudd på reglene om omgjøring utenfor ordinær klage- og ankesaksbehandling
    val saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke: Boolean?,
    val saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert: Boolean?,
    val saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform: Boolean?,

    // Brudd på journalføringsplikten
    val saksbehandlingsreglerBruddPaaJournalfoeringsplikten: Boolean?,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert: Boolean?,
    val saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet: Boolean?,

    // Brudd på plikten til å kommunisere på et klart språk
    val saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak: Boolean?,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok: Boolean?,
    val saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok: Boolean?,

    // Kvalitetsavvik i saker med trygdemedisin
    val brukAvRaadgivendeLege: RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean?,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean?,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean?,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean?,

    // Annet
    val annetFritekst: String?,
)