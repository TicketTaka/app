package com.jspl.tickettaka.infra.exception

abstract class BaseException: RuntimeException() {

    open lateinit var code: ApiResponseCode
    override lateinit var message: String

}