package com.dropbox.oauth.network;

import com.dropbox.oauth.exception.DbxOAuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DbxOAuthNetworkClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DbxOAuthNetworkClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ResponseEntity<Object> post(String url, String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>("null", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            Object json = objectMapper.readValue(response.getBody(), Object.class);

            return ResponseEntity.status(response.getStatusCode()).body(json);

        } catch (HttpStatusCodeException ex) {
            handleHttpException(ex);
            return null;
        } catch (Exception e) {
            throw new DbxOAuthException(
                    "Network error while calling Dropbox API: " + e.getMessage(), 500);
        }
    }

    public ResponseEntity<Map<String, Object>> postForm(
            String url,
            MultiValueMap<String, String> formParams,
            HttpHeaders headers
    ) {
        try {
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, headers);
            return restTemplate.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<>() {
                    });
        } catch (HttpStatusCodeException ex) {
            handleHttpException(ex);
            return null;
        } catch (Exception e) {
            throw new DbxOAuthException(
                    "Network error while exchanging token: " + e.getMessage(), 500);
        }
    }

    private void handleHttpException(HttpStatusCodeException ex) {
        int status = ex.getStatusCode().value();
        String body = ex.getResponseBodyAsString();

        String message = String.format("Dropbox API error (%d): %s", status, body);
        throw new DbxOAuthException(message, status);
    }
}
