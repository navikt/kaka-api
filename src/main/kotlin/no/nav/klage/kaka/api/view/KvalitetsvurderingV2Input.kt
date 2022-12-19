package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2.Radiovalg
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2.RadiovalgRaadgivendeLege
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel

data class KvalitetsvurderingV2Input(
    val klageforberedelsen: Radiovalg?,
    val klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean?,
    val klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt: Boolean?,
    val klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar: Boolean?,
    val klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean?,
    val klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean?,
    val klageforberedelsenSakensDokumenter: Boolean?,
    val klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean?,
    val klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean?,
    val klageforberedelsenSakensDokumenterManglerFysiskSaksmappe: Boolean?,
    val utredningen: Radiovalg?,
    val utredningenAvMedisinskeForhold: Boolean?,
    val utredningenAvInntektsforhold: Boolean?,
    val utredningenAvArbeidsaktivitet: Boolean?,
    val utredningenAvEoesUtenlandsproblematikk: Boolean?,
    val utredningenAvAndreAktuelleForholdISaken: Boolean?,
    val vedtaketAutomatiskVedtak: Boolean?,
    val vedtaket: Radiovalg?,
    val vedtaketDetErLagtTilGrunnFeilFaktum: Boolean?,
    val vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean?,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean?,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<Registreringshjemmel>?,
    val vedtaketLovbestemmelsenTolketFeil: Boolean?,
    val vedtaketLovbestemmelsenTolketFeilHjemlerList: Set<Registreringshjemmel>? ,
    val vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean?,
    val vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList: Set<Registreringshjemmel>?,
    val vedtaketFeilKonkretRettsanvendelse: Boolean?,
    val vedtaketFeilKonkretRettsanvendelseHjemlerList: Set<Registreringshjemmel>?,
    val vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean?,
    val vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum: Boolean?,
    val vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean?,
    val vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst: Boolean?,
    val brukAvRaadgivendeLege: RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean?,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean?,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean?,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean?,
    val annetFritekst: String?,
)