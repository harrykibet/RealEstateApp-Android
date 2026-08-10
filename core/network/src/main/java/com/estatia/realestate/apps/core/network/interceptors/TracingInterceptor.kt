package com.estatia.realestate.apps.core.network.interceptors

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that injects a distributed trace ID into outgoing network requests.
 * Uses OpenTelemetry for context propagation.
 */
@Singleton
class TracingInterceptor @Inject constructor() : Interceptor {

    private val tracer: Tracer by lazy {
        GlobalOpenTelemetry.getTracer("estatia-network")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Start a new span for the network request
        val span = tracer.spanBuilder("${request.method} ${request.url.encodedPath}")
            .startSpan()
        
        return try {
            val traceId = span.spanContext.traceId
            
            val newRequest = request.newBuilder()
                .header("X-Trace-Id", traceId)
                .build()
            
            val response = chain.proceed(newRequest)
            
            span.setAttribute("http.status_code", response.code.toLong())
            response
        } catch (e: Exception) {
            span.recordException(e)
            throw e
        } finally {
            span.end()
        }
    }
}
