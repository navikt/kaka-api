package no.nav.klage.kaka.config.problem

import no.nav.klage.kaka.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemHandlingControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleKvalitetsvurderingNotFoundException(
        ex: KvalitetsvurderingNotFoundException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.NOT_FOUND, ex)

    @ExceptionHandler
    fun handleSaksdataNotFoundException(
        ex: SaksdataNotFoundException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.NOT_FOUND, ex)

    @ExceptionHandler
    fun handleMissingTilgangException(
        ex: MissingTilgangException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.FORBIDDEN, ex)

    @ExceptionHandler
    fun handleKvalitetsvurderingFinalizedException(
        ex: KvalitetsvurderingFinalizedException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.FORBIDDEN, ex)

    @ExceptionHandler
    fun handleSaksdataFinalizedException(
        ex: SaksdataFinalizedException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.FORBIDDEN, ex)

    @ExceptionHandler
    fun handleInvalidSakenGjelderException(
        ex: InvalidSakenGjelderException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.BAD_REQUEST, ex)

    @ExceptionHandler
    fun handleSectionedValidationErrorWithDetailsException(
        ex: SectionedValidationErrorWithDetailsException,
        request: NativeWebRequest
    ): ProblemDetail =
        createSectionedValidationProblem(ex)

    @ExceptionHandler
    fun handleEnhetNotFoundForSaksbehandlerException(
        ex: EnhetNotFoundForSaksbehandlerException,
        request: NativeWebRequest
    ): ProblemDetail =
        create(HttpStatus.INTERNAL_SERVER_ERROR, ex)

    private fun createSectionedValidationProblem(ex: SectionedValidationErrorWithDetailsException): ProblemDetail {
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
            this.title = ex.title
            this.setProperty("sections", ex.sections)
        }
    }

    private fun create(httpStatus: HttpStatus, ex: Exception): ProblemDetail {
        val errorMessage = ex.message ?: "No error message available"
        return ProblemDetail.forStatusAndDetail(httpStatus, errorMessage).apply {
            title = errorMessage
        }
    }
}