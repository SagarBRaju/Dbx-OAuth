package com.dropbox.oauth.controller;

import com.dropbox.oauth.dto.response.BaseResponse;
import com.dropbox.oauth.service.DbxOAuthService;

import static com.dropbox.oauth.util.AppUrlConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(DBX_MAIN_URL)
public class DbxOAuthController {

    private final DbxOAuthService dbxOAuthService;

    @Autowired
    public DbxOAuthController(DbxOAuthService dbxOAuthService) {
        this.dbxOAuthService = dbxOAuthService;
    }

    @GetMapping(DBX_LOGIN)
    public ResponseEntity<Void> authorize() {
        String url = dbxOAuthService.buildAuthorizationUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping(DBX_CALLBACK)
    public ResponseEntity<BaseResponse> callback(@RequestParam("code") String authCode) {
        String token = dbxOAuthService.exchangeCodeForToken(authCode);
        return ResponseEntity.ok(BaseResponse.success("✅ Dropbox authorization succeeded! Access token", token));
    }

    @GetMapping(DBX_TOKEN_USING_CODE)
    public ResponseEntity<BaseResponse> token(@RequestParam("code") String authCode) {
        String token = dbxOAuthService.exchangeCodeForToken(authCode);
        return ResponseEntity.ok(BaseResponse.success("✅ Dropbox authorization succeeded!",token));
    }

    @GetMapping(DBX_TEAM_INFO)
    public ResponseEntity<BaseResponse> getTeamInfo(String token) {
        Object data = dbxOAuthService.getTeamInfo(token);
        return ResponseEntity.ok(BaseResponse.success("Team info fetched successfully", data));
    }
}
