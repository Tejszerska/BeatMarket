package com.spring.beatmarket.shared.utils;
import java.text.Normalizer;

public class StringUtils {

    public static String createSlug(String title) {
        if (title == null || title.isBlank()) {
            return "untitled";
        }

        String lowerCase = title.trim().toLowerCase();

        String normalized = Normalizer.normalize(lowerCase, Normalizer.Form.NFD);

        String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return withoutAccents
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }
}
