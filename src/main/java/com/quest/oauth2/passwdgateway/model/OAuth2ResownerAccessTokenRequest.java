package com.quest.oauth2.passwdgateway.model;

public class OAuth2ResownerAccessTokenRequest {

    public String getGrant_type() {
	return grant_type;
    }

    public void setGrant_type(String grant_type) {
	this.grant_type = grant_type;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getOtp() {
	return otp;
    }

    public void setOtp(String otp) {
	this.otp = otp;
    }

    public String getScope() {
	return scope;
    }

    public void setScope(String scope) {
	this.scope = scope;
    }

    private String grant_type;
    private String username;
    private String password;
    private String otp;
    private String scope;

}
