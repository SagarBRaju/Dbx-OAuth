package com.dropbox.oauth.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbxErrorParser {

    public static String extractDbxError(String message) {
        try {
            int jsonStart = message.indexOf("{");
            if (jsonStart == -1) return message;

            String json = message.substring(jsonStart);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            String error = node.has("error") ? node.get("error").asText() : "unknown_error";
            String description = node.has("error_description")
                    ? node.get("error_description").asText()
                    : "no description";

            return String.format("Dropbox Error: %s - %s", error, description);

        } catch (Exception e) {
            return "Failed to parse Dropbox error: " + e.getMessage();
        }
    }
}
