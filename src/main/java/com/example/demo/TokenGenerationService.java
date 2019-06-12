package com.example.demo;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author
 *
 */

@Service("TokenGenerationService")
public class TokenGenerationService {

    private Integer expiryDuration = 900;

    private String privateKeyString;
    private String issuer;
    private PrivateKey privateKey;
    private String cobrandJWTToken;

    private Long cobrandTokenLastRefreshedTime;

    /**
     * @throws Exception
     */
    @PostConstruct
    private void postContstruct() throws Exception {
        this.issuer = System.getenv("JWT_ISSUER");
        this.privateKeyString = System.getenv("JWT_PRIV_KEY");

        if ((this.privateKeyString != null) && (this.issuer != null)) {
            this.privateKey = loadPrivateKey(this.privateKeyString);
            cobrandTokenLastRefreshedTime = System.nanoTime();
            setCobrandJWT();
        } else {
           throw new Exception("System Variable JWT_ISSUER and JWT_PRIV_KEY are not found.");
        }

    }

    /**
     * @param subject - user id
     * @return String - User level JWT
     */
    public  String getUserJWT(final String subject) {
        return Jwts.builder().setSubject(subject).setIssuer(this.issuer).setIssuedAt(new Date())
                .setExpiration(getExpirayDate(expiryDuration)).signWith(SignatureAlgorithm.RS512, this.privateKey)
                .setHeaderParam("typ", "JWT").compact();
    }

    /**
     * @return String - App level JWT
     */
    public String getCobrandJWT() {
        Long durationInSeconds = (System.nanoTime() - this.cobrandTokenLastRefreshedTime) / 1000000000;

        if (durationInSeconds > this.expiryDuration) {
            setCobrandJWT();
        }

        return this.cobrandJWTToken;
    }

    /**
     *
     */
    private void setCobrandJWT() {
        this.cobrandTokenLastRefreshedTime = System.nanoTime();
        this.cobrandJWTToken = Jwts.builder().setIssuer(issuer).setIssuedAt(new Date()).setExpiration(getExpirayDate(expiryDuration))
                .signWith(SignatureAlgorithm.RS512, this.privateKey).setHeaderParam("typ", "JWT")
                .compact();
    }

    /**
     * @param base64DecodedPrivateKey
     * @return PrivateKey - Decoded Private key
     */
    private PrivateKey loadPrivateKey(final String base64DecodedPrivateKey) {
        PrivateKey privKey = null;
        try {
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");

            byte[] pkcs8EncodedBytesPrivate = Base64.getDecoder().decode(base64DecodedPrivateKey);
            privKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedBytesPrivate));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privKey;
    }

    /**
     * @param seconds
     * @return Date - expire date
     */
    private  Date getExpirayDate(final Integer seconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, seconds);

        return calendar.getTime();
    }

}
