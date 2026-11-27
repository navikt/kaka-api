package no.nav.klage.kaka.config

import no.nav.klage.auth.TexasClient
import no.nav.klage.auth.test.AzureSecurityContractTest
import no.nav.klage.kaka.api.KabalKvalitetsvurderingV3Controller
import no.nav.klage.kaka.api.MetadataController
import no.nav.klage.kaka.api.SaksdataController
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.services.KvalitetsvurderingV3Service
import no.nav.klage.kaka.services.SaksbehandlerService
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(KabalKvalitetsvurderingV3Controller::class, MetadataController::class, SaksdataController::class)
@Import(SecurityConfig::class, TokenUtil::class)
@TestPropertySource(properties = ["NAIS_CLUSTER_NAME=test-gcp", "NAIS_NAMESPACE=klage"])
class SecurityConfigTest : AzureSecurityContractTest() {
    @MockitoBean
    lateinit var kvalitetsvurderingV3Service: KvalitetsvurderingV3Service

    @MockitoBean
    lateinit var saksdataService: SaksdataService

    @MockitoBean
    lateinit var saksbehandlerService: SaksbehandlerService

    @MockitoBean
    lateinit var texasClient: TexasClient

    @MockitoBean
    lateinit var rolleMapper: RolleMapper

    private val kvalitetsvurderingId = UUID.randomUUID()

    private val saksdata =
        Saksdata(
            utfoerendeSaksbehandler = "Z123456",
            tilknyttetEnhet = "4295",
            kvalitetsvurderingReference = KvalitetsvurderingReference(id = kvalitetsvurderingId, version = 3),
        )

    override fun protectedRequest() = delete("/kabal/kvalitetsvurderinger/v3/$kvalitetsvurderingId")

    // A valid token passes Spring Security, and the controller then stops callers other than kabal-api.
    override fun assertAccepted(result: ResultActions) {
        result.andExpect(status().isForbidden)
    }

    @Test
    fun `kabal-api can delete kvalitetsvurdering`() {
        mockMvc
            .perform(protectedRequest().withToken(azpName = "test-gcp:klage:kabal-api"))
            .andExpect(status().isOk)

        verify(kvalitetsvurderingV3Service).deleteKvalitetsvurdering(kvalitetsvurderingId)
    }

    @Test
    fun `other apps cannot delete kvalitetsvurdering`() {
        mockMvc
            .perform(protectedRequest().withToken(azpName = "test-gcp:klage:kabal-frontend"))
            .andExpect(status().isForbidden)

        verify(kvalitetsvurderingV3Service, never()).deleteKvalitetsvurdering(kvalitetsvurderingId)
    }

    @Test
    fun `kabal-api can reset kvalitetsvurdering with a machine token`() {
        given(saksdataService.resetKvalitetsvurderingForSystem(saksdata.id)).willReturn(saksdata)
        val token =
            mockAzure.token(
                claims = mapOf("azp_name" to "test-gcp:klage:kabal-api", "idtyp" to "app"),
            )

        mockMvc
            .perform(
                post("/kabal/saksdata/${saksdata.id}/reset-kvalitetsvurdering")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(saksdata.id.toString()))
            .andExpect(jsonPath("$.kvalitetsvurderingId").value(kvalitetsvurderingId.toString()))
            .andExpect(jsonPath("$.kvalitetsvurderingVersion").value(3))

        verify(saksdataService).resetKvalitetsvurderingForSystem(saksdata.id)
    }

    @Test
    fun `other apps cannot reset kvalitetsvurdering in system context`() {
        mockMvc
            .perform(
                post("/kabal/saksdata/${saksdata.id}/reset-kvalitetsvurdering")
                    .withToken(azpName = "test-gcp:klage:kabal-frontend"),
            ).andExpect(status().isForbidden)

        verifyNoInteractions(saksdataService)
    }

    @Test
    fun `both reset endpoints require a token`() {
        mockMvc.perform(post("/saksdata/${saksdata.id}/reset-kvalitetsvurdering")).andExpect(status().isUnauthorized)
        mockMvc.perform(post("/kabal/saksdata/${saksdata.id}/reset-kvalitetsvurdering")).andExpect(status().isUnauthorized)

        verifyNoInteractions(saksdataService)
    }

    @Test
    fun `saksbehandler reset uses NAVident from token`() {
        given(saksdataService.resetKvalitetsvurdering(saksdataId = saksdata.id, innloggetSaksbehandler = "Z123456")).willReturn(saksdata)

        mockMvc
            .perform(
                post("/saksdata/${saksdata.id}/reset-kvalitetsvurdering")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("NAVident" to "Z123456"))}"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.kvalitetsvurderingReference.id").value(kvalitetsvurderingId.toString()))
            .andExpect(jsonPath("$.kvalitetsvurderingReference.version").value(3))

        verify(saksdataService).resetKvalitetsvurdering(saksdataId = saksdata.id, innloggetSaksbehandler = "Z123456")
    }

    @Test
    fun `both reset endpoints return forbidden for finalized saksdata`() {
        given(saksdataService.resetKvalitetsvurdering(saksdataId = saksdata.id, innloggetSaksbehandler = "Z123456"))
            .willThrow(SaksdataFinalizedException("Already finalized"))
        given(saksdataService.resetKvalitetsvurderingForSystem(saksdata.id))
            .willThrow(SaksdataFinalizedException("Already finalized"))

        mockMvc
            .perform(
                post("/saksdata/${saksdata.id}/reset-kvalitetsvurdering")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("NAVident" to "Z123456"))}"),
            ).andExpect(status().isForbidden)
        mockMvc
            .perform(
                post("/kabal/saksdata/${saksdata.id}/reset-kvalitetsvurdering")
                    .withToken(azpName = "test-gcp:klage:kabal-api"),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `userdata requires token`() {
        mockMvc.perform(get("/metadata/userdata")).andExpect(status().isUnauthorized)

        verifyNoInteractions(saksbehandlerService)
    }

    @Test
    fun `userdata uses NAVident from token`() {
        mockMvc
            .perform(
                get("/metadata/userdata")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("NAVident" to "Z123456"))}"),
            ).andExpect(status().isOk)

        verify(saksbehandlerService).getUserData(navIdent = "Z123456")
    }

    private fun org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.withToken(azpName: String) =
        header(HttpHeaders.AUTHORIZATION, "Bearer ${mockAzure.token(claims = mapOf("azp_name" to azpName))}")
}
