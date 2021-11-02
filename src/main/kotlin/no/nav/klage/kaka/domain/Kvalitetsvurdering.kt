package no.nav.klage.kaka.domain

import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "kvalitetsvurdering", schema = "kaka")
@DynamicUpdate
class Kvalitetsvurdering(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "klageforberedelsen_radio_valg")
    var klageforberedelsenRadioValg: RadioValg? = null,
    @Column(name = "sakens_dokumenter")
    var sakensDokumenter: Boolean = false,
    @Column(name = "oversittet_klagefrist_ikke_kommentert")
    var oversittetKlagefristIkkeKommentert: Boolean = false,
    @Column(name = "klagerens_relevante_anfoersler_ikke_kommentert")
    var klagerensRelevanteAnfoerslerIkkeKommentert: Boolean = false,
    @Column(name = "begrunnelse_for_hvorfor_avslag_opprettholdes")
    var begrunnelseForHvorforAvslagOpprettholdes: Boolean = false,
    @Column(name = "konklusjonen")
    var konklusjonen: Boolean = false,
    @Column(name = "oversendelsesbrevets_innhold_ikke_i_samsvar_med_tema")
    var oversendelsesbrevetsInnholdIkkeISamsvarMedTema: Boolean = false,
    @Column(name = "utredningen_radio_valg")
    var utredningenRadioValg: RadioValg? = null,
    @Column(name = "utredningen_av_medisinske_forhold")
    var utredningenAvMedisinskeForhold: Boolean = false,
    @Column(name = "utredningen_av_medisinske_forhold_text")
    var utredningenAvMedisinskeForholdText: String? = null,
    @Column(name = "utredningen_av_inntektsforhold")
    var utredningenAvInntektsforhold: Boolean = false,
    @Column(name = "utredningen_av_inntektsforhold_text")
    var utredningenAvInntektsforholdText: String? = null,
    @Column(name = "utredningen_av_arbeid")
    var utredningenAvArbeid: Boolean = false,
    @Column(name = "utredningen_av_arbeid_text")
    var utredningenAvArbeidText: String? = null,
    @Column(name = "arbeidsrettet_brukeroppfoelging")
    var arbeidsrettetBrukeroppfoelging: Boolean = false,
    @Column(name = "arbeidsrettet_brukeroppfoelging_text")
    var arbeidsrettetBrukeroppfoelgingText: String? = null,
    @Column(name = "utredningen_av_andre_aktuelle_forhold_i_saken")
    var utredningenAvAndreAktuelleForholdISaken: Boolean = false,
    @Column(name = "utredningen_av_andre_aktuelle_forhold_i_saken_text")
    var utredningenAvAndreAktuelleForholdISakenText: String? = null,
    @Column(name = "utredningen_av_eoes_problematikk")
    var utredningenAvEoesProblematikk: Boolean = false,
    @Column(name = "utredningen_av_eoes_problematikk_text")
    var utredningenAvEoesProblematikkText: String? = null,
    @Column(name = "veiledning_fra_nav")
    var veiledningFraNav: Boolean = false,
    @Column(name = "veiledning_fra_nav_text")
    var veiledningFraNavText: String? = null,
    @Column(name = "bruk_av_raadgivende_lege_radio_valg")
    var brukAvRaadgivendeLegeRadioValg: RadioValgRaadgivendeLege? = null,
    @Column(name = "raadgivende_lege_er_ikke_brukt")
    var raadgivendeLegeErIkkeBrukt: Boolean = false,
    @Column(name = "raadgivende_lege_er_brukt_feil_spoersmaal")
    var raadgivendeLegeErBruktFeilSpoersmaal: Boolean = false,
    @Column(name = "raadgivende_lege_har_uttalt_seg_utover_trygdemedisin")
    var raadgivendeLegeHarUttaltSegUtoverTrygdemedisin: Boolean = false,
    @Column(name = "raadgivende_lege_er_brukt_mangelfull_dokumentasjon")
    var raadgivendeLegeErBruktMangelfullDokumentasjon: Boolean = false,
    @Column(name = "vedtaket_radio_valg")
    var vedtaketRadioValg: RadioValg? = null,
    @Column(name = "det_er_ikke_brukt_riktig_hjemmel")
    var detErIkkeBruktRiktigHjemmel: Boolean = false,
    @Column(name = "innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrevet")
    var innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean = false,
    @Column(name = "rettsregelen_er_benyttet_feil")
    var rettsregelenErBenyttetFeil: Boolean = false,
    @Column(name = "vurdering_av_faktum_er_mangelfull")
    var vurderingAvFaktumErMangelfull: Boolean = false,
    @Column(name = "det_er_feil_i_konkret_rettsanvendelse")
    var detErFeilIKonkretRettsanvendelse: Boolean = false,
    @Column(name = "begrunnelsen_er_ikke_konkret_og_individuell")
    var begrunnelsenErIkkeKonkretOgIndividuell: Boolean = false,
    @Column(name = "spraaket_er_ikke_tydelig")
    var spraaketErIkkeTydelig: Boolean = false,
    @Column(name = "nye_opplysninger_mottatt")
    var nyeOpplysningerMottatt: Boolean = false,
    @Column(name = "bruk_i_opplaering")
    var brukIOpplaering: Boolean = false,
    @Column(name = "bruk_i_opplaering_text")
    var brukIOpplaeringText: String? = null,
    @Column(name = "betydelig_avvik")
    var betydeligAvvik: Boolean = false,
    @Column(name = "betydelig_Avvik_text")
    var betydeligAvvikText: String? = null,
    @Column(name = "created")
    val created: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Kvalitetsvurdering

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    enum class RadioValgRaadgivendeLege {
        IKKE_AKTUELT,
        BRA,
        MANGELFULLT
    }

    enum class RadioValg {
        BRA,
        MANGELFULLT
    }
}

