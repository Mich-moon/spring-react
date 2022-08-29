// request payload for the REST api

package com.demo.project2.payload.request;

import javax.validation.constraints.NotBlank;


public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

