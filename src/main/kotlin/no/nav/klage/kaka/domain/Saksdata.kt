package no.nav.klage.kaka.domain

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
    @Column(name = "tema_id")
    @Convert(converter = TemaConverter::class)
    var tema: Tema? = null,
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
                createMustBeFilledValidationError(::sakenGjelder.name)
            )
        }

        if (sakstype == null) {
            result.add(
                createMustBeFilledValidationError(::sakstype.name)
            )
        }

        if (tema == null) {
            result.add(
                createMustBeFilledValidationError(::tema.name)
            )
        }

        if (mottattVedtaksinstans == null) {
            result.add(
                createMustBeFilledValidationError(::mottattVedtaksinstans.name)
            )
        }

        if (vedtaksinstansEnhet == null) {
            result.add(
                createMustBeFilledValidationError(::vedtaksinstansEnhet.name)
            )
        }

        if (mottattKlageinstans == null) {
            result.add(
                createMustBeFilledValidationError(::mottattKlageinstans.name)
            )
        }

        if (utfall == null) {
            result.add(
                createMustBeFilledValidationError(::utfall.name)
            )
        } else if (utfall != Utfall.TRUKKET) {
            if (hjemler == null || hjemler!!.isEmpty()) {
                result.add(
                    createMustBeFilledValidationError(::hjemler.name)
                )
            }
        }

        result.addAll(kvalitetsvurdering.validate(tema))

        if (result.size > 0) {
            throw ValidationErrorWithDetailsException(
                title = "Validation error",
                invalidProperties = result
            )
        }
    }

    private fun createMustBeFilledValidationError(variableName: String): ValidationErrorWithDetailsException.InvalidProperty {
        return ValidationErrorWithDetailsException.InvalidProperty(
            field = variableName,
            reason = "$variableName må være fylt ut."
        )
    }
}