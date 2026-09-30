package com.oxalis.confluencetakehome;

import com.oxalis.confluencetakehome.client.ConfluenceClient;
import com.oxalis.confluencetakehome.config.AtlassianConfig;
import com.oxalis.confluencetakehome.service.SpaceService;

public class Main {

    public static void main(String[] args) {

        try {
            AtlassianConfig config = new AtlassianConfig();

            ConfluenceClient confluenceClient =
                    new ConfluenceClient(config);

            SpaceService spaceService =
                    new SpaceService(confluenceClient);

            System.out.println("Confluence connection successful.");
            System.out.println();

            System.out.println("Current user:");
            System.out.println(
                    confluenceClient.getCurrentUser()
            );

            System.out.println();

            System.out.println("Space access mode:");
            System.out.println(
                    spaceService.getSpaceRoleMode()
            );

        } catch (Exception e) {
            System.err.println(
                    "Application error: " + e.getMessage()
            );
            System.exit(1);
        }
    }
}