package org.sbuf.security.validation.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sbuf.security.jwt.Jwk;
import org.sbuf.security.jwt.Jwks;
import org.sbuf.security.jwt.impl.StandardJwt;
import org.sbuf.security.validation.SecurityRequestAuthenticator;
import org.sbuf.service.CacheManagerService;
import org.sbuf.service.RestClientService;
import org.sbuf.util.ApplicationContextUtils;
import org.sbuf.util.CipherUtils;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.security.PublicKey;
import java.util.function.Function;

@Slf4j
public abstract class AbstractJwtSecurityRequest implements SecurityRequestAuthenticator {

    private final String CACHE_JWT_KEY = "jwt-keys";
    private final String OPEN_ID_CONFIGURATION = ".well-known/openid-configuration";
    private final String OPEN_ID_JWKS_KEY = "jwks_uri";

    protected boolean checkJwt(String encodedJwt) {
        CacheManagerService cacheManagerService = ApplicationContextUtils.getBean(CacheManagerService.class);

        Function<String, String> openIdConfigFunction = (url) -> {
            if (url.endsWith("/")) {
                return url + OPEN_ID_CONFIGURATION;
            }

            return url + "/" + OPEN_ID_CONFIGURATION;
        };

        StandardJwt jwt;
        try {
            jwt = new StandardJwt(encodedJwt);
        } catch (IOException e) {
            log.error("Malformed or incorrect jwt");
            return false;
        }

        final String issuer = jwt.getPayload().getIssuer();
        if (StringUtils.isBlank(issuer) || !StringUtils.startsWith(issuer, "http")) {
            return false;
        }

        Jwks jwks = cacheManagerService.find(CACHE_JWT_KEY, issuer, Jwks.class)
                .orElseGet(() -> {

                    // rest call to open id configuration
                    JsonNode node = RestClientService.instance()
                            .method(HttpMethod.GET)
                            .url(openIdConfigFunction.apply(issuer))
                            .resultClass(JsonNode.class)
                            .exchange();

                    if (node == null) {
                        log.error("error: missing endpoint to access open id configuration");
                        return null;
                    }

                    JsonNode jwksNode = node.findValue(OPEN_ID_JWKS_KEY);
                    if (jwksNode == null) {
                        log.error("error: missing jwks_uri on open id configuration endpoint");
                        return null;
                    }

                    String jwksUrl = jwksNode.asText();
                    if (StringUtils.isBlank(jwksUrl)) {
                        log.error("error: missing jwks_uri on open id configuration endpoint");
                        return null;
                    }

                    Jwks jwksResult = RestClientService.instance()
                            .method(HttpMethod.GET)
                            .url(jwksUrl)
                            .resultClass(Jwks.class)
                            .exchange();

                    cacheManagerService.put(CACHE_JWT_KEY, issuer, jwksResult, 86400000);

                    return jwksResult;
                });

        if (jwks == null) {
            log.error("error on find jwks");
            return false;
        }

        Jwk jwk = jwks.findJwkByKey(jwt.getHeader().getKeyId());

        if (jwk == null) {
            log.error("error on find jwk");
            return false;
        }

        if (!jwt.checkDateValidation()) {
            log.error("Error on jwt date validation");
            return false;
        }

        PublicKey publicKey = CipherUtils.createPublicKey(jwk);

        if (publicKey == null) {
            log.error("Error on generate public key");
            return false;
        }

        if (!jwt.verify(publicKey)) {
            log.error("Error during uniqueIdJwt verification");
            return false;
        }

        return true;
    }
}
