package com.quest.oauth2.passwdgateway.model;

import com.google.gson.annotations.Expose;

public class OAuth2ErrorResponse {

    public void setError_description(String error_description) {
	this.error_description = error_description;
    }

    public void setError_uri(String error_uri) {
	this.error_uri = error_uri;
    }
    
    public void setState(String state) {
	this.state = state;
    }

    public enum OAuth2Error {
	invalid_request, unauthorized_client, access_denied, unsupported_response_type, invalid_scope, server_error, temporarily_unavailable
    }

    public OAuth2ErrorResponse(OAuth2Error error) {
	this.error = error;
    }

    public OAuth2ErrorResponse(OAuth2Error error, String description) {
	this(error);
	this.error_description = description;
    }

    public OAuth2Error getError() {
	return error;
    }

    public String getError_description() {
	return error_description;
    }

    public String getError_uri() {
	return error_uri;
    }
    
    public String getState() {
	return state;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("error: ").append(error);
	if(getError_description() != null) {
	    sb.append(", error_description: ").append(getError_description());
	}
	if(getError_uri() != null) {
	    sb.append(", error_uri: ").append(getError_uri() );
	}
	if(getState() != null) {
	    sb.append(", state: ").append(getState());
	}

	return sb.toString();
    }

    @Expose
    private OAuth2Error error;
    @Expose
    private String error_description;
    @Expose
    private String error_uri;
    @Expose
    private String state;

}
