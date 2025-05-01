package com.esprit.wonderwise.Service;

import org.json.JSONObject;
import org.vosk.*;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Service de dictée vocale Vosk – totalement offline.
 *
 * Utilisation :
 *
 * <pre>{@code
 * SpeechToTextService stt = new SpeechToTextService(
 *         modelPath,
 *         partial -> Platform.runLater(() -> textArea.setPromptText(partial)),
 *         text    -> Platform.runLater(() -> textArea.appendText(text + " "))
 * );
 *
 * stt.start();   // → commence l’écoute
 * stt.stop();    // → termine et envoie le dernier segment
 * }</pre>
 */
public class SpeechToTextService {

    /* ---------- Constantes ---------- */
    private static final int SAMPLE_RATE = 16_000;

    /* ---------- Callbacks UI ---------- */
    private final Consumer<String> onPartialUI;
    private final Consumer<String> onFinalUI;

    /* ---------- Vosk ---------- */
    private final Recognizer recognizer;

    /* ---------- Audio ---------- */
    private TargetDataLine microphone;
    private Thread worker;
    private volatile boolean running = false;

    /* ---------- Constructeur ---------- */
    public SpeechToTextService(String modelPath,
                               Consumer<String> onPartial,
                               Consumer<String> onFinal) throws IOException {

        this.onPartialUI = onPartial;
        this.onFinalUI   = onFinal;

        LibVosk.setLogLevel(LogLevel.INFO);
        Model model = new Model(modelPath);
        recognizer  = new Recognizer(model, SAMPLE_RATE);
    }

    /* ======================================
     *           Contrôle du service
     * ====================================== */

    /** Démarre la capture micro dans un thread séparé */
    public void start() {
        if (running) return;
        running = true;

        worker = new Thread(this::captureLoop, "STT-Worker");
        worker.setDaemon(true);
        worker.start();
    }

    /** Arrête proprement la capture et attend la fin du thread */
    public void stop() {
        if (!running) return;
        running = false;

        // Réveille read() si bloqué
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }

        try {
            if (worker != null) worker.join();
        } catch (InterruptedException ignored) {}
    }

    /* ======================================
     *              Boucle interne
     * ====================================== */

    private void captureLoop() {
        try {
            AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            microphone = AudioSystem.getTargetDataLine(fmt);
            microphone.open(fmt);
            microphone.start();

            byte[] buffer = new byte[4096];

            while (running) {
                int n = microphone.read(buffer, 0, buffer.length);
                if (n <= 0) continue;

                if (recognizer.acceptWaveForm(buffer, n)) {
                    // Segment terminé → résultat complet
                    String text = extract(recognizer.getResult(), "text");
                    if (!text.isBlank()) onFinalUI.accept(text);
                } else {
                    // Transcription partielle
                    String partial = extract(recognizer.getPartialResult(), "partial");
                    if (!partial.isBlank()) onPartialUI.accept(partial);
                }
            }

            /* Récupère le dernier morceau restant avant de fermer */
            String last = extract(recognizer.getFinalResult(), "text");
            if (!last.isBlank()) onFinalUI.accept(last);

        } catch (LineUnavailableException e) {
            e.printStackTrace(); // à remplacer par un logger
        } finally {
            recognizer.close();
        }
    }

    /* ---------- Helper JSON ---------- */
    private static String extract(String json, String key) {
        return new JSONObject(json).optString(key, "");
    }
}
