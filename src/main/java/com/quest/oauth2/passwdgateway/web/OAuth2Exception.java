package com.quest.oauth2.passwdgateway.web;

import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;

public class OAuth2Exception extends Exception {

    private static final long serialVersionUID = 1L;
    private OAuth2ErrorResponse oauth2ErrorResponse;

    public OAuth2Exception(OAuth2ErrorResponse error) {
	super(error.toString());
	this.oauth2ErrorResponse = error;
    }

    public OAuth2ErrorResponse getOauth2ErrorResponse() {
	return oauth2ErrorResponse;
    }
}
