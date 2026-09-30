package com.oxalis.confluencetakehome.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxalis.confluencetakehome.client.ConfluenceClient;

import java.io.IOException;
import java.util.Map;

public class GroupService {

    private final ConfluenceClient client;
    private final ObjectMapper objectMapper;

    public GroupService(ConfluenceClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    // POST a Group
    public String createGroup(String groupName) throws IOException {

        Map<String, String> requestBody = Map.of("name", groupName);

        String json = objectMapper.writeValueAsString(requestBody);

        String response = client.post("/wiki/rest/api/group", json);

        JsonNode root = objectMapper.readTree(response);

        return root.get("id").asText();
    }

    // POST Users to that group I created
    public void addUserToGroup(String groupId, String accountId) throws IOException {

        Map<String, String> requestBody = Map.of("accountId", accountId);

        String json = objectMapper.writeValueAsString(requestBody);

        client.post("/wiki/rest/api/group/userByGroupId?groupId=" + groupId, json);
    }

    //GET Members from a group
    public String getGroupMembers(String groupId)
            throws IOException {

        return client.get(
                "/wiki/rest/api/group/"
                        + groupId
                        + "/membersByGroupId"
        );
    }
}
