package com.jspl.tickettaka.infra.security

import com.jspl.tickettaka.infra.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {


    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity  // 인가
    class SecurityConfig(
        private val jwtAuthenticationFilter: JwtAuthenticationFilter,
        private val authenticationEntrypoint: AuthenticationEntryPoint
    ) {

        @Bean
        fun filterChain(http: HttpSecurity): SecurityFilterChain {
            return http
                .httpBasic { it.disable() }
                .formLogin { it.disable() }
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it.requestMatchers(
                        "/api/members/signup",
                        "/api/members/login",
                        "/api/members/kakaologin",
                        "/api/members/getKakaoAccessToken",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error",
                        "/h2-console/**"
                    ).permitAll()
//                        .requestMatchers("/test/test2/**").hasRole("TempNameConsumer") // 추후 삭제
//                        .anyRequest().permitAll()
                    .anyRequest().authenticated()

                }
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .exceptionHandling{
                    it.authenticationEntryPoint(authenticationEntrypoint)
                }
                .build()
        }

    }


//    @Bean
//    fun filterChain(http: HttpSecurity): SecurityFilterChain {
//        return http
//            .httpBasic { it.disable() } // BasicAuthenticationFilter, DefaultLoginPageGeneratingFilter, DefaultLogoutPageGeneratingFilter 제외
//            .formLogin { it.disable() } // UsernamePassworedAuthenticationFilter, DefaultLoginPageGeneratingFilter, DefaultLogoutPageGeneratingFilter 제외
//            .csrf { it.disable() } // CsrfFilter 제외
//            .build()
//    }

}