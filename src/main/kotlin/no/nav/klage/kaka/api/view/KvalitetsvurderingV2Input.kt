package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2.Radiovalg
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2.RadiovalgRaadgivendeLege
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel

data class KvalitetsvurderingV2Input (
    val sakensDokumenter: Boolean?,
    val sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean?,
    val sakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean?,
    val sakensDokumenterManglerFysiskSaksmappe: Boolean?,
    val klageforberedelsen: Radiovalg?,
    val klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten: Boolean?,
    val klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean?,
    val klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt: Boolean?,
    val klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes: Boolean?,
    val klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean?,
    val klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean?,
    val utredningen: Radiovalg?,
    val utredningenAvMedisinskeForhold: Boolean?,
    val utredningenAvInntektsforhold: Boolean?,
    val utredningenAvArbeidsaktivitet: Boolean?,
    val utredningenAvEoesUtenlandsproblematikk: Boolean?,
    val utredningenAvAndreAktuelleForholdISaken: Boolean?,

    val vedtaketLovbestemmelsenTolketFeil: Boolean?,
    val vedtaketLovbestemmelsenTolketFeilHjemlerList: Set<Registreringshjemmel>?,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean?,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<Registreringshjemmel>?,
    val vedtaketFeilKonkretRettsanvendelse: Boolean?,
    val vedtaketFeilKonkretRettsanvendelseHjemlerList: Set<Registreringshjemmel>?,

    val vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean?,
    val vedtaketIkkeGodtNokFremFaktum: Boolean?,
    val vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean?,
    val vedtaketMyeStandardtekst: Boolean?,

    val vedtakAutomatiskVedtak: Boolean?,
    val vedtaket: Radiovalg?,
    val vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean?,
    val vedtaketDetErLagtTilGrunnFeilFaktum: Boolean?,
    val vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean?,

    val brukAvRaadgivendeLege: RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean?,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean?,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean?,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort: Boolean?,
    val annetFritekst: String?,
)