package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2

data class CensoredKvalitetsvurderingV2View(
    val klageforberedelsen: KvalitetsvurderingV2.Radiovalg?,
    val klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean,
    val klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt: Boolean,
    val klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar: Boolean,
    val klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean,
    val klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean,
    val klageforberedelsenSakensDokumenter: Boolean,
    val klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean,
    val klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean,
    val klageforberedelsenSakensDokumenterManglerFysiskSaksmappe: Boolean,
    val klageforberedelsenUtredningenUnderKlageforberedelsen: Boolean,
    val klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger: Boolean,
    val klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger: Boolean,
    val utredningen: KvalitetsvurderingV2.Radiovalg?,
    val utredningenAvMedisinskeForhold: Boolean,
    val utredningenAvInntektsforhold: Boolean,
    val utredningenAvArbeidsaktivitet: Boolean,
    val utredningenAvEoesUtenlandsproblematikk: Boolean,
    val utredningenAvSivilstandBoforhold: Boolean,
    val utredningenAvAndreAktuelleForholdISaken: Boolean,
    val vedtaketAutomatiskVedtak: Boolean,
    val vedtaket: KvalitetsvurderingV2.Radiovalg?,
    val vedtaketDetErLagtTilGrunnFeilFaktum: Boolean,
    val vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    val vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<String>?,
    val vedtaketBruktFeilHjemmel: Boolean,
    val vedtaketBruktFeilHjemmelHjemlerList: Set<String>?,
    val vedtaketAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    val vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<String>?,
    val vedtaketLovbestemmelsenTolketFeil: Boolean,
    val vedtaketLovbestemmelsenTolketFeilHjemlerList: Set<String>?,
    val vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    val vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList: Set<String>?,
    val vedtaketFeilKonkretRettsanvendelse: Boolean,
    val vedtaketFeilKonkretRettsanvendelseHjemlerList: Set<String>?,
    val vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean,
    val vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum: Boolean,
    val vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean,
    val vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst: Boolean,
    val brukAvRaadgivendeLege: KvalitetsvurderingV2.RadiovalgRaadgivendeLege?,
    val raadgivendeLegeIkkebrukt: Boolean,
    val raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    val raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    val raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,
)

fun KvalitetsvurderingV2.toCensoredKvalitetsvurderingV2View(): CensoredKvalitetsvurderingV2View {
    return CensoredKvalitetsvurderingV2View(
        klageforberedelsen = klageforberedelsen,
        klageforberedelsenOversittetKlagefristIkkeKommentert = klageforberedelsenOversittetKlagefristIkkeKommentert,
        klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
        klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
        klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
        klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
        klageforberedelsenSakensDokumenter = klageforberedelsenSakensDokumenter,
        klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
        klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
        klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
        klageforberedelsenUtredningenUnderKlageforberedelsen = klageforberedelsenUtredningenUnderKlageforberedelsen,
        klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
        klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
        utredningen = utredningen,
        utredningenAvMedisinskeForhold = utredningenAvMedisinskeForhold,
        utredningenAvInntektsforhold = utredningenAvInntektsforhold,
        utredningenAvArbeidsaktivitet = utredningenAvArbeidsaktivitet,
        utredningenAvEoesUtenlandsproblematikk = utredningenAvEoesUtenlandsproblematikk,
        utredningenAvSivilstandBoforhold = utredningenAvSivilstandBoforhold,
        utredningenAvAndreAktuelleForholdISaken = utredningenAvAndreAktuelleForholdISaken,
        vedtaketAutomatiskVedtak = vedtaketAutomatiskVedtak,
        vedtaket = vedtaket,
        vedtaketDetErLagtTilGrunnFeilFaktum = vedtaketDetErLagtTilGrunnFeilFaktum,
        vedtaketSpraakOgFormidlingErIkkeTydelig = vedtaketSpraakOgFormidlingErIkkeTydelig,
        vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
        vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id }?.toSet(),
        vedtaketBruktFeilHjemmel = vedtaketBruktFeilHjemmel,
        vedtaketBruktFeilHjemmelHjemlerList = vedtaketBruktFeilHjemmelHjemlerList?.map { it.id }?.toSet(),
        vedtaketAlleRelevanteHjemlerErIkkeVurdert = vedtaketAlleRelevanteHjemlerErIkkeVurdert,
        vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id }?.toSet(),
        vedtaketLovbestemmelsenTolketFeil = vedtaketLovbestemmelsenTolketFeil,
        vedtaketLovbestemmelsenTolketFeilHjemlerList = vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id }?.toSet(),
        vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
        vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id }?.toSet(),
        vedtaketFeilKonkretRettsanvendelse = vedtaketFeilKonkretRettsanvendelse,
        vedtaketFeilKonkretRettsanvendelseHjemlerList = vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id }?.toSet(),
        vedtaketIkkeKonkretIndividuellBegrunnelse = vedtaketIkkeKonkretIndividuellBegrunnelse,
        vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
        vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
        vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
        brukAvRaadgivendeLege = brukAvRaadgivendeLege,
        raadgivendeLegeIkkebrukt = raadgivendeLegeIkkebrukt,
        raadgivendeLegeMangelfullBrukAvRaadgivendeLege = raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
        raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
        raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
        )
}