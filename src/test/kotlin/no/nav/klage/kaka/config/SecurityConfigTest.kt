package no.nav.klage.kaka.config

import no.nav.klage.auth.TexasClient
import no.nav.klage.auth.test.AzureSecurityContractTest
import no.nav.klage.kaka.api.KabalKvalitetsvurderingV3Controller
import no.nav.klage.kaka.api.MetadataController
import no.nav.klage.kaka.services.KvalitetsvurderingV3Service
import no.nav.klage.kaka.services.SaksbehandlerService
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.TokenUtil
import org.junit.jupiter.api.Test
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(KabalKvalitetsvurderingV3Controller::class, MetadataController::class)
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

    private val kvalitetsvurderingId = UUID.randomUUID()

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
