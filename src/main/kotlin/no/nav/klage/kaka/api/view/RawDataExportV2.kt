package no.nav.klage.kaka.api.view

import java.util.*

data class AnonymizedFinishedVurderingV2(
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

    var klageforberedelsenSakensDokumenter: Boolean,
    var klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean,
    var klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean,
    var klageforberedelsenSakensDokumenterManglerFysiskSaksmappe: Boolean,
    var klageforberedelsen: String? = null,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean,
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt: Boolean,
    var klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar: Boolean,
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsen: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger: Boolean,
    var utredningen: String? = null,
    var utredningenAvMedisinskeForhold: Boolean,
    var utredningenAvInntektsforhold: Boolean,
    var utredningenAvArbeidsaktivitet: Boolean,
    var utredningenAvEoesUtenlandsproblematikk: Boolean,
    var utredningenAvAndreAktuelleForholdISaken: Boolean,
    var utredningenAvSivilstandBoforhold: Boolean,
    var vedtaketLovbestemmelsenTolketFeil: Boolean,
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmel: Boolean,
    var vedtaketBruktFeilHjemmelHjemlerList: List<String>? = null,
    var vedtaketAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    var vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketFeilKonkretRettsanvendelse: Boolean,
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: List<String>? = null,
    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst: Boolean,
    var vedtaketAutomatiskVedtak: Boolean,
    var vedtaket: String? = null,
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList: List<String>? = null,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,
    var brukAvRaadgivendeLege: String? = null,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class AnonymizedFinishedVurderingWithoutEnheterV2(
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

    var klageforberedelsenSakensDokumenter: Boolean,
    var klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean,
    var klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean,
    var klageforberedelsenSakensDokumenterManglerFysiskSaksmappe: Boolean,
    var klageforberedelsen: String? = null,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean,
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt: Boolean,
    var klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar: Boolean,
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsen: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger: Boolean,
    var klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger: Boolean,
    var utredningen: String? = null,
    var utredningenAvMedisinskeForhold: Boolean,
    var utredningenAvInntektsforhold: Boolean,
    var utredningenAvArbeidsaktivitet: Boolean,
    var utredningenAvEoesUtenlandsproblematikk: Boolean,
    var utredningenAvAndreAktuelleForholdISaken: Boolean,
    var utredningenAvSivilstandBoforhold: Boolean,
    var vedtaketLovbestemmelsenTolketFeil: Boolean,
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmel: Boolean,
    var vedtaketBruktFeilHjemmelHjemlerList: List<String>? = null,
    var vedtaketAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    var vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketFeilKonkretRettsanvendelse: Boolean,
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: List<String>? = null,
    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean,
    var vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst: Boolean,
    var vedtaketAutomatiskVedtak: Boolean,
    var vedtaket: String? = null,
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList: List<String>? = null,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean,
    var brukAvRaadgivendeLege: String? = null,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class ManagerResponseV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV2>,
    val saksbehandlere: Map<String, List<AnonymizedFinishedVurderingV2>>?,
    val mine: List<AnonymizedFinishedVurderingV2>,
    val rest: List<AnonymizedFinishedVurderingV2>,
)

data class TotalResponseV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV2>,
    val rest: List<AnonymizedFinishedVurderingV2>,
)

data class MyResponseV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV2>,
    val mine: List<AnonymizedFinishedVurderingV2>,
    val rest: List<AnonymizedFinishedVurderingV2>,
)

data class OpenResponseWithoutEnheterV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
)

data class VedtaksinstanslederResponseV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
    val mine: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
)
