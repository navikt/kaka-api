package no.nav.klage.kaka.domain.kvalitetsvurdering.v2

import jakarta.persistence.*
import no.nav.klage.kaka.domain.RegistreringshjemmelConverter
import no.nav.klage.kaka.domain.raadgivendeLegeYtelser
import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "kvalitetsvurdering_v2", schema = "kaka")
@DynamicUpdate
class KvalitetsvurderingV2(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    var klageforberedelsen: Radiovalg? = null,
    var klageforberedelsenOversittetKlagefristIkkeKommentert: Boolean = false,
    @Column(name = "klageforberedelsen_klagers_relevante_anfoersler_ikke_tilstrekke")
    var klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt: Boolean = false,

    var klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar: Boolean = false,
    @Column(name = "klageforberedelsen_oversendelsesbrevets_innhold_er_ikke_i_samsvar_med_sakens_tema")
    var klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema: Boolean = false,
    var klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker: Boolean = false,

    var klageforberedelsenSakensDokumenter: Boolean = false,
    var klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert: Boolean = false,
    var klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn: Boolean = false,
    var klageforberedelsenSakensDokumenterManglerFysiskSaksmappe: Boolean = false,

    @Enumerated(EnumType.STRING)
    var utredningen: Radiovalg? = null,
    var utredningenAvMedisinskeForhold: Boolean = false,
    var utredningenAvInntektsforhold: Boolean = false,
    var utredningenAvArbeidsaktivitet: Boolean = false,
    var utredningenAvEoesUtenlandsproblematikk: Boolean = false,
    @Column(name = "utredningen_av_andre_aktuelle_forhold_i_saken")
    var utredningenAvAndreAktuelleForholdISaken: Boolean = false,

    var vedtaketAutomatiskVedtak: Boolean = false,

    @Enumerated(EnumType.STRING)
    var vedtaket: Radiovalg? = null,
    var vedtaketDetErLagtTilGrunnFeilFaktum: Boolean = false,
    var vedtaketSpraakOgFormidlingErIkkeTydelig: Boolean = false,

    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList: Set<Registreringshjemmel>? = null,

    var vedtaketLovbestemmelsenTolketFeil: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemme",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketLovbestemmelsenTolketFeilHjemlerList: Set<Registreringshjemmel>? = null,

    @Column(name = "vedtaket_innholdet_i_rettsreglene_er_ikke_tilstrekkelig_beskrevet")
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholdet_i",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList: Set<Registreringshjemmel>? = null,

    var vedtaketFeilKonkretRettsanvendelse: Boolean = false,
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkre",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "kvalitetsvurdering_v2_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var vedtaketFeilKonkretRettsanvendelseHjemlerList: Set<Registreringshjemmel>? = null,

    var vedtaketIkkeKonkretIndividuellBegrunnelse: Boolean = false,
    @Column(name = "vedtaket_ikke_konkret_individuell_begrunnelse_faktum")
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum: Boolean = false,
    @Column(name = "vedtaket_ikke_konkret_individuell_begrunnelse_rettsregel")
    var vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum: Boolean = false,
    var vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst: Boolean = false,

    @Enumerated(EnumType.STRING)
    var brukAvRaadgivendeLege: RadiovalgRaadgivendeLege? = null,
    var raadgivendeLegeIkkebrukt: Boolean = false,
    var raadgivendeLegeMangelfullBrukAvRaadgivendeLege: Boolean = false,
    var raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin: Boolean = false,
    var raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert: Boolean = false,

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

    fun resetFieldsUnusedInAnke() {
        klageforberedelsen = null
        klageforberedelsenSakensDokumenter = false
        klageforberedelsenOversittetKlagefristIkkeKommentert = false
        klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = false
        klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = false
        klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = false
        klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = false

        klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = false
        klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = false
        klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = false
    }

    fun cleanup() {
        if (klageforberedelsen == Radiovalg.BRA) {
            klageforberedelsenSakensDokumenter = false
            klageforberedelsenOversittetKlagefristIkkeKommentert = false
            klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = false
            klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = false
            klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = false
            klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = false
            klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = false
            klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = false
            klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = false
        } else {
            if (!klageforberedelsenSakensDokumenter) {
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = false
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = false
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = false
            }
        }

        if (utredningen == Radiovalg.BRA) {
            utredningenAvMedisinskeForhold = false
            utredningenAvInntektsforhold = false
            utredningenAvArbeidsaktivitet = false
            utredningenAvEoesUtenlandsproblematikk = false
            utredningenAvAndreAktuelleForholdISaken = false
        }

        if (vedtaket == Radiovalg.BRA) {
            vedtaketLovbestemmelsenTolketFeil = false
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = false
            vedtaketFeilKonkretRettsanvendelse = false
            vedtaketIkkeKonkretIndividuellBegrunnelse = false
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = false
            vedtaketDetErLagtTilGrunnFeilFaktum = false
            vedtaketSpraakOgFormidlingErIkkeTydelig = false

            vedtaketLovbestemmelsenTolketFeilHjemlerList = null
            vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = null
            vedtaketFeilKonkretRettsanvendelseHjemlerList = null
            vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = null
        } else {
            if (!vedtaketLovbestemmelsenTolketFeil) {
                vedtaketLovbestemmelsenTolketFeilHjemlerList = null
            }
            if (!vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert) {
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = null
            }
            if (!vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet) {
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = null
            }
            if (!vedtaketFeilKonkretRettsanvendelse) {
                vedtaketFeilKonkretRettsanvendelseHjemlerList = null
            }

            if (!vedtaketIkkeKonkretIndividuellBegrunnelse) {
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = false
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = false
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = false
            }
        }

        if (brukAvRaadgivendeLege != RadiovalgRaadgivendeLege.MANGELFULLT) {
            raadgivendeLegeIkkebrukt = false
            raadgivendeLegeMangelfullBrukAvRaadgivendeLege = false
            raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = false
            raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = false
        }
    }

    fun getInvalidProperties(ytelse: Ytelse?, type: Type): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        result += getCommonInvalidProperties(ytelse)

        if (type == Type.KLAGE) {
            result += getSpecificInvalidPropertiesForKlage()
        }

        return result
    }

    private fun getCommonInvalidProperties(ytelse: Ytelse?): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()

        if (utredningen == null) {
            result.add(
                createRadioValgValidationError(::utredningen.name)
            )
        } else if (utredningen == Radiovalg.MANGELFULLT) {
            if (
                !utredningenAvMedisinskeForhold &&
                !utredningenAvInntektsforhold &&
                !utredningenAvArbeidsaktivitet &&
                !utredningenAvEoesUtenlandsproblematikk &&
                !utredningenAvAndreAktuelleForholdISaken
            ) {
                result.add(
                    createMissingChecksValidationError(::utredningen.name + "Group")
                )
            }
        }

        if (vedtaket == null) {
            result.add(
                createRadioValgValidationError(::vedtaket.name)
            )
        } else if (vedtaket == Radiovalg.MANGELFULLT) {
            if (
                !vedtaketLovbestemmelsenTolketFeil &&
                !vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert &&
                !vedtaketFeilKonkretRettsanvendelse &&
                !vedtaketIkkeKonkretIndividuellBegrunnelse &&
                !vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet &&
                !vedtaketDetErLagtTilGrunnFeilFaktum &&
                !vedtaketSpraakOgFormidlingErIkkeTydelig
            ) {
                result.add(
                    createMissingChecksValidationError(::vedtaket.name + "Group")
                )
            }

            if (vedtaketLovbestemmelsenTolketFeil && vedtaketLovbestemmelsenTolketFeilHjemlerList.isNullOrEmpty()) {
                result.add(
                    createMissingChecksValidationError(::vedtaketLovbestemmelsenTolketFeilHjemlerList.name)
                )
            }

            if (vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert && vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList.isNullOrEmpty()) {
                result.add(
                    createMissingChecksValidationError(::vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList.name)
                )
            }

            if (vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet && vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList.isNullOrEmpty()) {
                result.add(
                    createMissingChecksValidationError(::vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList.name)
                )
            }

            if (vedtaketFeilKonkretRettsanvendelse && vedtaketFeilKonkretRettsanvendelseHjemlerList.isNullOrEmpty()) {
                result.add(
                    createMissingChecksValidationError(::vedtaketFeilKonkretRettsanvendelseHjemlerList.name)
                )
            }
        }

        if (ytelse in raadgivendeLegeYtelser) {
            if (brukAvRaadgivendeLege == null) {
                result.add(
                    createRadioValgValidationError(::brukAvRaadgivendeLege.name)
                )
            } else if (brukAvRaadgivendeLege == RadiovalgRaadgivendeLege.MANGELFULLT) {
                if (
                    !raadgivendeLegeIkkebrukt &&
                    !raadgivendeLegeMangelfullBrukAvRaadgivendeLege &&
                    !raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin &&
                    !raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert
                ) {
                    result.add(
                        createMissingChecksValidationError(::brukAvRaadgivendeLege.name + "Group")
                    )
                }
            }
        }

        return result
    }

    private fun getSpecificInvalidPropertiesForKlage(): List<InvalidProperty> {
        val result = mutableListOf<InvalidProperty>()
        if (klageforberedelsen == null) {
            result.add(
                createRadioValgValidationError(::klageforberedelsen.name)
            )
        } else if (klageforberedelsen == Radiovalg.MANGELFULLT) {
            if (
                !klageforberedelsenSakensDokumenter &&
                !klageforberedelsenOversittetKlagefristIkkeKommentert &&
                !klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt &&
                !klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar &&
                !klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema &&
                !klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker
            ) {
                result.add(
                    createMissingChecksValidationError(::klageforberedelsen.name + "Group")
                )
            }
        }
        return result
    }

    private fun createMissingChecksValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg minst én."
        )
    }

    private fun createRadioValgValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Velg et alternativ."
        )
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
