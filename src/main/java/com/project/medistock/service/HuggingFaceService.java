package com.project.medistock.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.File;
import java.nio.file.Files;

@Service
public class HuggingFaceService {

    private final String HF_KEY = "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";
    private final String OR_KEY = "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";

    public String getSuggestion(String medicine) {

        if (medicine == null || medicine.trim().isEmpty()) {
            medicine = "medicine";
        }

        try {
            String URL = "https://openrouter.ai/api/v1/chat/completions";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + OR_KEY);
            headers.set("HTTP-Referer", "http://localhost:3000");
            headers.set("X-Title", "MediStock");
            headers.setContentType(MediaType.APPLICATION_JSON);

            String safeMedicine = medicine.replace("\"", "").replace("\\", "");
            String body = "{\"model\":\"openai/gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"Give 5 bullet points about " + safeMedicine + " medicine uses benefits and precautions\"}]}";

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(URL, request, String.class);

            String result = response.getBody();

            System.out.println("OPENROUTER RESULT: " + result);

            if (result == null || result.contains("\"error\"") || result.trim().isEmpty()) {
                return fallback(medicine);
            }

            if (result.contains("\"content\"")) {
                String cleaned = result
                        .replaceAll("[\\s\\S]*\"content\":\"", "")
                        .replaceAll("\",[\\s\\S]*", "")
                        .replaceAll("\\\\n", "\n")
                        .replaceAll("\\\\u003e", ">")
                        .trim();

                if (!cleaned.isEmpty()) {
                    return cleaned;
                }
            }

            return fallback(medicine);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return fallback(medicine);
        }
    }

    public String extractTextFromImage(File file) {

        try {
            String[] models = {
                "https://api-inference.huggingface.co/models/microsoft/trocr-large-printed",
                "https://api-inference.huggingface.co/models/microsoft/trocr-base-handwritten",
                "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base"
            };

            byte[] imageBytes = Files.readAllBytes(file.toPath());

            for (String API_URL : models) {

                try {
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + HF_KEY);
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                    HttpEntity<byte[]> request = new HttpEntity<>(imageBytes, headers);

                    ResponseEntity<String> response =
                            restTemplate.postForEntity(API_URL, request, String.class);

                    String result = response.getBody();

                    System.out.println("IMAGE RESULT: " + result);

                    if (result == null || result.contains("\"error\"") || result.trim().isEmpty()) {
                        continue;
                    }

                    if (result.contains("generated_text")) {
                        String text = result
                                .replaceAll("\\[\\{\"generated_text\":\"", "")
                                .replaceAll("\"\\}\\]", "")
                                .trim();

                        text = text.replaceAll("[^a-zA-Z ]", "");

                        if (!text.isEmpty()) {
                            return text.split(" ")[0].toLowerCase();
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Model failed: " + e.getMessage());
                    continue;
                }
            }

            return "medicine";

        } catch (Exception e) {
            System.out.println("IMAGE ERROR: " + e.getMessage());
            return "medicine";
        }
    }

    private String fallback(String name) {
        return "• " + name + " is commonly used for treating fever, pain or infections\n" +
               "• Helps reduce symptoms and improve health\n" +
               "• Take only as prescribed by doctor\n" +
               "• Avoid overdose and misuse\n" +
               "• Consult doctor if symptoms continue";
    }
}
