package no.nav.klage.kaka.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import jakarta.transaction.Transactional
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.setFieldOnObject
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class KvalitetsvurderingV1Service(
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
    private val saksdataRepository: SaksdataRepository,
    private val tokenUtil: TokenUtil,
) {

    fun createKvalitetsvurdering(): KvalitetsvurderingV1 {
        return kvalitetsvurderingV1Repository.save(
            KvalitetsvurderingV1()
        )
    }

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
        val kvalitetsvurdering = getKvalitetsvurderingAndVerifyOwnershipAndNotFinalized(kvalitetsvurderingId)

        input.fields().forEach { (key, value) ->
            setFieldOnObject(obj = kvalitetsvurdering as Any, fieldToChange = key to getValue(value))
        }
        kvalitetsvurdering.modified = LocalDateTime.now()
        return kvalitetsvurdering
    }

    fun cleanUpKvalitetsvurdering(
        kvalitetsvurderingId: UUID
    ) {
        val kvalitetsvurdering = kvalitetsvurderingV1Repository.getReferenceById(kvalitetsvurderingId)
        kvalitetsvurdering.cleanup()
        kvalitetsvurdering.modified = LocalDateTime.now()
    }

    fun removeFieldsUnusedInAnke(
        kvalitetsvurderingId: UUID
    ) {
        val kvalitetsvurdering = kvalitetsvurderingV1Repository.getReferenceById(kvalitetsvurderingId)
        kvalitetsvurdering.resetFieldsUnusedInAnke()
        kvalitetsvurdering.modified = LocalDateTime.now()
    }

    private fun getKvalitetsvurderingAndVerifyOwnershipAndNotFinalized(
        kvalitetsvurderingId: UUID
    ): KvalitetsvurderingV1 {
        val kvalitetsvurdering = kvalitetsvurderingV1Repository.findById(kvalitetsvurderingId)
        if (kvalitetsvurdering.isEmpty) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $kvalitetsvurderingId")
        }
        return kvalitetsvurdering.get()
            .also {
                val saksdata = saksdataRepository.findOneByKvalitetsvurderingReferenceId(it.id)
                if (saksdata?.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException(
                    "Saksdata er allerede fullført"
                )
                if (saksdata != null && saksdata.utfoerendeSaksbehandler != tokenUtil.getIdent()) {
                    throw MissingTilgangException("Kvalitetsvurdering tilhører ikke innlogget saksbehandler")
                }
            }
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

    fun deleteKvalitetsvurdering(id: UUID) {
        try {
            kvalitetsvurderingV1Repository.deleteById(id)
        } catch(e: EmptyResultDataAccessException) {
            throw KvalitetsvurderingNotFoundException("Could not find kvalitetsvurdering with id $id")
        }
    }
}