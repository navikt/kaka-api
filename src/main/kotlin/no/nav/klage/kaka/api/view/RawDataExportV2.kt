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
    val mottattKlageinstans: Date,

    var sakensDokumenter: Boolean,
    var sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean,
    var sakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean,
    var sakensDokumenterManglerFysiskSaksmappe: Boolean,    
    var klageforberedelsen: String? = null,
    var klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten: Boolean,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean,
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt: Boolean,
    var klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes: Boolean,    
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean,    
    var utredningen: String? = null,
    var utredningenAvMedisinskeForhold: Boolean,
    var utredningenAvInntektsforhold: Boolean,
    var utredningenAvArbeidsaktivitet: Boolean,
    var utredningenAvEoesUtenlandsproblematikk: Boolean,    
    var utredningenAvAndreAktuelleForholdISaken: Boolean,
    var vedtaketLovbestemmelsenTolketFeil: Boolean,    
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean,    
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketFeilKonkretRettsanvendelse: Boolean,    
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: List<String>? = null,
    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean,
    var vedtaketIkkeGodtNokFremFaktum: Boolean,
    var vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean,
    var vedtaketMyeStandardtekst: Boolean,
    var vedtakAutomatiskVedtak: Boolean,    
    var vedtaket: String? = null,    
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort: Boolean,    
    var brukAvRaadgivendeLege: String? = null,
    var annetFritekst: String? = null,

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

    var sakensDokumenter: Boolean,
    var sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean,
    var sakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean,
    var sakensDokumenterManglerFysiskSaksmappe: Boolean,
    var klageforberedelsen: String? = null,
    var klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten: Boolean,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean,
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt: Boolean,
    var klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes: Boolean,
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean,
    var utredningen: String? = null,
    var utredningenAvMedisinskeForhold: Boolean,
    var utredningenAvInntektsforhold: Boolean,
    var utredningenAvArbeidsaktivitet: Boolean,
    var utredningenAvEoesUtenlandsproblematikk: Boolean,
    var utredningenAvAndreAktuelleForholdISaken: Boolean,
    var vedtaketLovbestemmelsenTolketFeil: Boolean,
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: List<String>? = null,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: List<String>? = null,
    var vedtaketFeilKonkretRettsanvendelse: Boolean,
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: List<String>? = null,
    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean,
    var vedtaketIkkeGodtNokFremFaktum: Boolean,
    var vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean,
    var vedtaketMyeStandardtekst: Boolean,
    var vedtakAutomatiskVedtak: Boolean,
    var vedtaket: String? = null,
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean,
    var raadgivendeLegeIkkebrukt: Boolean,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort: Boolean,
    var brukAvRaadgivendeLege: String? = null,
    var annetFritekst: String? = null,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class AnonymizedUnfinishedVurderingV2(
    /** unique and static id */
    val id: UUID,
    val sakstypeId: String,
    val tilknyttetEnhet: String,

    /** Første av de to created datoene (saksdata/kvalitetsvurdering). */
    val createdDate: Date,
    /** Siste av de to modified datoene (saksdata/kvalitetsvurdering). */
    val modifiedDate: Date,
)

data class TotalResponseV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV2>,
    val anonymizedUnfinishedVurderingList: List<AnonymizedUnfinishedVurderingV2>,
)

data class TotalResponseWithoutEnheterV2(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV2>
)