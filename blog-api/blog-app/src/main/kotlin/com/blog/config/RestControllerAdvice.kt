package com.blog.config

import mu.KotlinLogging
import org.springframework.beans.TypeMismatchException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice(annotations = [RestController::class])
@Order(Ordered.HIGHEST_PRECEDENCE)
class RestControllerAdvice {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(Exception::class)
    suspend fun handleException(
        ex: Exception,
    ): ResponseEntity<ExceptionDto> {

        log.error("처리 중 에러 발생 ${ex.javaClass.simpleName} - ${ex.message}", ex)

        val dto = ExceptionDto()
        return ResponseEntity.status(dto.stateCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
    }

    @ExceptionHandler(TypeMismatchException::class)
    suspend fun handleTypeMismatch(
        ex: TypeMismatchException,
    ): ResponseEntity<ExceptionDto> {

        log.warn("처리 중 에러 발생 ${ex.javaClass.simpleName} - ${ex.message}", ex)

        val dto = ExceptionDto()

        dto.message = "${ex.value} 값을 확인해주세요"
        dto.stateCode = HttpStatus.BAD_REQUEST.value()

        return ResponseEntity.status(dto.stateCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun failValidation(
        ex: WebExchangeBindException,
    ): ResponseEntity<ExceptionDto> {

        log.warn("validation 실패 발생 ${ex.javaClass.simpleName} - ${ex.message}", ex)

        val dto = ExceptionDto()
        val message = ex.bindingResult.fieldError?.defaultMessage ?: "요청이 잘못 되었습니다."

        dto.message = message
        dto.stateCode = HttpStatus.BAD_REQUEST.value()

        return ResponseEntity.status(dto.stateCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto)
    }
}

data class ExceptionDto(
    var message: String = "알 수 없는 장애가 발생하였습니다. 잠시 후, 다시 시도해주세요",
    var stateCode: Int = 500,
)