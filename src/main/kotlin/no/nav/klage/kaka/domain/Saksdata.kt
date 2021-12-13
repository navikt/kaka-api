package no.nav.klage.kaka.domain

import no.nav.klage.kaka.api.view.SaksdataView
import no.nav.klage.kaka.exceptions.InvalidProperty
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.exceptions.SectionedValidationErrorWithDetailsException
import no.nav.klage.kaka.exceptions.ValidationSection
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.RegistreringshjemmelConverter
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
    @Convert(converter = TypeConverter::class)
    var sakstype: Type? = null,
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
    @ElementCollection(targetClass = Registreringshjemmel::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "registreringshjemmel",
        schema = "kaka",
        joinColumns = [JoinColumn(name = "saksdata_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = RegistreringshjemmelConverter::class)
    @Column(name = "id")
    var registreringshjemler: Set<Registreringshjemmel>? = null,
    @Column(name = "utfoerende_saksbehandlerident")
    var utfoerendeSaksbehandler: String,
    @Column(name = "tilknyttet_enhet")
    var tilknyttetEnhet: String,
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
        val result = mutableListOf<InvalidProperty>()
        val validationErrors = mutableListOf<InvalidProperty>()
        val sectionList = mutableListOf<ValidationSection>()


        if (sakenGjelder == null) {
            validationErrors.add(
                createMustBeFilledValidationError(SaksdataView::sakenGjelder.name)
            )
        }

        if (sakstype == null) {
            validationErrors.add(
                createMustBeSelectedValidationError(SaksdataView::sakstypeId.name)
            )
        }

        if (ytelse == null) {
            validationErrors.add(
                createMustBeSelectedValidationError(SaksdataView::ytelseId.name)
            )
        }

        if (mottattVedtaksinstans == null) {
            validationErrors.add(
                createMustBeFilledValidationError(SaksdataView::mottattVedtaksinstans.name)
            )
        }

        if (vedtaksinstansEnhet == null) {
            validationErrors.add(
                createMustBeSelectedValidationError(SaksdataView::vedtaksinstansEnhet.name)
            )
        }

        if (mottattKlageinstans == null) {
            validationErrors.add(
                createMustBeFilledValidationError(SaksdataView::mottattKlageinstans.name)
            )
        }

        if (utfall == null) {
            validationErrors.add(
                createMustBeSelectedValidationError(SaksdataView::utfallId.name)
            )
        } else if (utfall != Utfall.TRUKKET) {
            if (registreringshjemler.isNullOrEmpty()) {
                validationErrors.add(
                    createMustBeSelectedValidationError(SaksdataView::hjemmelIdList.name)
                )
            }
        }

        if (validationErrors.isNotEmpty()) {
            sectionList.add(
                ValidationSection(
                    section = "saksdata",
                    properties = validationErrors
                )
            )
        }

        val kvalitetsvurderingValidationErrors = kvalitetsvurdering.getInvalidProperties(null)

        if (kvalitetsvurderingValidationErrors.isNotEmpty()) {
            sectionList.add(
                ValidationSection(
                    section = "kvalitetsvurdering",
                    properties = kvalitetsvurderingValidationErrors
                )
            )
        }

        if (sectionList.isNotEmpty()) {
            throw SectionedValidationErrorWithDetailsException(
                title = "Validation error",
                sections = sectionList
            )
        }
    }

    private fun createMustBeFilledValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Må fylles ut."
        )
    }

    private fun createMustBeSelectedValidationError(variableName: String): InvalidProperty {
        return InvalidProperty(
            field = variableName,
            reason = "Må være valgt."
        )
    }
}