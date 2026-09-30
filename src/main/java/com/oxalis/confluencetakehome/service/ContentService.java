package com.oxalis.confluencetakehome.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oxalis.confluencetakehome.client.ConfluenceClient;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ContentService {

    private final ConfluenceClient client;
    private final ObjectMapper objectMapper;

    public ContentService(ConfluenceClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    //POST to Create a Page
    public String createPage(String spaceId, String title, String htmlContent) throws IOException {

        Map<String, String> body = Map.of(
                "representation", "storage",
                "value", htmlContent
        );

        Map<String, Object> requestBody = Map.of(
                "spaceId", spaceId,
                "status", "current",
                "title", title,
                "body", body
        );

        String json = objectMapper.writeValueAsString(requestBody);

        String response = client.post("/wiki/api/v2/pages", json);

        JsonNode root = objectMapper.readTree(response);

        return root.get("id").asText();
    }

    //Upload an Image
    public String uploadImage(String pageId, File image
    ) throws IOException {
        return client.uploadAttachment(pageId, image);
    }

    //Update Page to place Image GPT help me on this for putting image
    public String embedImage(String pageId, String fileName) throws IOException {

        String response =
                client.get(
                        "/wiki/api/v2/pages/"
                                + pageId
                                + "?body-format=storage"
                );

        JsonNode page = objectMapper.readTree(response);

        String title = page.get("title").asText();

        String currentContent =
                page.get("body")
                        .get("storage")
                        .get("value")
                        .asText();

        int currentVersion =
                page.get("version")
                        .get("number")
                        .asInt();

        String image =
                "<p><ac:image>"
                        + "<ri:attachment ri:filename=\""
                        + fileName
                        + "\" />"
                        + "</ac:image></p>";

        String updatedContent = currentContent + image;

        Map<String, String> body = Map.of(
                "representation", "storage",
                "value", updatedContent
        );

        Map<String, Object> version = Map.of(
                "number", currentVersion + 1,
                "message", "Embed uploaded image"
        );

        Map<String, Object> requestBody = Map.of(
                "id", pageId,
                "status", "current",
                "title", title,
                "body", body,
                "version", version
        );

        String json = objectMapper.writeValueAsString(requestBody);

        return client.put(
                "/wiki/api/v2/pages/" + pageId,
                json
        );
    }

    //PUT Restricted Permision to a user
    public void restrictReadToUser(String pageId, String accountId) throws IOException {

        String encodedAccountId =
                URLEncoder.encode(
                        accountId,
                        StandardCharsets.UTF_8
                );

        client.put(
                "/wiki/rest/api/content/"
                        + pageId
                        + "/restriction/byOperation/read/user"
                        + "?accountId="
                        + encodedAccountId
        );
    }
}