package no.nav.klage.kaka.config.problem

import no.nav.klage.kaka.exceptions.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.ThrowableProblem
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
    fun handleSaksdataNotFoundException(
        ex: SaksdataNotFoundException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.NOT_FOUND, ex, request)

    @ExceptionHandler
    fun handleMissingTilgangException(
        ex: MissingTilgangException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.FORBIDDEN, ex, request)

    @ExceptionHandler
    fun handleKvalitetsvurderingFinalizedException(
        ex: KvalitetsvurderingFinalizedException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.FORBIDDEN, ex, request)

    @ExceptionHandler
    fun handleSaksdataFinalizedException(
        ex: SaksdataFinalizedException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.FORBIDDEN, ex, request)

    @ExceptionHandler
    fun handleInvalidSakenGjelderException(
        ex: InvalidSakenGjelderException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.BAD_REQUEST, ex, request)

    @ExceptionHandler
    fun handleSectionedValidationErrorWithDetailsException(
        ex: SectionedValidationErrorWithDetailsException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(ex, createSectionedValidationProblem(ex), request)

    @ExceptionHandler
    fun handleEnhetNotFoundForSaksbehandlerException(
        ex: EnhetNotFoundForSaksbehandlerException,
        request: NativeWebRequest
    ): ResponseEntity<Problem> =
        create(Status.INTERNAL_SERVER_ERROR, ex, request)

    private fun createSectionedValidationProblem(ex: SectionedValidationErrorWithDetailsException): ThrowableProblem {
        return Problem.builder()
            .withStatus(Status.BAD_REQUEST)
            .withTitle(ex.title)
            .with("sections", ex.sections)
            .build()
    }
}