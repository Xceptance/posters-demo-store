package com.xceptance.neodymium.ai.test.videoRecording.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xceptance.neodymium.util.Neodymium;

public class GeminiVideoAnalyzer {

    private final String apiKey;
    private final HttpClient client;

    public GeminiVideoAnalyzer() {
        this.apiKey = Neodymium.aiConfiguration().aiApiKey();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public void analyze(Path videoPath) throws Exception {
        analyze(videoPath, null);
    }

    public void analyze(Path videoPath, String systemContext) throws Exception {
        System.out.println("Uploading video to Gemini File API: " + videoPath.getFileName());
        String fileUri = uploadVideo(videoPath);

        System.out.println("Video uploaded successfully. File URI: " + fileUri);
        System.out.println("Polling for processing completion...");

        String fileId = extractIdFromUri(fileUri);
        pollUntilActive(fileId);

        System.out.println("Video is ACTIVE. Generating content...");
        String rawSteps = generateContent(fileUri, systemContext);

        System.out.println("Received analysis from AI.");

        StringBuilder yaml = new StringBuilder();
        yaml.append("steps: |\n");
        yaml.append("  Open ${neodymium.url}\n");

        java.util.List<String> consolidatedSteps = new java.util.ArrayList<>();
        String ongoingTypeTarget = null;
        String ongoingTypeValue = "";
        java.util.regex.Pattern typePattern = java.util.regex.Pattern
                .compile("(?i)^Type\\s+[\"'](.*?)[\"']\\s+(?:into|in)\\s+(.*)$");

        for (String line : rawSteps.split("\n")) {
            line = line.trim();
            // Remove markdown list bullets if the AI adds them
            if (line.startsWith("- "))
                line = line.substring(2).trim();
            else if (line.startsWith("* "))
                line = line.substring(2).trim();

            // Skip empty lines or AI conversational filler
            if (line.isEmpty() || line.startsWith("```") || line.toLowerCase().startsWith("here are the")
                    || line.toLowerCase().startsWith("analyze this"))
                continue;

            // We explicitly ignore the first initial navigation if the AI still
            // hallucinates it
            if (line.toLowerCase().startsWith("open") || line.toLowerCase().startsWith("navigate")) {
                continue;
            }

            java.util.regex.Matcher m = typePattern.matcher(line);
            if (m.matches()) {
                String val = m.group(1);
                String target = m.group(2).trim();

                if (ongoingTypeTarget != null && isSimilarTarget(ongoingTypeTarget, target)) {
                    ongoingTypeValue += val; // Merge!
                } else {
                    if (ongoingTypeTarget != null) {
                        consolidatedSteps.add(
                                "  Type \"" + ongoingTypeValue.replace("\"", "\\\"") + "\" into " + ongoingTypeTarget);
                    }
                    ongoingTypeTarget = target;
                    ongoingTypeValue = val;
                }
                continue;
            }

            // Not a type action, flush if pending
            if (ongoingTypeTarget != null) {
                consolidatedSteps
                        .add("  Type \"" + ongoingTypeValue.replace("\"", "\\\"") + "\" into " + ongoingTypeTarget);
                ongoingTypeTarget = null;
            }

            consolidatedSteps.add("  " + line);
        }

        // Flush final if still pending
        if (ongoingTypeTarget != null) {
            consolidatedSteps
                    .add("  Type \"" + ongoingTypeValue.replace("\"", "\\\"") + "\" into " + ongoingTypeTarget);
        }

        for (String step : consolidatedSteps) {
            yaml.append(step).append("\n");
        }

        // Dynamically resolve Output Path
        String testNameStr = com.xceptance.neodymium.util.Neodymium.getTestName();
        String outputPathStr = "src/test/resources/generated.yml";
        if (testNameStr != null && testNameStr.contains(" :: ")) {
            String[] parts = testNameStr.split(" :: ");
            String className = parts[0];
            String methodName = parts[1];
            outputPathStr = "src/test/resources/" + className.replace('.', '/') + "/" + methodName + ".yaml";

            try {
                Class<?> testClass = Class.forName(className);
                com.xceptance.neodymium.common.testdata.DataFolder[] folders = testClass
                        .getAnnotationsByType(com.xceptance.neodymium.common.testdata.DataFolder.class);
                if (folders != null && folders.length > 0) {
                    outputPathStr = "src/test/resources/" + folders[0].value() + "/" + methodName + ".yaml";
                }
            } catch (Exception e) {
            }
        }

        Path outputPath = Paths.get(outputPathStr);
        if (outputPath.getParent() != null && !Files.exists(outputPath.getParent())) {
            Files.createDirectories(outputPath.getParent());
        }

        Files.writeString(outputPath, yaml.toString());
        System.out.println("Written step-by-step YAML steps to: " + outputPath.toAbsolutePath());

        if (com.xceptance.neodymium.util.Neodymium.aiConfiguration().aiGenerateV2DiagnosticLogs()) {
            Path rawLogPath = Paths.get(outputPathStr.replace(".yaml", ".raw.txt").replace(".yml", ".raw.txt"));
            Files.writeString(rawLogPath, rawSteps);
            System.out.println("Written raw AI diagnostic log to: " + rawLogPath.toAbsolutePath());
        }
    }

    private String uploadVideo(Path videoPath) throws Exception {
        long fileSize = Files.size(videoPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/upload/v1beta/files?key=" + apiKey))
                .header("X-Goog-Upload-Command", "start, upload, finalize")
                .header("X-Goog-Upload-Header-Content-Length", String.valueOf(fileSize))
                .header("X-Goog-Upload-Header-Content-Type", "video/mp4")
                .header("Content-Type", "video/mp4")
                .POST(HttpRequest.BodyPublishers.ofFile(videoPath))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to upload video: " + response.statusCode() + " " + response.body());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.getAsJsonObject("file").get("uri").getAsString();
    }

    private String extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return parts[parts.length - 1];
    }

    private void pollUntilActive(String fileId) throws Exception {
        while (true) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://generativelanguage.googleapis.com/v1beta/files/" + fileId + "?key=" + apiKey))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Failed to check file status: " + response.statusCode() + " " + response.body());
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String state = json.get("state").getAsString();

            if ("ACTIVE".equals(state)) {
                return;
            } else if ("FAILED".equals(state)) {
                throw new RuntimeException("Video processing failed inside Gemini.");
            }

            // Wait 2s before polling again
            Thread.sleep(2000);
        }
    }

    private String generateContent(String fileUri, String systemContext) throws Exception {
        JsonObject fileData = new JsonObject();
        fileData.addProperty("mimeType", "video/mp4");
        fileData.addProperty("fileUri", fileUri);

        JsonObject filePart = new JsonObject();
        filePart.add("fileData", fileData);

        String prompt = "Analyze this automated browser testing video. The video has an injected HUD specifying 'ACTION: [action]' and the current page 'URL:'. Watch the video carefully, and extract a comprehensive chronological step-by-step list of all actions performed.\n\n"
                        +
                        "CRITICAL: Do NOT output the opening/navigating phase to the first URL! Skip straight to the active user actions (clicking, typing, etc).\n"
                        +
                        "CRITICAL: Ignore all scrolling actions. Do NOT output 'Scroll' as the test framework handles scrolling automatically.\n" +
                        "Return the output EXCLUSIVELY as a simple text list where each step is on a new line.\n" +
                        "The verbs MUST strictly map to the following atomic actions: Click [element], Type \"[text]\" into [element], Clear '[element]', Select '[option]' from '[element]', Wait, Hover over '[element]', Press '[key]'.\n"
                        +
                        "For the [element], use the EXACT full string provided in the HUD action detail, including any appended contextual information like 'in [context]' or '(index [N])'. Do NOT truncate or shorten it. Do NOT append the element type (such as 'button', 'link', 'logo', 'input') to the [element] name. Do NOT use CSS selectors or IDs.\n"
                        +
                        "Do NOT include data bindings, template parameters, assertions, or verifications. Do NOT wrap inside markdown formatting blocks.";

        if (systemContext != null && !systemContext.trim().isEmpty()) {
            prompt += "\n\nSYSTEM CONTEXT (Rules specific to the System Under Test):\n" + systemContext;
        }

        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(filePart);
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject body = new JsonObject();
        body.add("contents", contents);

        String modelName = Neodymium.aiConfiguration().aiModel();
        if (modelName == null || modelName.isEmpty()) {
            modelName = "gemini-2.5-flash"; // fallback
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + modelName
                        + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to generate content: " + response.statusCode() + " " + response.body());
        }

        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
        if (candidates != null && candidates.size() > 0) {
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject contentResp = firstCandidate.getAsJsonObject("content");
            if (contentResp != null) {
                JsonArray partsResp = contentResp.getAsJsonArray("parts");
                if (partsResp != null && partsResp.size() > 0) {
                    return partsResp.get(0).getAsJsonObject().get("text").getAsString().trim();
                }
            }
        }

        throw new RuntimeException("Unexpected response format: " + response.body());
    }

    private boolean isSimilarTarget(String t1, String t2) {
        if (t1 == null || t2 == null)
            return false;
        String s1 = t1.toLowerCase().replaceAll("[^a-z0-9]", "");
        String s2 = t2.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (s1.isEmpty() || s2.isEmpty())
            return t1.equalsIgnoreCase(t2);
        return s1.contains(s2) || s2.contains(s1) || s1.equals(s2);
    }
}
