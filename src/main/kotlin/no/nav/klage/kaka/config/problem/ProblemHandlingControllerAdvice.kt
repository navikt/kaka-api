package no.nav.klage.kaka.config.problem

import no.nav.klage.kaka.exceptions.KvalitetsvurderingNotFoundException
import no.nav.klage.kaka.exceptions.MissingTilgangException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.AdviceTrait
import org.zalando.problem.spring.web.advice.ProblemHandling

@ControllerAdvice
class ProblemHandlingControllerAdvice : KakaExceptionAdviceTrait, ProblemHandling

interface KakaExceptionAdviceTrait : AdviceTrait {

    @ExceptionHandler
    fun handleKvalitetsvurderingNotFoundException(
        ex: KvalitetsvurderingNotFoundException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.NOT_FOUND, ex, request)

    @ExceptionHandler
    fun handleMissingTilgangException(
        ex: MissingTilgangException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.FORBIDDEN, ex, request)

}

