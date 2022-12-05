package no.nav.klage.kaka.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.setFieldOnObject
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class KvalitetsvurderingV2Service(
    private val kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository,
    private val saksdataRepository: SaksdataRepository,
) {

    fun getKvalitetsvurdering(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): KvalitetsvurderingV2 {
        val kvalitetsvurdering = kvalitetsvurderingV2Repository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
    }

    fun patchKvalitetsvurdering(kvalitetsvurderingId: UUID, input: JsonNode): KvalitetsvurderingV2 {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)

        input.fields().forEach { (key, value) ->
            setFieldOnObject(obj = kvalitetsvurdering as Any, fieldToChange = key to getValue(value))
        }
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    private fun getKvalitetsvurderingAndVerifyNotFinalized(
        kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV2 {
        val kvalitetsvurdering = kvalitetsvurderingV2Repository.getReferenceById(kvalitetsvurderingId)
            .also {
                val saksdata = saksdataRepository.findOneByKvalitetsvurderingReferenceId(it.id)
                if (saksdata?.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException(
                    "Saksdata er allerede fullført"
                )
            }

        return kvalitetsvurdering
    }

    private fun getValue(node: JsonNode): Any? {
        return when (node) {
            is IntNode -> node.intValue()
            is BooleanNode -> node.booleanValue()
            is TextNode -> node.textValue()
            is NullNode -> null
            is ArrayNode -> node.elements().asSequence().map { Registreringshjemmel.of(getValue(it).toString()) }.toSet()
            else -> error("not supported")
        }
    }
}