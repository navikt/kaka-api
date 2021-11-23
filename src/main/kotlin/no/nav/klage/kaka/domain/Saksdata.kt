package no.nav.klage.kaka.domain

import no.nav.klage.kaka.api.view.SaksdataView
import no.nav.klage.kaka.domain.kodeverk.*
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.exceptions.ValidationErrorWithDetailsException
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "saksdata", schema = "kaka")
@DynamicUpdate
class Saksdata(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "saken_gjelder")
    var sakenGjelder: String? = null,
    @Column(name = "sakstype_id")
    @Convert(converter = SakstypeConverter::class)
    var sakstype: Sakstype? = null,
    @Column(name = "ytelse_id")
    @Convert(converter = YtelseConverter::class)
    var ytelse: Ytelse? = null,
    @Column(name = "dato_mottatt_vedtaksinstans")
    var mottattVedtaksinstans: LocalDate? = null,
    @Column(name = "vedtaksinstans_enhet")
    var vedtaksinstansEnhet: String? = null,
    @Column(name = "dato_mottatt_klageinstans")
    var mottattKlageinstans: LocalDate? = null,
    @Column(name = "utfall_id")
    @Convert(converter = UtfallConverter::class)
    var utfall: Utfall? = null,
    @ElementCollection(targetClass = Hjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "hjemmel",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "saksdata_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = HjemmelConverter::class)
    @Column(name = "id")
    var hjemler: Set<Hjemmel>? = null,
    @Column(name = "utfoerende_saksbehandlerident")
    var utfoerendeSaksbehandler: String,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "kvalitetsvurdering_id", referencedColumnName = "id")
    var kvalitetsvurdering: Kvalitetsvurdering,
    @Column(name = "dato_saksdata_avsluttet_av_saksbehandler")
    var avsluttetAvSaksbehandler: LocalDateTime? = null,
    @Column(name = "created")
    val created: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now()
) {

    override fun toString(): String {
        return "Saksdata(id=$id, " +
                "modified=$modified, " +
                "created=$created)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Saksdata

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun verifyAccess(innloggetIdent: String) {
        if (innloggetIdent != utfoerendeSaksbehandler) throw MissingTilgangException("Innlogget bruker har ikke tilgang til saksdataen")
    }

    fun validate() {
        val result = mutableListOf<ValidationErrorWithDetailsException.InvalidProperty>()

        if (sakenGjelder == null) {
            result.add(
                createMustBeFilledValidationError(SaksdataView::sakenGjelder.name)
            )
        }

        if (sakstype == null) {
            result.add(
                createMustBeSelectedValidationError(SaksdataView::sakstypeId.name)
            )
        }

//        if (tema == null) {
//            result.add(
//                createMustBeSelectedValidationError(SaksdataView::temaId.name)
//            )
//        }
//TODO: Reintroduce after testing
//        if (ytelse == null) {
//            result.add(
//                createMustBeSelectedValidationError(SaksdataView::ytelse.name)
//            )
//        }

        if (mottattVedtaksinstans == null) {
            result.add(
                createMustBeFilledValidationError(SaksdataView::mottattVedtaksinstans.name)
            )
        }

        if (vedtaksinstansEnhet == null) {
            result.add(
                createMustBeSelectedValidationError(SaksdataView::vedtaksinstansEnhet.name)
            )
        }

        if (mottattKlageinstans == null) {
            result.add(
                createMustBeFilledValidationError(SaksdataView::mottattKlageinstans.name)
            )
        }

        if (utfall == null) {
            result.add(
                createMustBeSelectedValidationError(SaksdataView::utfallId.name)
            )
        } else if (utfall != Utfall.TRUKKET) {
            if (hjemler.isNullOrEmpty()) {
                result.add(
                    createMustBeSelectedValidationError(SaksdataView::hjemmelIdList.name)
                )
            }
        }

        result.addAll(kvalitetsvurdering.getInvalidProperties(null))

        if (result.isNotEmpty()) {
            throw ValidationErrorWithDetailsException(
                title = "Validation error",
                invalidProperties = result
            )
        }
    }

    private fun createMustBeFilledValidationError(variableName: String): ValidationErrorWithDetailsException.InvalidProperty {
        return ValidationErrorWithDetailsException.InvalidProperty(
            field = variableName,
            reason = "Må fylles ut."
        )
    }

    private fun createMustBeSelectedValidationError(variableName: String): ValidationErrorWithDetailsException.InvalidProperty {
        return ValidationErrorWithDetailsException.InvalidProperty(
            field = variableName,
            reason = "Må være valgt."
        )
    }
}