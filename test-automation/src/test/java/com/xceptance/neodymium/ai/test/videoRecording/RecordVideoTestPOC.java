package com.xceptance.neodymium.ai.test.videoRecording;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.WebDriver;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.xceptance.neodymium.ai.test.videoRecording.utils.GeminiVideoAnalyzer;
import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFolder;
import com.xceptance.neodymium.junit5.NeodymiumTestGenerator;
import com.xceptance.neodymium.util.Neodymium;

@Browser()
@DataFolder("videoRecordings")
public class RecordVideoTestPOC {

    public static String system_context = """
            - On the page the "Buy Here" button will lead to the Product Page and will NOT add the product to the cart!
            """;

    @NeodymiumTestGenerator
    public void testVideoRecordingPoc() throws Exception {
        // Enforce configuration dynamically to bypass property loading typos within
        // Neodymium defaults
        System.setProperty("video.deleteTempRecordings", "false");
        System.setProperty("video.deleteRecordingsAfterAddingToAllureReport", "false");
        System.setProperty("video.enableFilming", "true");
        System.setProperty("video.filmAutomatically", "false");

        // Read script
        String script = Files.readString(Paths.get("src/test/resources/ai-video-recorder.js"));

        // Get configured URL
        String url = Neodymium.configuration().url();
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("neodymium.url is not configured");
        }

        // Open browser
        Selenide.open(url);

        WebDriver driver = Neodymium.getDriver();

        // Inject JS Observer Script IMMEDIATELY so the top bar is visible instantly
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script);

        // Inject 3-second warm-up visual
        String countdownScript = "let d = document.createElement('div');" +
                "d.id='warmup-overlay';" +
                "d.style.position='fixed'; d.style.top='0'; d.style.left='0'; d.style.width='100vw'; d.style.height='100vh';"
                +
                "d.style.backgroundColor='rgba(0,0,0,0.8)'; d.style.color='white'; d.style.fontSize='10vw'; d.style.fontWeight='bold';"
                +
                "d.style.display='flex'; d.style.alignItems='center'; d.style.justifyContent='center'; d.style.zIndex='999999';"
                +
                "document.body.appendChild(d);" +
                "let c = 3;" +
                "d.innerText = 'Recording starts in ' + c;" +
                "let i = setInterval(() => { c--; if(c>0) d.innerText = 'Recording starts in ' + c; else { clearInterval(i); d.remove(); } }, 1000);";
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(countdownScript);

        // Wait exactly 3 seconds for countdown to finish avoiding recording it
        Thread.sleep(3000);

        // START RECORDING MANUALLY
        com.xceptance.neodymium.common.recording.FilmTestExecution.startVideoRecording("manual_poc_video");

        // Start monitor to continuously check if the JS is injected
        Thread monitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    try {
                        Boolean isInitialized = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript("return window.__aiRecorderInitialized === true;");
                        if (isInitialized == null || !isInitialized) {
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script);
                            System.out
                                    .println("AI Observer JS injected/re-injected on page: " + driver.getCurrentUrl());
                        }
                    } catch (Exception e) {
                        // Ignore minor execution issues when navigating
                    }
                    Thread.sleep(100); // Check every 100ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (org.openqa.selenium.NoSuchSessionException e) {
                    System.out.println("Monitor thread detected browser is dead (NoSuchSessionException). Exiting.");
                    break;
                } catch (Exception e) {
                    // Ignore driver exceptions if navigating, but exit if unreachable
                    if (e.getMessage() != null
                            && (e.getMessage().contains("invalid session id") || e.getMessage().contains("unreachable")
                                    || e.getMessage().contains("Session not found"))) {
                        System.out.println("Monitor thread detected browser is dead. Exiting.");
                        break;
                    }
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        System.out.println(
                "Browser opened and recording started! Press the 'Stop & Generate AI Test' button in the browser to stop.");

        int checkIntervalMs = 500;

        while (true) {
            try {
                if (!WebDriverRunner.hasWebDriverStarted() || !monitorThread.isAlive()) {
                    break;
                }
                // Check if browser is still alive
                WebDriverRunner.getWebDriver().getTitle();

                // Check if finish button was clicked inside the browser UI
                Boolean isFinished = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return window.__aiRecorderFinished === true;");
                if (Boolean.TRUE.equals(isFinished)) {
                    System.out.println("Finish button pressed in the browser UI. Stopping recording.");

                    // Retrieve action log
                    try {
                        String actionLogJson = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript("return sessionStorage.getItem('__aiActionLog');");
                        if (actionLogJson != null) {
                            String testNameStr = com.xceptance.neodymium.util.Neodymium.getTestName();
                            String outputPathStr = "src/test/resources/generated.actions.txt";
                            if (testNameStr != null && testNameStr.contains(" :: ")) {
                                String[] parts = testNameStr.split(" :: ");
                                String className = parts[0];
                                String methodName = parts[1];
                                outputPathStr = "src/test/resources/" + className.replace('.', '/') + "/" + methodName
                                        + ".actions.txt";

                                try {
                                    Class<?> testClass = Class.forName(className);
                                    com.xceptance.neodymium.common.testdata.DataFolder[] folders = testClass
                                            .getAnnotationsByType(
                                                    com.xceptance.neodymium.common.testdata.DataFolder.class);
                                    if (folders != null && folders.length > 0) {
                                        outputPathStr = "src/test/resources/" + folders[0].value() + "/" + methodName
                                                + ".actions.txt";
                                    }
                                } catch (Exception ex) {
                                }
                            }

                            java.nio.file.Path logPath = Paths.get(outputPathStr);
                            if (logPath.getParent() != null && !Files.exists(logPath.getParent())) {
                                Files.createDirectories(logPath.getParent());
                            }
                            Files.writeString(logPath, actionLogJson);
                            System.out.println("Written JS action log to: " + logPath.toAbsolutePath());
                        }
                    } catch (Exception e) {
                        System.out.println("Could not retrieve JS action log.");
                    }
                    break;
                }

                Selenide.sleep(checkIntervalMs);
            } catch (org.openqa.selenium.NoSuchSessionException e) {
                System.out.println("Main thread detected browser was closed manually. Ending test gracefully.");
                break;
            } catch (Exception e) {
                if (e.getMessage() != null
                        && (e.getMessage().contains("invalid session id") || e.getMessage().contains("unreachable")
                                || e.getMessage().contains("Session not found"))) {
                    System.out.println("Main thread detected browser was closed. Ending test gracefully.");
                    break;
                }
            }
        }

        System.out.println("Test complete, stopping monitor...");
        monitorThread.interrupt();

        System.out.println("Stopping video recording...");
        com.xceptance.neodymium.common.recording.FilmTestExecution.finishVideoFilming("manual_poc_video", true);

        // Now process video immediately
        File videoFile = new File("target/videos/manual_poc_video.mp4");
        Thread.sleep(1000);
        if (videoFile.exists()) {
            System.out.println("Starting Gemini Video Analysis on: " + videoFile.getAbsolutePath());
            GeminiVideoAnalyzer analyzer = new GeminiVideoAnalyzer();
            analyzer.analyze(videoFile.toPath(), system_context);
        } else {
            System.out.println("Could not find generated video at " + videoFile.getAbsolutePath());
        }
    }
}
