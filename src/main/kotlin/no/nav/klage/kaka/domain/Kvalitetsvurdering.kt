package no.nav.klage.kaka.domain

import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.Ytelse.*
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
    @Column(name = "betydelig_avvik_text")
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

    fun cleanup() {
        if (!brukIOpplaering) {
            brukIOpplaeringText = null
        }
        if (!betydeligAvvik) {
            betydeligAvvikText = null
        }

        if (klageforberedelsenRadioValg == RadioValg.BRA) {
            sakensDokumenter = false
            oversittetKlagefristIkkeKommentert = false
            klagerensRelevanteAnfoerslerIkkeKommentert = false
            begrunnelseForHvorforAvslagOpprettholdes = false
            konklusjonen = false
            oversendelsesbrevetsInnholdIkkeISamsvarMedTema = false
        }

        if (utredningenRadioValg == RadioValg.BRA) {
            utredningenAvMedisinskeForhold = false
            utredningenAvMedisinskeForholdText = null
            utredningenAvInntektsforhold = false
            utredningenAvInntektsforholdText = null
            utredningenAvArbeid = false
            utredningenAvArbeidText = null
            arbeidsrettetBrukeroppfoelging = false
            arbeidsrettetBrukeroppfoelgingText = null
            utredningenAvAndreAktuelleForholdISaken = false
            utredningenAvAndreAktuelleForholdISakenText = null
            utredningenAvEoesProblematikk = false
            utredningenAvEoesProblematikkText = null
            veiledningFraNav = false
            veiledningFraNavText = null
        } else {
            if (!utredningenAvMedisinskeForhold) {
                utredningenAvMedisinskeForholdText = null
            }
            if (!utredningenAvInntektsforhold) {
                utredningenAvInntektsforholdText = null
            }
            if (!utredningenAvArbeid) {
                utredningenAvArbeidText = null
            }
            if (!arbeidsrettetBrukeroppfoelging) {
                arbeidsrettetBrukeroppfoelgingText = null
            }
            if (!utredningenAvAndreAktuelleForholdISaken) {
                utredningenAvAndreAktuelleForholdISakenText = null
            }
            if (!utredningenAvEoesProblematikk) {
                utredningenAvEoesProblematikkText = null
            }
            if (!veiledningFraNav) {
                veiledningFraNavText = null
            }
        }

        if (brukAvRaadgivendeLegeRadioValg != null && brukAvRaadgivendeLegeRadioValg == RadioValgRaadgivendeLege.BRA) {
            raadgivendeLegeErIkkeBrukt = false
            raadgivendeLegeErBruktFeilSpoersmaal = false
            raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = false
            raadgivendeLegeErBruktMangelfullDokumentasjon = false
        }

        if (vedtaketRadioValg == RadioValg.BRA) {
            detErIkkeBruktRiktigHjemmel = false
            innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = false
            rettsregelenErBenyttetFeil = false
            vurderingAvFaktumErMangelfull = false
            detErFeilIKonkretRettsanvendelse = false
            begrunnelsenErIkkeKonkretOgIndividuell = false
            spraaketErIkkeTydelig = false
            nyeOpplysningerMottatt = false
        }
    }

    fun getInvalidProperties(ytelse: Ytelse?, type: Type?): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        result += getCommonInvalidProperties(ytelse)

        if (type == Type.KLAGE) {
            result += getSpecificInvalidPropertiesForKlage()
        }

        return result
    }

    private fun getCommonInvalidProperties(ytelse: Ytelse?): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        if (utredningenRadioValg == null) {
            result.add(
                createRadioValgValidationError(::utredningenRadioValg.name)
            )
        } else if (utredningenRadioValg == RadioValg.MANGELFULLT) {
            if (
                !utredningenAvMedisinskeForhold &&
                !utredningenAvInntektsforhold &&
                !utredningenAvArbeid &&
                !arbeidsrettetBrukeroppfoelging &&
                !utredningenAvAndreAktuelleForholdISaken &&
                !utredningenAvEoesProblematikk &&
                !veiledningFraNav
            ) {
                result.add(
                    createMissingChecksValidationError(::utredningenRadioValg.name)
                )
            }
        }

        if (ytelse in raadgivendeLegeYtelser) {
            if (brukAvRaadgivendeLegeRadioValg == null) {
                result.add(
                    createRadioValgValidationError(::brukAvRaadgivendeLegeRadioValg.name)
                )
            } else if (brukAvRaadgivendeLegeRadioValg == RadioValgRaadgivendeLege.MANGELFULLT) {
                if (
                    !raadgivendeLegeErIkkeBrukt &&
                    !raadgivendeLegeErBruktFeilSpoersmaal &&
                    !raadgivendeLegeHarUttaltSegUtoverTrygdemedisin &&
                    !raadgivendeLegeErBruktMangelfullDokumentasjon
                ) {
                    result.add(
                        createMissingChecksValidationError(::brukAvRaadgivendeLegeRadioValg.name)
                    )
                }
            }
        }

        if (vedtaketRadioValg == null) {
            result.add(
                createRadioValgValidationError(::vedtaketRadioValg.name)
            )
        } else if (vedtaketRadioValg == RadioValg.MANGELFULLT) {
            if (
                !detErIkkeBruktRiktigHjemmel &&
                !innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet &&
                !rettsregelenErBenyttetFeil &&
                !vurderingAvFaktumErMangelfull &&
                !detErFeilIKonkretRettsanvendelse &&
                !begrunnelsenErIkkeKonkretOgIndividuell &&
                !spraaketErIkkeTydelig &&
                !nyeOpplysningerMottatt
            ) {
                result.add(
                    createMissingChecksValidationError(::vedtaketRadioValg.name)
                )
            }
        }
        return result
    }

    private fun getSpecificInvalidPropertiesForKlage(): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()
        if (klageforberedelsenRadioValg == null) {
            result.add(
                createRadioValgValidationError(::klageforberedelsenRadioValg.name)
            )
        } else if (klageforberedelsenRadioValg == RadioValg.MANGELFULLT) {
            if (
                !sakensDokumenter &&
                !oversittetKlagefristIkkeKommentert &&
                !klagerensRelevanteAnfoerslerIkkeKommentert &&
                !begrunnelseForHvorforAvslagOpprettholdes &&
                !konklusjonen &&
                !oversendelsesbrevetsInnholdIkkeISamsvarMedTema
            ) {
                result.add(
                    createMissingChecksValidationError(::klageforberedelsenRadioValg.name)
                )
            }
        }
        return result
    }

    private fun createRadioValgValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg et alternativ."
        )
    }

    private fun createMissingChecksValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg minst én."
        )
    }
}

private val raadgivendeLegeYtelser = setOf(
    GRU_GRU,
    HJE_HJE,
    SYK_SYK,
    OMS_OMP,
    OMS_OLP,
    OMS_PSB,
    OMS_PLS,
    HJE_HJE,
    AAP_AAP,
    UFO_UFO,
    YRK_YRK,
    YRK_YSY,
    YRK_MEN,
    FOR_FOR,
    FOR_SVA,
    UFO_TVF,
)

