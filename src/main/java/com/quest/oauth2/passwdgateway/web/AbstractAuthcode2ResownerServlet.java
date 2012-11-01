package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;
import com.quest.oauth2.passwdgateway.model.OAuth2ResownerAccessTokenRequest;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse.OAuth2Error;

public abstract class AbstractAuthcode2ResownerServlet extends HttpServlet {

    //private static final String EXPECTED_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    //private static final String EXPECTED_CONTENT_TYPE_REGEX = "application/x-www-form-urlencoded\\s*;\\s*charset\\s*=\\s*UTF-8";
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
	    .getLogger(AbstractAuthcode2ResownerServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	ServletUtils
		.PopulateJsonResponse(
			response,
			new OAuth2ErrorResponse(
				OAuth2Error.invalid_request,
				"Resource Owner access token requests should be POSTed.  See OAuth 2.0 spec (section 4.3)"));
	return;
    }

    @Override
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
/*
	if (request.getContentType() == null
		|| !request.getContentType().matches(
			EXPECTED_CONTENT_TYPE_REGEX)) {
	    ServletUtils
		    .PopulateJsonResponse(
			    response,
			    new OAuth2ErrorResponse(
				    OAuth2Error.invalid_request,
				    "Content-Type should be: "
					    + EXPECTED_CONTENT_TYPE
					    + ". You sent: "
					    + request.getContentType()));
	    return;
	}
*/
	try {

	    OAuth2ResownerAccessTokenRequest atr = new OAuth2ResownerAccessTokenRequest();
	    BeanUtils.populate(atr, request.getParameterMap());
	    if (!"password".equals(atr.getGrant_type())) {

		ServletUtils.PopulateJsonResponse(
			response,
			new OAuth2ErrorResponse(
				OAuth2Error.unsupported_response_type,
				"Not supported grant_type: "
					+ atr.getGrant_type()));
		return;
	    }

	    if (atr.getUsername() == null || atr.getUsername().isEmpty()) {
		ServletUtils.PopulateJsonResponse(response,
			new OAuth2ErrorResponse(OAuth2Error.invalid_request,
				"username is required" ));
		return;
	    }

	    if (atr.getPassword() == null || atr.getPassword().isEmpty()) {
		ServletUtils.PopulateJsonResponse(response,
			new OAuth2ErrorResponse(OAuth2Error.invalid_request,
				"password is required" ));
		return;
	    }

	    String authCodeUrl = buildAuthCodeUrl(atr.getScope());
	    String authCode = getAuthCode(authCodeUrl, atr.getUsername(),
		    atr.getPassword(), atr.getOtp());
	    
	    WebResponse accessTokenResponse = getAccessTokenWithAuthCode(authCode);
	    Map<String,String> accessToken = getAccessToken(accessTokenResponse);
	    
	    Gson gson = new GsonBuilder().create(); 
	    String myJsonString = gson.toJson(accessToken);     

	     	    
	    response.setStatus(accessTokenResponse.getStatusCode());
	    response.setContentType("application/json");	    
	    response.getWriter().write(myJsonString);
	    
	} catch (IllegalAccessException e) {
	    log.severe(e.getMessage());
	    ServletUtils.PopulateJsonResponse(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    e.getMessage());
	} catch (InvocationTargetException e) {
	    log.severe(e.getMessage());
	    ServletUtils.PopulateJsonResponse(response,
		    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
		    e.getMessage());
	} catch (OAuth2Exception e) {
	    ServletUtils.PopulateJsonResponse(response,
		    e.getOauth2ErrorResponse());
	}
    }

    protected String getState() {
	SecureRandom random = new SecureRandom();
	return new BigInteger(128, random).toString(32);
    }

    protected String buildAuthCodeUrl(String scope)
	    throws UnsupportedEncodingException {
	StringBuilder sb = new StringBuilder();
	sb.append(getAuthCodeBaseUrl());
	sb.append("?response_type=code");
	sb.append("&client_id=").append(
		URLEncoder.encode(getClientId(), "UTF-8"));
	sb.append("&redirect_uri=").append(
		URLEncoder.encode(getRedirectUri(), "UTF-8"));
	if (scope != null) {
	    sb.append("&scope=").append(URLEncoder.encode(scope, "UTF-8"));
	}
	sb.append("&state=").append(getState());
	return sb.toString();
    }

    protected WebResponse getAccessTokenWithAuthCode(String authCode)
	    throws OAuth2Exception {
	final WebClient webclient = new WebClient();
	webclient.setThrowExceptionOnFailingStatusCode(false);
	webclient.setThrowExceptionOnScriptError(false);
	webclient.setCssEnabled(false);

	try {
	    WebRequest request = new WebRequest(new URL(getAccessTokenUrl()),
		    HttpMethod.POST);

	    request.setAdditionalHeader("Accept", "application/json");
	    request.setAdditionalHeader("Content-Type","application/x-www-form-urlencoded");
	    
	    request.setRequestParameters(new ArrayList<NameValuePair>());	    
	    request.getRequestParameters().add(
		    new NameValuePair("code", authCode));
	    request.getRequestParameters().add(
		    new NameValuePair("client_id", getClientId()));
	    request.getRequestParameters().add(
		    new NameValuePair("client_secret", getClientSecret()));
	    request.getRequestParameters().add(
		    new NameValuePair("redirect_uri", getRedirectUri()));
	    request.getRequestParameters().add(
		    new NameValuePair("grant_type", "authorization_code"));	    
	      
	    return webclient.getPage(request).getWebResponse();
	    
	} catch (MalformedURLException e) {
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.server_error, e.getMessage()));
	} catch (IOException e) {
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.server_error, e.getMessage()));
	}
    }

    protected String parseRedirectUrlCode(URL url) {
	Map<String, String> params = ServletUtils.ParseQueryString(url.getQuery());
	return params.get("code");
    }

    protected OAuth2ErrorResponse parseRedirectUrlError(URL url) {

	Map<String, String> params = ServletUtils.ParseQueryString(url.getQuery());

	// create the OAuth2ErrorResponse
	OAuth2Error error = OAuth2Error.server_error;
	try {
	    if (params.containsKey("error")) {
		error = OAuth2Error.valueOf(params.get("error"));
	    }
	} catch (IllegalArgumentException e) {
	    // unknown "error" parameter. Leave value set to server_error
	}
	OAuth2ErrorResponse errorResponse = new OAuth2ErrorResponse(error);

	if (params.containsKey("error_description")) {
	    errorResponse.setError_description(params.get("error_description"));
	}
	if (params.containsKey("error_uri")) {
	    errorResponse.setError_description(params.get("error_uri"));
	}
	if (params.containsKey("state")) {
	    errorResponse.setState(params.get("state"));
	}
	return errorResponse;
    }

    protected boolean isRedirectUrl(URL url) {
	// According documentation I should be able to call URL.sameFile() here.
	// But, in actual implementation (at least in OpenJDK) the fragment
	// isn't ignored
	return url.toString().startsWith(getRedirectUri());
    }

    protected String getClientId() {
	return getInitParameter("client_id");
    }

    protected String getClientSecret() {
	return getInitParameter("client_secret");
    }

    protected String getRedirectUri() {
	return getInitParameter("redirect_uri");
    }

    protected abstract String getAuthCode(String authCodeUrl, String username,
	    String password, String otp) throws OAuth2Exception; 
    
    protected abstract Map<String, String> getAccessToken(WebResponse accessCodeResponse);
    
    protected abstract String getAuthCodeBaseUrl();

    protected abstract String getAccessTokenUrl();

}
