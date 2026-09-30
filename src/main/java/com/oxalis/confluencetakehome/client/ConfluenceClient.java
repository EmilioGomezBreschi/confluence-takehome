package com.oxalis.confluencetakehome.client;

import com.oxalis.confluencetakehome.config.AtlassianConfig;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class ConfluenceClient {

    private final OkHttpClient client;
    private final AtlassianConfig config;

    public ConfluenceClient(AtlassianConfig config) {
        this.config = config;
        this.client = new OkHttpClient();
    }

    // Code we are going to be using repeatedly
    public String execute(Request request) throws IOException {

        try (Response response = client.newCall(request).execute()) {

            String body = response.body() != null
                    ? response.body().string()
                    : "";

            if (!response.isSuccessful()) {
                throw new IOException(
                        "Atlassian API error "
                                + response.code()
                                + ": "
                                + body
                );
            }

            return body;
        }
    }

    //Build credentials
    private String getCredentials() {
        return Credentials.basic(
                config.getEmail(),
                config.getApiToken()
        );
    }


    // GET Current User
    public String getCurrentUser() throws IOException {

        String url = config.getBaseUrl() + "/wiki/rest/api/user/current";

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Authorization", getCredentials())
                .get()
                .build();

        return execute(request);
    }

    //POST handler
    public String post(String path, String jsonBody) throws IOException {

        MediaType jsonMediaType = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(jsonBody, jsonMediaType);

        Request request = new Request.Builder()
                .url(config.getBaseUrl() + path)
                .header("Accept", "application/json")
                .header("Authorization", getCredentials())
                .post(body)
                .build();

        return execute(request);
    }

    //GET handler
    public String get(String path) throws IOException {

        Request request = new Request.Builder()
                .url(config.getBaseUrl() + path)
                .header("Accept", "application/json")
                .header("Authorization", getCredentials())
                .get()
                .build();

        return execute(request);
    }

    //DELETE Handler
    public String delete(String path) throws IOException {

        Request request = new Request.Builder()
                .url(config.getBaseUrl() + path)
                .header("Accept", "application/json")
                .header("Authorization", getCredentials())
                .delete()
                .build();

        return execute(request);
    }

    //POST Image handler GPT help
    public String uploadAttachment(String pageId, File file
    ) throws IOException {

        MediaType mediaType = MediaType.get("image/jpeg");

        RequestBody fileBody = RequestBody.create(file, mediaType);

        RequestBody multipartBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                                "file",
                                file.getName(),
                                fileBody
                        )
                        .build();

        Request request =
                new Request.Builder()
                        .url(
                                config.getBaseUrl()
                                        + "/wiki/rest/api/content/"
                                        + pageId
                                        + "/child/attachment"
                        )
                        .header(
                                "Authorization",
                                getCredentials()
                        )
                        .header(
                                "Accept",
                                "application/json"
                        )
                        .header(
                                "X-Atlassian-Token",
                                "nocheck"
                        )
                        .post(multipartBody)
                        .build();

        return execute(request);
    }

    //PUT Handler
    public String put(String path, String jsonBody) throws IOException {

        MediaType jsonMediaType = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(jsonBody, jsonMediaType);

        Request request =
                new Request.Builder()
                        .url(config.getBaseUrl() + path)
                        .header("Accept", "application/json")
                        .header("Authorization", getCredentials())
                        .put(body)
                        .build();

        return execute(request);
    }

    //Empty PUT Handler overload
    public String put(String path) throws IOException {

        RequestBody emptyBody =
                RequestBody.create(new byte[0], null);

        Request request =
                new Request.Builder()
                        .url(config.getBaseUrl() + path)
                        .header("Accept", "application/json")
                        .header("Authorization", getCredentials())
                        .put(emptyBody)
                        .build();

        return execute(request);
    }

}
