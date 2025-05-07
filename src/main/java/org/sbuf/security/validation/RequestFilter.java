package org.sbuf.security.validation;

import org.sbuf.config.CachedBodyHttpServletRequest;
import org.sbuf.property.ApplicationProperties;
import org.sbuf.util.ApplicationContextUtils;
import org.sbuf.util.LabelUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class RequestFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(httpServletRequest);
        ContentCachingResponseWrapper responseCacheWrapperObject = new ContentCachingResponseWrapper(httpServletResponse);

        MDC.clear();
        String requestTransactionId = cachedBodyHttpServletRequest.getHeader(LabelUtils.TRANSACTION_ID);
        if (requestTransactionId == null || requestTransactionId.isBlank()) {
            requestTransactionId = UUID.randomUUID().toString();
            cachedBodyHttpServletRequest.putHeader(LabelUtils.TRANSACTION_ID, requestTransactionId);
        }

        Map<String, String> requestHeaderMap = new HashMap<>();

        for (String headerName : Collections.list(cachedBodyHttpServletRequest.getHeaderNames())) {
            requestHeaderMap.put(headerName, cachedBodyHttpServletRequest.getHeader(headerName));
        }

        String requestUrl = cachedBodyHttpServletRequest.getRequestURL() + (StringUtils.isNotBlank(cachedBodyHttpServletRequest.getQueryString()) ? "?" + cachedBodyHttpServletRequest.getQueryString() : "");
        String requestBodyAsString = StreamUtils.copyToString(cachedBodyHttpServletRequest.getInputStream(), StandardCharsets.UTF_8);

        MDC.put(LabelUtils.TRANSACTION_ID, requestTransactionId);
        MDC.put(LabelUtils.SPAN_ID, UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        MDC.put(LabelUtils.MODULE_ID, applicationProperties.getName());
        MDC.put(LabelUtils.CONTAINER, applicationProperties.getContainer());
        MDC.put(LabelUtils.NAMESPACE, applicationProperties.getNamespace());
        MDC.put(LabelUtils.VERSION, applicationProperties.getVersion());

        log.info(LabelUtils.LOG_CLIENT_REQUEST,
                requestUrl,
                cachedBodyHttpServletRequest.getMethod(),
                requestHeaderMap,
                !requestBodyAsString.isBlank() ? ApplicationContextUtils.replaceContextLineSeparator(ApplicationContextUtils.hideSensitiveData(requestBodyAsString)) : "");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Request Filter - doFilter method");

        filterChain.doFilter(cachedBodyHttpServletRequest, responseCacheWrapperObject);

        stopWatch.stop();

        Map<String, String> responseHeaderMap = new HashMap<>();

        for (String headerName : responseCacheWrapperObject.getHeaderNames()) {
            responseHeaderMap.put(headerName, cachedBodyHttpServletRequest.getHeader(headerName));
        }

        String responseBodyAsString = new String(responseCacheWrapperObject.getContentAsByteArray(), StandardCharsets.UTF_8);

        responseCacheWrapperObject.copyBodyToResponse();

        log.info(LabelUtils.LOG_CLIENT_REQUEST_RESPONSE,
                requestUrl,
                cachedBodyHttpServletRequest.getMethod(),
                requestHeaderMap,
                stopWatch.getTotalTimeMillis() + "ms",
                !requestBodyAsString.isBlank() ? ApplicationContextUtils.replaceContextLineSeparator(ApplicationContextUtils.hideSensitiveData(requestBodyAsString)) : "",
                httpServletResponse.getStatus(),
                responseHeaderMap,
                !responseBodyAsString.isBlank() ? ApplicationContextUtils.replaceContextLineSeparator(ApplicationContextUtils.hideSensitiveData(responseBodyAsString)) : "");
    }
}
