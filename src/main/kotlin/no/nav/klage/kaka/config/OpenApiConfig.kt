package no.nav.klage.kaka.config

import no.nav.klage.kaka.api.SaksdataController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Tag
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class OpenApiConfig {

    @Bean
    fun apiInternal(): Docket {
        return Docket(DocumentationType.OAS_30)
            .select()
            .apis(RequestHandlerSelectors.basePackage(SaksdataController::class.java.packageName))
            .build()
            .pathMapping("/")
            .genericModelSubstitutes(ResponseEntity::class.java)
            .tags(
                Tag("kaka-api:saksdata", "API for KAKA Saksdata"),
                Tag("kaka-api:kvalitet", "API for KAKA kvalitetsvurdering")
            )
    }

}