package no.nav.klage.kaka.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.setFieldOnObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class KvalitetsvurderingV1Service(
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
    private val saksdataRepository: SaksdataRepository,
) {

    fun getKvalitetsvurdering(
        kvalitetsvurderingId: UUID,
        innloggetSaksbehandler: String
    ): KvalitetsvurderingV1 {
        val kvalitetsvurdering = kvalitetsvurderingV1Repository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
    }

    fun patchKvalitetsvurdering(kvalitetsvurderingId: UUID, input: JsonNode): KvalitetsvurderingV1 {
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyNotFinalized(kvalitetsvurderingId)

        input.fields().forEach { (key, value) ->
            setFieldOnObject(obj = kvalitetsvurdering as Any, fieldToChange = key to getValue(value))
        }
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    private fun getKvalitetsvurderingAndVerifyNotFinalized(
        kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV1 {
        val kvalitetsvurdering = kvalitetsvurderingV1Repository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
            .also {
                val saksdata = saksdataRepository.findOneByKvalitetsvurderingV1Id(it.id)
                if (saksdata?.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException(
                    "Saksdata er allerede fullført"
                )
            }
    }

    private fun getValue(node: JsonNode): Any? {
        return when (node) {
            is IntNode -> node.intValue()
            is BooleanNode -> node.booleanValue()
            is TextNode -> node.textValue()
            is NullNode -> null
            else -> error("not supported")
        }
    }
}