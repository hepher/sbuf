package org.sbuf.security.jwt;


import org.sbuf.model.dto.AbstractEntity;
import com.enelx.bfw.framework.entity.AbstractEntity;
import com.enelx.bfw.framework.security.jwt.JwtKeys;
import com.enelx.bfw.framework.util.CertificateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
public class Jwk extends AbstractEntity {

    @JsonIgnore
    private X509Certificate x509Cert;

    @JsonProperty(JwtKeys.Parameter.ALGORITHM)
    private String algorithm;

    @JsonProperty(JwtKeys.Parameter.PUBLIC_KEY_USE)
    private String usage;

    @JsonProperty(JwtKeys.Parameter.KEY_TYPE)
    private String keyType;

    @JsonProperty(JwtKeys.Parameter.KEY_ID)
    private String keyId;

    @JsonProperty(JwtKeys.Parameter.RSA_PUBLIC_KEY_MODULUS)
    private String modulus;

    @JsonProperty(JwtKeys.Parameter.RSA_PUBLIC_KEY_EXPONENT)
    private String exponent;

    @JsonProperty(JwtKeys.Parameter.X509_CERT_SHA1_THUMBPRINT)
    private String x509CertTumblingSha1;

    @JsonProperty(JwtKeys.Parameter.X509_CERT_CHAIN)
    private List<String> x509CertList;

    @JsonProperty(JwtKeys.Parameter.X509_CERT_SHA256_THUMBPRINT)
    private String x509CertTumblingSha256;

    @JsonIgnore
    private Function<BigInteger, byte[]> unsignBigIntFunction = bigInt -> {
        // Copied from Apache Commons Codec 1.8

        int bitlen = bigInt.bitLength();

        // round bitlen
        bitlen = ((bitlen + 7) >> 3) << 3;
        final byte[] bigBytes = bigInt.toByteArray();

        if (((bigInt.bitLength() % 8) != 0) && (((bigInt.bitLength() / 8) + 1) == (bitlen / 8))) {
            return bigBytes;
        }

        // set up params for copying everything but sign bit
        int startSrc = 0;
        int len = bigBytes.length;

        // if bigInt is exactly byte-aligned, just skip signbit in copy
        if ((bigInt.bitLength() % 8) == 0) {
            startSrc = 1;
            len--;
        }

        final int startDst = bitlen / 8 - len; // to pad w/ nulls as per spec
        final byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
    };

    public Jwk() {}

    public Jwk(X509Certificate x509Cert) throws CertificateEncodingException, NoSuchAlgorithmException {
        this.x509Cert = x509Cert;
        algorithm = "RS256";
        keyType = x509Cert.getPublicKey().getAlgorithm();
        modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(unsignBigIntFunction.apply(((RSAPublicKey) x509Cert.getPublicKey()).getModulus()));
        exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(unsignBigIntFunction.apply(((RSAPublicKey) x509Cert.getPublicKey()).getPublicExponent()));
        keyId = x509Cert.getSerialNumber().toString();
        x509CertTumblingSha1 = CertificateUtils.getSha256Thumbprint(x509Cert);
        x509CertList = Collections.singletonList(Base64.getEncoder().encodeToString(x509Cert.getEncoded()));
    }

    public Jwk(RSAPublicKey rsaPublicKey) {
        algorithm = "RS256";
        keyType = rsaPublicKey.getAlgorithm();
        modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(unsignBigIntFunction.apply(rsaPublicKey.getModulus()));
        exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(unsignBigIntFunction.apply(rsaPublicKey.getPublicExponent()));
    }
}
