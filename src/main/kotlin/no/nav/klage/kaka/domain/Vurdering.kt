package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kodeverk.*
import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.MissingTilgangException
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "vurdering", schema = "kaka")
@DynamicUpdate
class Vurdering(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(name = "klager")
    var klager: String? = null,
    @Column(name = "sakstype_id")
    @Convert(converter = SakstypeConverter::class)
    var sakstype: Sakstype? = Sakstype.KLAGE,
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
        joinColumns = [JoinColumn(name = "vurdering_id", referencedColumnName = "id", nullable = false)]
    )
    @Convert(converter = HjemmelConverter::class)
    @Column(name = "id")
    var hjemler: MutableSet<Hjemmel> = mutableSetOf(),
    @Column(name = "utfoerende_saksbehandlerident")
    var utfoerendeSaksbehandler: String? = null,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "kvalitetsvurdering_id", referencedColumnName = "id")
    var kvalitetsvurdering: Kvalitetsvurdering,
    @Column(name = "dato_vurdering_avsluttet_av_saksbehandler")
    var avsluttetAvSaksbehandler: LocalDateTime? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified")
    var modified: LocalDateTime = LocalDateTime.now()

    ) {

    override fun toString(): String {
        return "Behandling(id=$id, " +
                "modified=$modified, " +
                "created=$created)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vurdering

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun verifyAccess(innloggetIdent: String) {
        if (innloggetIdent != utfoerendeSaksbehandler) throw MissingTilgangException("Innlogget bruker har ikke tilgang til kvalitetsvurderingen")
    }
}