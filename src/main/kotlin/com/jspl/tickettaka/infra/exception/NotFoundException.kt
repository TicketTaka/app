package com.jspl.tickettaka.infra.exception

class NotFoundException(override var message: String): BaseException() {
    override var code: ApiResponseCode = ApiResponseCode.NOT_FOUND
}
