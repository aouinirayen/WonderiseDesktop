package com.esprit.wonderwise.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BadWordsFilter {

    private static final String API_URL = "https://www.purgomalum.com/service/containsprofanity?text=";

    public static boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            // Proper URL encoding for all special characters
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
            return false; // Default to allowing content if API fails
        }
    }
}