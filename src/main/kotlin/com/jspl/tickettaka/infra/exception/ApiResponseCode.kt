package com.jspl.tickettaka.infra.exception

import org.springframework.http.HttpStatus

enum class ApiResponseCode(status: HttpStatus, message: String) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    DUPLICATE_ENTITY(HttpStatus.BAD_REQUEST, "이미 해당 값이 존재합니다. 다시 입력해주세요."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 값을 찾을 수 없습니다."),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "관리자만 접근 가능합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 서버 연결 오류입니다.")
    ;

    val status: HttpStatus = status
    val message: String = message
}