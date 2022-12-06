package no.nav.klage.kaka.domain.kvalitetsvurdering.v2

import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.RegistreringshjemmelConverter
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "kvalitetsvurdering_v2", schema = "kaka")
@DynamicUpdate
class KvalitetsvurderingV2(
    @Id
    val id: UUID = UUID.randomUUID(),
    var sakensDokumenter: Boolean = false,
    var sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean = false,
    var sakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean = false,
    var sakensDokumenterManglerFysiskSaksmappe: Boolean = false,
    @Enumerated(EnumType.STRING)
    var klageforberedelsen: Radiovalg? = null,
    var klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten: Boolean = false,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean = false,
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt: Boolean = false,
    var klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes: Boolean = false,
    @Column(name = "klageforberedelsen_oversendelsesbrevets_innhold_er_ikke_i_samsvar_med_sakens_tema")
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean = false,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean = false,
    @Enumerated(EnumType.STRING)
    var utredningen: Radiovalg? = null,
    var utredningenAvMedisinskeForhold: Boolean = false,
    var utredningenAvInntektsforhold: Boolean = false,
    var utredningenAvArbeidsaktivitet: Boolean = false,
    var utredningenAvEoesUtenlandsproblematikk: Boolean = false,
    @Column(name = "utredningen_av_andre_aktuelle_forhold_i_saken")
    var utredningenAvAndreAktuelleForholdISaken: Boolean = false,
    var vedtaketLovbestemmelsenTolketFeil: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemmelsen_tolket_feil_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: Set<Registreringshjemmel>? = null,
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_hjemmel_eller_alle_relevante_hjemler_er_ikke_vurdert_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<Registreringshjemmel>? = null,
    var vedtaketFeilKonkretRettsanvendelse: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkret_rettsanvendelse_hjemler_list",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: Set<Registreringshjemmel>? = null,
    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean = false,
    var vedtaketIkkeGodtNokFremFaktum: Boolean = false,
    var vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean = false,
    var vedtaketMyeStandardtekst: Boolean = false,
    var vedtakAutomatiskVedtak: Boolean = false,
    @Enumerated(EnumType.STRING)
    var vedtaket: Radiovalg? = null,
    @Column(name = "vedtaket_innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrevet")
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean = false,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean = false,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean = false,
    var raadgivendeLegeIkkebrukt: Boolean = false,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean = false,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean = false,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort: Boolean = false,
    @Enumerated(EnumType.STRING)
    var brukAvRaadgivendeLege: RadiovalgRaadgivendeLege? = null,
    var annetFritekst: String? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    var modified: LocalDateTime = LocalDateTime.now()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KvalitetsvurderingV2

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    //TODO
    fun getInvalidProperties(ytelse: Ytelse?, type: Type): List<InvalidProperty> {
        return emptyList()
    }

    enum class Radiovalg {
        BRA,
        MANGELFULLT
    }

    enum class RadiovalgRaadgivendeLege {
        IKKE_AKTUELT,
        BRA,
        MANGELFULLT
    }
}
