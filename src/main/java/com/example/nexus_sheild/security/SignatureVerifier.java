package com.example.nexus_sheild.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureVerifier {
    private static final String ALGORITHM = "HmacSHA256";

    public static boolean verifySignature(String payload, String timestamp, String receivedSignature, String secretKey) {
        try {
            String dataToSign = timestamp + "." + payload;
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hashBytes = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for(byte b: hashBytes){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return MessageDigest.isEqual(hexString.toString().getBytes(), receivedSignature.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
}
