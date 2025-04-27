package com.esprit.wonderwise.API;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class Geminiapi {
    private static final String API_KEY = "AIzaSyBLu0HLCgFzXnLH1Wmf6axyZX4w881P1aA";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyBLu0HLCgFzXnLH1Wmf6axyZX4w881P1aA";

    public static String getAIResponse(String userInput) throws Exception {
        // Validation de l'entrée
        if (userInput == null || userInput.trim().isEmpty()) {
            throw new IllegalArgumentException("L'entrée ne peut pas être vide");
        }

        // Construction de la requête
        JSONObject requestBody = buildRequestBody(userInput);
        HttpURLConnection conn = createConnection();

        // Envoi de la requête
        sendRequest(conn, requestBody);

        // Vérification du code de réponse
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("La requête API a échoué avec le code HTTP : " + conn.getResponseCode());
        }

        // Lecture de la réponse
        String response = readResponse(conn);
        return parseAIResponse(response);
    }

    private static JSONObject buildRequestBody(String userInput) {
        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject textObject = new JSONObject();

        textObject.put("text", userInput);
        partsArray.put(textObject);
        contentObject.put("parts", partsArray);
        contentsArray.put(contentObject);
        requestBody.put("contents", contentsArray);

        // Paramètres de sécurité
        JSONArray safetySettings = new JSONArray();
        JSONObject setting = new JSONObject();
        setting.put("category", "HARM_CATEGORY_DANGEROUS_CONTENT");
        setting.put("threshold", "BLOCK_ONLY_HIGH");
        safetySettings.put(setting);
        requestBody.put("safetySettings", safetySettings);

        return requestBody;
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        return conn;
    }

    private static void sendRequest(HttpURLConnection conn, JSONObject requestBody) throws Exception {
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

    private static String parseAIResponse(String jsonResponse) throws Exception {
        JSONObject response = new JSONObject(jsonResponse);

        if (!response.has("candidates") || response.getJSONArray("candidates").isEmpty()) {
            throw new RuntimeException("Aucune réponse valide de l'IA");
        }

        JSONArray candidates = response.getJSONArray("candidates");
        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");

        if (parts.isEmpty()) {
            throw new RuntimeException("Réponse vide de l'IA");
        }

        return parts.getJSONObject(0).getString("text");
    }
}