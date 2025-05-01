package com.esprit.wonderwise.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class BadWordsFilter {

    private static final String API_URL = "https://www.purgomalum.com/service/containsprofanity?text=";

    // 🛑 Ajoute ici tes propres mots à filtrer
    private static final Set<String> customBadWords = new HashSet<>(Set.of(
            "catas", "null"
    ));

    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        // 1️⃣ Vérification locale
        String lowerText = text.toLowerCase();
        for (String word : customBadWords) {
            if (lowerText.contains(word.toLowerCase())) {
                return true;
            }
        }

        // 2️⃣ Vérification via l'API
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + encodedText))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return Boolean.parseBoolean(response.body().trim());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
