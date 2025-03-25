package org.sbuf.util;

import org.sbuf.exception.SbufException;
import org.sbuf.security.jwt.Jwk;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
public class CipherUtils {

    private CipherUtils() {}

    private static final String ALG_RSA = "RSA";
    private static final String ALG_AES = "AES";

    private static final String AES_TRANSFORMER = "AES/ECB/PKCS5Padding";

    private static final String PUBLIC_KEY_BEGIN_PREFIX = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBLIC_KEY_END_PREFIX = "-----END PUBLIC KEY-----";
    private static final String PRIVATE_KEY_BEGIN_PREFIX = "-----BEGIN PRIVATE KEY-----";
    private static final String PRIVATE_KEY_END_PREFIX = "-----END PRIVATE KEY-----";

    private static final Function<byte[], String> encode = bytes -> Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);

    public static synchronized PrivateKey generatePrivateKeyFromString(String privateKeyAsString) {

        try {
            privateKeyAsString = privateKeyAsString
                    .replace(PRIVATE_KEY_BEGIN_PREFIX, "")
                    .replaceAll("\n", "")
                    .replace(PRIVATE_KEY_END_PREFIX, "");

            KeyFactory kf = KeyFactory.getInstance(ALG_RSA);
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyAsString));
            return kf.generatePrivate(keySpecPKCS8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SbufException(e.getMessage());
        }
    }

    public static synchronized PublicKey generatePublicKeyFromString(String publicKeyAsString) {
        try {
            publicKeyAsString = publicKeyAsString
                    .replace(PUBLIC_KEY_BEGIN_PREFIX, "")
                    .replaceAll("\n", "")
                    .replace(PUBLIC_KEY_END_PREFIX, "");

            KeyFactory kf = KeyFactory.getInstance(ALG_RSA);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyAsString));
            return kf.generatePublic(keySpecX509);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new SbufException(e.getMessage());
        }
    }

    public static synchronized PublicKey createPublicKey(Jwk jwk) {
        PublicKey pub = null;
        try {
            RSAPublicKeySpec spec = new RSAPublicKeySpec(
                    new BigInteger(1, Base64.getUrlDecoder().decode(jwk.getModulus())),
                    new BigInteger(1, Base64.getUrlDecoder().decode(jwk.getExponent())));
            KeyFactory factory = KeyFactory.getInstance(ALG_RSA);
            pub = factory.generatePublic(spec);
        } catch (Exception e) {
            throw new SbufException(e.getMessage());
        }
        return pub;
    }
}
