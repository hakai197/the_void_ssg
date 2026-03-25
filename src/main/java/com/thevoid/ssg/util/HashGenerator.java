package com.thevoid.ssg.util;

import com.thevoid.ssg.model.enums.EntropyMode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class HashGenerator {

    public String generateSiteHash(String siteName) {
        String source = siteName + LocalDateTime.now().toString() + UUID.randomUUID();
        return hashString(source);
    }

    public String regenerateSiteHash(String siteName, EntropyMode mode) {
        String source = siteName + mode.name() + LocalDateTime.now().toString();
        return hashString(source);
    }

    public String generateEntityWard() {
        String[] wards = {
                "Ph'nglui mglw'nafh", "Y'ahn'ghft", "Throdog Uaaah",
                "Yog-Sothoth", "Ia! Ia!", "Fhtagn", "Cthulhu R'lyeh"
        };
        String ward = wards[(int)(Math.random() * wards.length)];
        return ward + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}