package com.jspl.tickettaka.infra.exception

data class ModelNotFoundException(val modelName:String ,val id:Long?) :
    RuntimeException("해당 ${modelName}에 ${id}값을 찾을 수 없습니다")
