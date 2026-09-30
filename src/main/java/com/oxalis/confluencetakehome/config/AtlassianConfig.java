package com.oxalis.confluencetakehome.config;

public class AtlassianConfig {
    private final String baseUrl;
    private final String email;
    private final String apiToken;

    public AtlassianConfig() {
        //Bring environment variables to code
        baseUrl = System.getenv("ATLASSIAN_BASE_URL");
        apiToken = System.getenv("ATLASSIAN_API_TOKEN");
        email = System.getenv("ATLASSIAN_EMAIL");

        //if any variable is not getting a value, tell me
        if (baseUrl == null) {
            throw new IllegalStateException("Missing Base Url");
        }
        if (apiToken == null) {
            throw new IllegalStateException("Missing API Token");
        }
        if (email == null) {
            throw new IllegalStateException("Missing Email");
        }
    }

    //Getters
    public String getBaseUrl() {
        return baseUrl;
    }
    public String getEmail() {
        return email;
    }
    public String getApiToken() {
        return apiToken;
    }


}
