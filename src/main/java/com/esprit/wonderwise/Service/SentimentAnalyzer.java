package com.esprit.wonderwise.Service;

public class SentimentAnalyzer {

    // Méthode pour analyser le sentiment
    public static String analyzeSentiment(String text) {
        text = text.toLowerCase();
        if (text.contains("merci") || text.contains("parfait") || text.contains("bien") || text.contains("satisfait")) {
            return "positif";
        } else if (text.contains("problème") || text.contains("horrible") || text.contains("mauvais") || text.contains("insatisfait")) {
            return "négatif";
        } else {
            return "neutre";
        }
    }

    // Méthode pour obtenir l'emoji en fonction du sentiment
    public static String getEmoji(String sentiment) {
        return switch (sentiment) {
            case "positif" -> "🥰";
            case "négatif" -> "😡";
            case "neutre" -> "😐";
            default -> "";
        };
    }

    // Méthode pour obtenir l'étiquette du sentiment
    public static String getSentimentLabel(String sentiment) {
        return switch (sentiment) {
            case "positif" -> " Positif";
            case "négatif" -> "Négatif";
            case "neutre" -> "Neutre";
            default -> "";
        };
    }

    // Méthode pour générer le sentiment avec l'emoji sans balise HTML
    public static String generateSentimentHTML(String sentiment) {
        String sentimentLabel = getSentimentLabel(sentiment);
        String emoji = getEmoji(sentiment);

        // Retourne simplement l'étiquette de sentiment et l'emoji
        return sentimentLabel + " " + emoji;
    }
}
