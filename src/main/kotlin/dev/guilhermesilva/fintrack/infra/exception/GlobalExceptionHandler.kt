package dev.guilhermesilva.fintrack.infra.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        exception: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = exception.status,
            message = exception.message,
            path = request.requestURI
        )

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        exception: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.valueOf(exception.statusCode.value())

        return buildResponse(
            status = status,
            message = exception.reason ?: status.reasonPhrase,
            path = request.requestURI
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val details = exception.bindingResult
            .fieldErrors
            .map {
                FieldErrorResponse(
                    field = it.field,
                    message = it.defaultMessage ?: "Invalid value"
                )
            }

        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            path = request.requestURI,
            details = details
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        exception: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val details = exception.constraintViolations
            .map {
                FieldErrorResponse(
                    field = it.propertyPath.toString(),
                    message = it.message
                )
            }

        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            path = request.requestURI,
            details = details
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        exception: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid value for parameter '${exception.name}'",
            path = request.requestURI
        )

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        exception: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Missing required parameter '${exception.parameterName}'",
            path = request.requestURI
        )

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(
        exception: BadCredentialsException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Invalid email or password",
            path = request.requestURI
        )

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        exception: UsernameNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Invalid token or user not found",
            path = request.requestURI
        )

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        exception: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.CONFLICT,
            message = "Data integrity violation",
            path = request.requestURI
        )

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        exception: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> =
        buildResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Unexpected internal server error",
            path = request.requestURI
        )

    private fun buildResponse(
        status: HttpStatus,
        message: String,
        path: String,
        details: List<FieldErrorResponse> = emptyList()
    ): ResponseEntity<ApiError> =
        ResponseEntity
            .status(status)
            .body(
                ApiError(
                    status = status.value(),
                    error = status.reasonPhrase,
                    message = message,
                    path = path,
                    details = details
                )
            )
}