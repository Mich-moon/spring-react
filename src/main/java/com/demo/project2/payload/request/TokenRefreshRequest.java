// request payload for the REST api

package com.demo.project2.payload.request;

import javax.validation.constraints.NotBlank;


public class TokenRefreshRequest {

    @NotBlank
    private String refreshToken;

    // setter
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // getter
    public String getRefreshToken() {
        return refreshToken;
    }

}
