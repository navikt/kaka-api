package no.nav.klage.kaka.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration

@EnableJwtTokenValidation(ignore = ["springfox"])
@Configuration
internal class SecurityConfig {

    companion object {
        const val ISSUER_AAD = "aad"
    }
}