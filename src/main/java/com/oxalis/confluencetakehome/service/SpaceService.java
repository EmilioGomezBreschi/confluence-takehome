package com.oxalis.confluencetakehome.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxalis.confluencetakehome.client.ConfluenceClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceService {

    //Instead of making a constructor, getters, setters, will do a record that only saves info
    public record SpaceInfo(
            String id,
            String key,
            String name
    ) {}

    private final ConfluenceClient client;
    private final ObjectMapper objectMapper;

    public SpaceService(ConfluenceClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    //POST collaborative Spaces
    public SpaceInfo createSpace(String key, String name) throws IOException {

        Map<String, String> requestBody = Map.of(
                "key", key,
                "name", name
        );

        String json = objectMapper.writeValueAsString(requestBody);

        String response =
                client.post("/wiki/rest/api/space", json);

        JsonNode root = objectMapper.readTree(response);

        return new SpaceInfo(
                root.get("id").asText(),
                root.get("key").asText(),
                root.get("name").asText()
        );
    }

    //GET available permissions
    public String getSpacePermissions(String spaceId) throws IOException {

        return client.get("/wiki/api/v2/spaces/" + spaceId + "/permissions"
        );
    }

    //GET ALL available permissions
    public List<JsonNode> getAllSpacePermissions(String spaceId) throws IOException {

        List<JsonNode> permissions = new ArrayList<>();

        String path = "/wiki/api/v2/spaces/" + spaceId + "/permissions";

        while (path != null) {

            String response = client.get(path);

            JsonNode root = objectMapper.readTree(response);

            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode permission : results) {
                    permissions.add(permission);
                }
            }

            JsonNode next = root.path("_links").get("next");

            if (next == null || next.isNull()) {
                path = null;
            } else {
                path = next.asText();
            }
        }

        return permissions;
    }

    //DELETE Space
    public void deleteSpace(String spaceKey) throws IOException {

        client.delete("/wiki/rest/api/space/" + spaceKey);
    }

    //POST Private Space it's the same as POST space, but API URL says "_private"
    public SpaceInfo createPrivateSpace(
            String key,
            String name
    ) throws IOException {

        Map<String, String> requestBody = Map.of(
                "key", key,
                "name", name
        );

        String json = objectMapper.writeValueAsString(requestBody);

        String response =
                client.post("/wiki/rest/api/space/_private", json);

        JsonNode root = objectMapper.readTree(response);

        return new SpaceInfo(
                root.get("id").asText(),
                root.get("key").asText(),
                root.get("name").asText()
        );
    }

    //GET Space Rol Mode
    public String getSpaceRolMode() throws IOException {

        return client.get("/wiki/api/v2/space-role-mode");
    }

    //GET available Space Roles
    public String getAvailableSpaceRoles(String spaceId) throws IOException {

        return client.get("/wiki/api/v2/space-roles?space-id=" + spaceId);
    }

    //POST Roles to Users per Space
    public String setSpaceRoleAssignment(String spaceId, String principalType, String principalId, String roleId) throws IOException {

        Map<String, String> principal = Map.of(
                "principalType", principalType,
                "principalId", principalId
        );

        Map<String, Object> assignment = Map.of(
                "principal", principal,
                "roleId", roleId
        );

        List<Map<String, Object>> requestBody = List.of(assignment);

        String json = objectMapper.writeValueAsString(requestBody);

        return client.post("/wiki/api/v2/spaces/" + spaceId + "/role-assignments",
                json
        );
    }

    //POST create custom Roles
    public String createSpaceRole(String name, String description, List<String> permissions) throws IOException {

        Map<String, Object> requestBody = Map.of(
                "name", name,
                "description", description,
                "spacePermissions", permissions
        );

        String json = objectMapper.writeValueAsString(requestBody);

        return client.post("/wiki/api/v2/space-roles", json);
    }
}
