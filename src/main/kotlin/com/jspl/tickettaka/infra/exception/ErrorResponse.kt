package com.jspl.tickettaka.infra.exception
data class ErrorResponse(val errorMessage: String?) : RuntimeException(errorMessage)
