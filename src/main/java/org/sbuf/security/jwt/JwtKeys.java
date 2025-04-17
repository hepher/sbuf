package org.sbuf.security.jwt;

public interface JwtKeys {

    interface HeaderClaim {
        String ALGORITHM = "alg";
        String TOKEN_TYPE = "typ";
        String X509_FINGERPRINT = "x5t";
        String KEY_ID = "kid";
    }

    interface PayloadClaim {
        String ISSUER = "iss";
        String SUBJECT = "sub";
        String AUDIENCE = "aud";
        String EXPIRATION_TIME = "exp";
        String NOT_BEFORE = "nbf";
        String ISSUED_AT = "iat";
        String JWT_ID = "jti";
        String TOKEN_HASH_VALUE = "at_hash";
        String AUTHENTICATION_METHODS = "amr";
        String AUTHORIZED_PARTY = "azp";
    }

    interface Parameter {
        String KEY_TYPE = "kty";
        String PUBLIC_KEY_USE = "use"; // values: sig, enc
        String ALGORITHM = "alg";
        String KEY_ID = "kid";
        String RSA_PUBLIC_KEY_MODULUS = "n";
        String RSA_PUBLIC_KEY_EXPONENT = "e";
        String X509_CERT_CHAIN = "x5c";
        String X509_CERT_TUMBLING = "x5t";
        String X509_CERT_SHA1_THUMBPRINT = "x5t#S256";
        String X509_CERT_SHA256_THUMBPRINT = "x5t#S384";
        String X509_CERT_SHA512_THUMBPRINT = "x5t#S512";
        String X509_CERT_CHAIN_SHA1_THUMBPRINTS = "x5c#S256";
        String X509_CERT_CHAIN_SHA256_THUMBPRINTS = "x5c#S384";
        String X509_CERT_CHAIN_SHA512_THUMBPRINTS = "x5c#S512";
        String ECPRIVATE_KEY_CURVE = "crv";
        String ECPRIVATE_KEY_X = "x";
        String ECPRIVATE_KEY_Y = "y";
        
    }
}
