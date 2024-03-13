package com.jspl.tickettaka.infra.jwt

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger 설정 클래스입니다.
 */
@Configuration
class SwaggerConfig {

    /**
     * OpenAPI 빈을 생성하여 반환합니다.
     */
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            // 보안 요구사항 추가: Bearer Authentication을 사용
            .addSecurityItem(
                SecurityRequirement().addList("Bearer Authentication")
            )
            // 구성 요소 설정: Bearer Authentication 스키마 추가
            .components(
                Components().addSecuritySchemes(
                    "Bearer Authentication",
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP) // HTTP 인증 사용
                        .scheme("Bearer") // Bearer 스킴
                        .bearerFormat("JWT") // JWT 형식의 토큰
                        .`in`(SecurityScheme.In.HEADER) // 헤더에 포함
                        .name("Authorization") // Authorization 헤더 이름
                )
            )
            // API 정보 설정: 제목, 설명, 버전
            .info(
                Info()
                    .title("Course API")
                    .description("Course API schema")
                    .version("1.0.0")
            )
    }
}