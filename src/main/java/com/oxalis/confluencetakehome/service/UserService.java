package com.oxalis.confluencetakehome.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxalis.confluencetakehome.client.ConfluenceClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class UserService {

    private final ConfluenceClient client;
    private final ObjectMapper objectMapper;

    public UserService(ConfluenceClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    // Receive a List of emails and convert them to JSON and make POST
    public String inviteUsers(List<String> emails) throws IOException {

        Map<String, Object> requestBody = Map.of("emails", emails);

        String json = objectMapper.writeValueAsString(requestBody);

        return client.post("/wiki/api/v2/user/access/invite-by-email", json);
    }

    // Receive a displayName and get its ID
    public String findUserAccountId(String displayName) throws IOException {

        String cql = "user.fullname ~ \"" + displayName + "\"";

        String encodedCql = URLEncoder.encode(cql, StandardCharsets.UTF_8);

        String response = client.get("/wiki/rest/api/search/user?cql=" + encodedCql);

        JsonNode root = objectMapper.readTree(response);

        JsonNode results = root.get("results");

        if (results == null || results.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        return results
                .get(0)
                .get("user")
                .get("accountId")
                .asText();
    }
}
