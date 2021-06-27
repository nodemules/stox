package com.nodemules.stox.config

import com.nodemules.stox.failure.Failure
import io.vavr.control.Either
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class EitherResponseBodyAdvice : ResponseBodyAdvice<Either<Failure, *>> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return AbstractJackson2HttpMessageConverter::class.java.isAssignableFrom(converterType)
    }

    override fun beforeBodyWrite(
        body: Either<Failure, *>?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Either<Failure, *>? = when (body){
        is Either -> body.peekLeft { response.setStatusCode(HttpStatus.valueOf(it.code)) }
        else -> body
    }

}