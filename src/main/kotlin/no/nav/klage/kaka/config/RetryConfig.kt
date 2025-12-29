package no.nav.klage.kaka.config

import org.springframework.context.annotation.Configuration
import org.springframework.resilience.annotation.EnableResilientMethods

@Configuration
@EnableResilientMethods
class RetryConfig