package codes.cy.crmapi.api.security

import codes.cy.crmapi.api.middleware.ExceptionHandlingAdvice
import codes.cy.crmapi.api.middleware.ProblemDetailsPayload
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler

private const val UNAUTHORIZED_DETAIL = "Authentication is required: supply a valid Bearer token."
private const val FORBIDDEN_DETAIL = "The authenticated principal does not have a role permitted to perform this operation."

/**
 * Side-effecting boundary writing an RFC 7807 [ProblemDetailsPayload] directly to the servlet response.
 */
private fun HttpServletResponse.executeWriteProblemDetails(status: HttpStatus, title: String, detail: String, instance: String) {
    this.status = status.value()
    contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
    writer.write(
        Json.encodeToString(
            ProblemDetailsPayload.serializer(),
            ProblemDetailsPayload(title = title, status = status.value(), detail = detail, instance = instance),
        ),
    )
}

/**
 * Renders `401 Unauthorized` (missing / invalid token) as Problem Details, keeping the standard `WWW-Authenticate: Bearer` header.
 */
class ProblemDetailsAuthenticationEntryPoint(
    private val bearerDelegate: BearerTokenAuthenticationEntryPoint = BearerTokenAuthenticationEntryPoint(),
) : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        bearerDelegate.commence(request, response, authException)
        response.executeWriteProblemDetails(
            HttpStatus.UNAUTHORIZED,
            ExceptionHandlingAdvice.UNAUTHORIZED_TITLE,
            UNAUTHORIZED_DETAIL,
            request.requestURI,
        )
    }
}

/**
 * Renders `403 Forbidden` (valid token, insufficient role) as Problem Details.
 */
class ProblemDetailsAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        response.executeWriteProblemDetails(
            HttpStatus.FORBIDDEN,
            ExceptionHandlingAdvice.FORBIDDEN_TITLE,
            FORBIDDEN_DETAIL,
            request.requestURI,
        )
    }
}
