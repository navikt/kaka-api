package no.nav.klage.kaka.api.view

import java.util.*

data class AnonymizedFinishedVurderingV1(
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

    val arbeidsrettetBrukeroppfoelging: Boolean,
    val begrunnelseForHvorforAvslagOpprettholdes: Boolean,
    val begrunnelsenErIkkeKonkretOgIndividuell: Boolean,
    val betydeligAvvik: Boolean,
    val brukIOpplaering: Boolean,
    val detErFeilIKonkretRettsanvendelse: Boolean,
    val detErIkkeBruktRiktigHjemmel: Boolean,
    val innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    val klagerensRelevanteAnfoerslerIkkeKommentert: Boolean,
    val konklusjonen: Boolean,
    val nyeOpplysningerMottatt: Boolean,
    val oversendelsesbrevetsInnholdIkkeISamsvarMedTema: Boolean,
    val oversittetKlagefristIkkeKommentert: Boolean,
    val raadgivendeLegeErBruktFeilSpoersmaal: Boolean,
    val raadgivendeLegeErBruktMangelfullDokumentasjon: Boolean,
    val raadgivendeLegeErIkkeBrukt: Boolean,
    val raadgivendeLegeHarUttaltSegUtoverTrygdemedisin: Boolean,
    val rettsregelenErBenyttetFeil: Boolean,
    val sakensDokumenter: Boolean,
    val spraaketErIkkeTydelig: Boolean,
    val utredningenAvAndreAktuelleForholdISaken: Boolean,
    val utredningenAvArbeid: Boolean,
    val utredningenAvEoesProblematikk: Boolean,
    val utredningenAvInntektsforhold: Boolean,
    val utredningenAvMedisinskeForhold: Boolean,
    val veiledningFraNav: Boolean,
    val vurderingAvFaktumErMangelfull: Boolean,
    val klageforberedelsenRadioValg: String?,
    val utredningenRadioValg: String?,
    val vedtaketRadioValg: String?,
    val brukAvRaadgivendeLegeRadioValg: String?,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class AnonymizedFinishedVurderingWithoutEnheterV1(
    /** unique and static id */
    val id: UUID,
    val hjemmelIdList: List<String>,
    val avsluttetAvSaksbehandler: Date,
    val ytelseId: String,
    val utfallId: String,
    val sakstypeId: String,
    val mottattVedtaksinstans: Date?,
    val mottattKlageinstans: Date,

    val arbeidsrettetBrukeroppfoelging: Boolean,
    val begrunnelseForHvorforAvslagOpprettholdes: Boolean,
    val begrunnelsenErIkkeKonkretOgIndividuell: Boolean,
    val betydeligAvvik: Boolean,
    val brukIOpplaering: Boolean,
    val detErFeilIKonkretRettsanvendelse: Boolean,
    val detErIkkeBruktRiktigHjemmel: Boolean,
    val innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean,
    val klagerensRelevanteAnfoerslerIkkeKommentert: Boolean,
    val konklusjonen: Boolean,
    val nyeOpplysningerMottatt: Boolean,
    val oversendelsesbrevetsInnholdIkkeISamsvarMedTema: Boolean,
    val oversittetKlagefristIkkeKommentert: Boolean,
    val raadgivendeLegeErBruktFeilSpoersmaal: Boolean,
    val raadgivendeLegeErBruktMangelfullDokumentasjon: Boolean,
    val raadgivendeLegeErIkkeBrukt: Boolean,
    val raadgivendeLegeHarUttaltSegUtoverTrygdemedisin: Boolean,
    val rettsregelenErBenyttetFeil: Boolean,
    val sakensDokumenter: Boolean,
    val spraaketErIkkeTydelig: Boolean,
    val utredningenAvAndreAktuelleForholdISaken: Boolean,
    val utredningenAvArbeid: Boolean,
    val utredningenAvEoesProblematikk: Boolean,
    val utredningenAvInntektsforhold: Boolean,
    val utredningenAvMedisinskeForhold: Boolean,
    val veiledningFraNav: Boolean,
    val vurderingAvFaktumErMangelfull: Boolean,
    val klageforberedelsenRadioValg: String?,
    val utredningenRadioValg: String?,
    val vedtaketRadioValg: String?,
    val brukAvRaadgivendeLegeRadioValg: String?,

    val kaBehandlingstidDays: Int,
    val vedtaksinstansBehandlingstidDays: Int,
    val totalBehandlingstidDays: Int,

    /** Første av de to created datoene. */
    val createdDate: Date,
    /** Siste av de to modified datoene. */
    val modifiedDate: Date,
)

data class Date(
    val weekNumber: Int,
    val year: Int,
    val month: Int,
    val day: Int,
    val iso: String,
    val epochDay: Int
)

data class ManagerResponseV1(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV1>,
    val saksbehandlere: Map<String, List<AnonymizedFinishedVurderingV1>>?,
    val mine: List<AnonymizedFinishedVurderingV1>,
    val rest: List<AnonymizedFinishedVurderingV1>,
)

data class TotalResponseV1(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV1>,
    val rest: List<AnonymizedFinishedVurderingV1>,
)

data class MyResponseV1(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingV1>,
    val mine: List<AnonymizedFinishedVurderingV1>,
    val rest: List<AnonymizedFinishedVurderingV1>,
)

data class OpenResponseWithoutEnheterV1(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV1>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV1>,
)

data class VedtaksinstanslederResponseV1(
    val anonymizedFinishedVurderingList: List<AnonymizedFinishedVurderingWithoutEnheterV1>,
    val mine: List<AnonymizedFinishedVurderingWithoutEnheterV1>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV1>,
)