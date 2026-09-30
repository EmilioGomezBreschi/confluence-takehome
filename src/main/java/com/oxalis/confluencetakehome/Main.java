package com.oxalis.confluencetakehome;

import com.fasterxml.jackson.databind.JsonNode;
import com.oxalis.confluencetakehome.client.ConfluenceClient;
import com.oxalis.confluencetakehome.config.AtlassianConfig;
import com.oxalis.confluencetakehome.service.ContentService;
import com.oxalis.confluencetakehome.service.GroupService;
import com.oxalis.confluencetakehome.service.SpaceService;
import com.oxalis.confluencetakehome.service.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        AtlassianConfig config = new AtlassianConfig();

        ConfluenceClient confluenceClient = new ConfluenceClient(config);

        GroupService groupService = new GroupService(confluenceClient);
        UserService userService = new UserService(confluenceClient);
        SpaceService spaceService = new SpaceService(confluenceClient);
        ContentService contentService = new ContentService(confluenceClient);

        // List of Users in my group
        List<String> standardUsers = List.of(
                "Emilio Gomez 1",
                "Emilio Gomez 2",
                "Emilio Gomez 3",
                "Emilio Gomez 4"
        );

        //Group ID
        String groupId = "ededaeb3-88d9-48e0-a9da-87528afa1fcb";

        String user2AccountId = userService.findUserAccountId("Emilio Gomez");

        contentService.restrictReadToUser("557298", user2AccountId);

        System.out.println("Page restricted to Emilio Gomez");
    }
}