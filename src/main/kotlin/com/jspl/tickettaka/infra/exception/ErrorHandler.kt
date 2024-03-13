package com.jspl.tickettaka.infra.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandler {
    private val log = LoggerFactory.getLogger(BaseException::class.java)
    @ExceptionHandler(BaseException::class)
    protected fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        log.error("에러 발생 -> {${e.message}}")
        return ResponseEntity.status(e.code.status)
            .body(ErrorResponse(e.code, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleIllegalStateException(
        e: MethodArgumentNotValidException,
        bindingResult: BindingResult
    ): ResponseEntity<ErrorResponse> {
        val defaultMessage = bindingResult.fieldError?.defaultMessage
        return ResponseEntity(
            ErrorResponse(ApiResponseCode.BAD_REQUEST, defaultMessage),
            HttpStatus.BAD_REQUEST
        )
    }
}