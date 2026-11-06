package codes.cy.crmapi.api.middleware

import jakarta.servlet.http.HttpServletRequest
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Immutable RFC 7807 / RFC 9457 Problem Details envelope serialized via JetBrains `kotlinx.serialization`.
 */
@Serializable
data class ProblemDetailsPayload(
    val type: String = "about:blank",
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String,
)

/**
 * Global exception handling advice translating unhandled exceptions into standardized RFC 7807 / RFC 9457 [ProblemDetailsPayload] responses.
 */
@RestControllerAdvice
class ExceptionHandlingAdvice(
    private val environment: Environment,
) {
    companion object {
        const val BAD_REQUEST_TITLE = "Bad Request"
        const val NOT_FOUND_TITLE = "Not Found"
        const val INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error"
        const val GENERIC_INTERNAL_ERROR_DETAIL = "An unexpected server error occurred."

        private val logger = LoggerFactory.getLogger(ExceptionHandlingAdvice::class.java)

        /**
         * Pure mapping function creating an RFC 7807 [ProblemDetailsPayload] envelope from an exception.
         */
        fun createProblemDetails(
            requestPath: String,
            exception: Exception,
            includeInternalErrorDetails: Boolean,
        ): ProblemDetailsPayload {
            val statusCode = when (exception) {
                is IllegalArgumentException,
                is MethodArgumentTypeMismatchException,
                is MissingServletRequestParameterException,
                is HttpMessageNotReadableException,
                -> HttpStatus.BAD_REQUEST.value()

                is NoSuchElementException,
                is NoResourceFoundException,
                is NoHandlerFoundException,
                -> HttpStatus.NOT_FOUND.value()

                else -> HttpStatus.INTERNAL_SERVER_ERROR.value()
            }

            val title = when (statusCode) {
                HttpStatus.BAD_REQUEST.value() -> BAD_REQUEST_TITLE
                HttpStatus.NOT_FOUND.value() -> NOT_FOUND_TITLE
                else -> INTERNAL_SERVER_ERROR_TITLE
            }

            val detail = if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value() && !includeInternalErrorDetails) {
                GENERIC_INTERNAL_ERROR_DETAIL
            } else {
                exception.message ?: GENERIC_INTERNAL_ERROR_DETAIL
            }

            return ProblemDetailsPayload(
                title = title,
                status = statusCode,
                detail = detail,
                instance = requestPath,
            )
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ProblemDetailsPayload> {
        logger.error("An unhandled exception occurred during request execution: {}", exception.message, exception)
        val isDevelopment = environment.activeProfiles.any {
            it.equals("dev", ignoreCase = true) || it.equals("development", ignoreCase = true)
        }
        val problemDetail = createProblemDetails(request.requestURI, exception, isDevelopment)
        return ResponseEntity
            .status(problemDetail.status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problemDetail)
    }
}
