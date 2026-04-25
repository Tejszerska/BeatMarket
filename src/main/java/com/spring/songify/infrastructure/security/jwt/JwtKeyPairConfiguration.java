package com.spring.songify.infrastructure.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Configuration
class JwtKeyPairConfiguration {
    @Bean
    KeyPair keyPair () throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        // Save public key to file
        saveKeyToFile("public_key.pem", keyPair.getPublic().getEncoded(), true);
        // Save private key to file
        saveKeyToFile("private_key.pem", keyPair.getPrivate().getEncoded(), false);

        return keyPair;
    }
    private void saveKeyToFile(String fileName, byte[] keyBytes, boolean isPublicKey) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(Paths.get("src", "main", "resources", fileName).toString())) {
            fos.write(getPemEncoded(keyBytes, isPublicKey));
        }
    }

    private byte[] getPemEncoded(byte[] keyBytes, boolean isPublicKey) {
        String pemHeader = isPublicKey ? "-----BEGIN PUBLIC KEY-----\n" : "-----BEGIN PRIVATE KEY-----\n";
        String pemFooter = isPublicKey ? "\n-----END PUBLIC KEY-----\n" : "\n-----END PRIVATE KEY-----\n";
        String pemEncoded = pemHeader + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(keyBytes) + pemFooter;
        return pemEncoded.getBytes();
    }
}
