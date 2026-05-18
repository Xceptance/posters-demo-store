package com.xceptance.posters;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CheckCsrf {
    public static void main(String[] args) throws Exception {
        CookieManager cookieManager = new CookieManager();
        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        // 1. Get login page to get CSRF token
        HttpRequest getLogin = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/backoffice/login"))
                .GET()
                .build();
        HttpResponse<String> loginRes = client.send(getLogin, HttpResponse.BodyHandlers.ofString());
        
        String csrfToken = null;
        for (String line : loginRes.body().split("\n")) {
            if (line.contains("name=\"_csrf\"")) {
                int start = line.indexOf("value=\"") + 7;
                int end = line.indexOf("\"", start);
                if (start > 6 && end > start) {
                    csrfToken = line.substring(start, end);
                }
            }
        }
        
        if (csrfToken == null) {
            System.out.println("Could not find CSRF token in login page.");
            System.out.println(loginRes.body());
            return;
        }
        
        System.out.println("Login CSRF Token: " + csrfToken);

        // 2. Login
        String formData = "username=admin&password=admin&_csrf=" + csrfToken;
        HttpRequest postLogin = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/backoffice/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        HttpResponse<String> postRes = client.send(postLogin, HttpResponse.BodyHandlers.ofString());
        
        // 3. Get customer list page
        HttpRequest getDashboard = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/backoffice/customers/list"))
                .GET()
                .build();
        HttpResponse<String> dashRes = client.send(getDashboard, HttpResponse.BodyHandlers.ofString());
        
        for (String line : dashRes.body().split("\n")) {
            if (line.contains("<meta name=\"_csrf")) {
                System.out.println("META TAG FOUND: " + line.trim());
            }
        }
    }
}
