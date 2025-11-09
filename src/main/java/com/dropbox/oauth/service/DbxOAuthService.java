package com.dropbox.oauth.service;

import com.dropbox.oauth.config.DbxOAuthProperties;
import com.dropbox.oauth.exception.DbxOAuthException;
import com.dropbox.oauth.network.DbxOAuthNetworkClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.dropbox.oauth.util.DbxApiUrls.*;

@Service
public class DbxOAuthService {

    private final DbxOAuthProperties props;
    private final DbxOAuthNetworkClient networkClient;

    public DbxOAuthService(DbxOAuthProperties props, DbxOAuthNetworkClient networkClient) {
        this.props = props;
        this.networkClient = networkClient;
    }

    public String buildAuthorizationUrl() {
        return UriComponentsBuilder
                .fromUriString(DBX_AUTHORIZE)
                .queryParam("client_id", props.getClientId())
                .queryParam("redirect_uri", props.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("token_access_type", "offline")
                .queryParam(props.getScopes())
                .build()
                .toUriString();
    }

    public String exchangeCodeForToken(String authCode) {
        if (authCode == null || authCode.isBlank()) {
            throw new DbxOAuthException("Authorization code is missing", 400);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", authCode);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", props.getRedirectUri());
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());

        ResponseEntity<Map<String, Object>> response =
                networkClient.postForm(DBX_TOKEN, form, headers);

        Map<String, Object> body = response.getBody();
        System.out.println(body);
        if (body.isEmpty() || !body.containsKey("access_token")) {
            throw new DbxOAuthException("Failed to obtain access token from Dropbox", 400);
        }

        return (String) body.get("access_token");
    }


    public Object getTeamInfo(String accessToken) {
        validateAccessToken(accessToken);

        ResponseEntity<Object> response = networkClient.post(DBX_TEAM_INFO, accessToken);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new DbxOAuthException("Dropbox API returned: " + response.getStatusCode(),
                    response.getStatusCode().value());
        }

        return response.getBody();
    }

    private void validateAccessToken(String token) {
        if (token == null || token.isBlank()) {
            throw new DbxOAuthException("Access token missing. Please authorize first at /dropbox/authorize", 400);
        }
    }
}
