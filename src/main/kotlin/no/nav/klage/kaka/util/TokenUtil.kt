package no.nav.klage.kaka.util


import no.nav.klage.kaka.config.SecurityConfig
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Service

@Service
class TokenUtil(private val tokenValidationContextHolder: TokenValidationContextHolder) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val securelogger = getSecureLogger()
    }

    fun getIdent(): String =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.get("NAVident")?.toString()
            ?: throw RuntimeException("Ident not found in token")

    fun getName(): String =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.get("name")?.toString()
            ?: throw RuntimeException("name not found in token")

    //Brukes ikke per nå:
    fun isMaskinTilMaskinToken(): Boolean {
        return getClaim("sub") == getClaim("oid")
    }

    fun getCallingApplication(): String {
        //azp_name er på formen <dev-gcp:some-team:some-consumer>
        return getClaim("azp_name").orEmpty()
    }

    private fun getClaim(name: String): String? =
        tokenValidationContextHolder.tokenValidationContext.getJwtToken(SecurityConfig.ISSUER_AAD)
            .jwtTokenClaims?.getStringClaim(name)
}