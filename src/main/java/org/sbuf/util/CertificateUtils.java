import jakarta.xml.bind.DatatypeConverter;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;

public class CertificateUtils {

    private CertificateUtils() {}

    public static X509Certificate generateSelfSignedCert(RSAPublicKey publicKey, PrivateKey privateKey) throws CertificateEncodingException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=Enel X Guest Certificate");

        certGen.setSerialNumber(BigInteger.valueOf(TimeUtils.getStartOfYear().toInstant().toEpochMilli()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName);
        certGen.setNotBefore(Date.from(TimeUtils.getStartOfYear().toInstant()));
        certGen.setNotAfter(Date.from(TimeUtils.getEndOfYear().toInstant()));
        certGen.setPublicKey(publicKey);
        certGen.setSignatureAlgorithm("SHA256WithRSA");

        return certGen.generate(privateKey);
    }

    public static String getSha1Thumbprint(X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

    public static String getSha256Thumbprint(X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(cert.getEncoded());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
