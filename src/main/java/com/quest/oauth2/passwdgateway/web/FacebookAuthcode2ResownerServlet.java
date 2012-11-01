package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebResponse;

import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse.OAuth2Error;

public class FacebookAuthcode2ResownerServlet extends AbstractAuthcode2ResownerServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
	    .getLogger(FacebookAuthcode2ResownerServlet.class.getSimpleName());

    @Override
    protected String getAuthCode(String authCodeUrl, String username,
	    String password, String otp) throws OAuth2Exception {

	final WebClient webClient = new WebClient(
		BrowserVersion.INTERNET_EXPLORER_8);
	webClient.setThrowExceptionOnFailingStatusCode(false);
	webClient.setThrowExceptionOnScriptError(false);
	webClient.setCssEnabled(false);
	webClient.setJavaScriptEnabled(false);
	webClient.getCookieManager().setCookiesEnabled(true);

	try {

	    // GET the Oauth2 "code" Url
	    HtmlPage page = webClient.getPage(authCodeUrl);
	    log.info("Fetched: " + authCodeUrl);

	    // Bad request?
	    if (!page.getByXPath("//*[@id=\"error_ok\"]").isEmpty()) {
		OAuth2ErrorResponse error = new OAuth2ErrorResponse(
			OAuth2Error.invalid_request,
			"No additional error details available from Facebook. Check your client_id and redirect_uri. Make sure they match the values in the Facebook Application");
		log.warning(error.toString());
		throw new OAuth2Exception(error);
	    }

	    // Submit the login form
	    HtmlForm form = page.getHtmlElementById("login_form");
	    form.getInputByName("email").setValueAttribute(username);
	    form.getInputByName("pass").setValueAttribute(password);
	    HtmlCheckBoxInput persist = form.getInputByName("persistent");
	    persist.setChecked(false);
	    page = form.getInputByName("login").click();
	    log.info("Submitted login_form for: " + username);

	    // Login fail?
	    if (!page.getByXPath("//*[@id=\"standard_error\"]").isEmpty()) {
		log.info("Invalid username or password for: " + username);
		throw new OAuth2Exception(new OAuth2ErrorResponse(
			OAuth2Error.access_denied,
			"Invalid username or password"));
	    }

	    // Need authorization?
	    if (!page.getByXPath("//*[@name=\"grant_clicked\"]").isEmpty()) {
		log.info("Authorization required");
		page = page.getElementByName("grant_clicked").click();
		log.info("Clicked to authorize");
	    }

	    // Read the authorization code from the title of the callback
	    if(!isRedirectUrl(page.getUrl())) {
		log.severe("Redirected to unexpected Url: "+ page.getUrl());
		throw new OAuth2Exception(new OAuth2ErrorResponse(
			OAuth2Error.server_error, AUTOMATION_FAILURE_MESSAGE));
	    }

	    return parseRedirectUrlCode(page.getUrl());

	} catch (ElementNotFoundException e) {
	    log.severe("Unable to find:" + e.getMessage());
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.server_error, AUTOMATION_FAILURE_MESSAGE));
	} catch (IOException e) {
	    log.severe(e.getMessage());
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.server_error, AUTOMATION_FAILURE_MESSAGE));
	} finally {
	    webClient.closeAllWindows();
	}
    }

    @Override
    protected String getAuthCodeBaseUrl() {
	return "https://www.facebook.com/dialog/oauth";
    }

    @Override
    protected String getAccessTokenUrl() {
	return "https://graph.facebook.com/oauth/access_token";
    }
    
    @Override
    protected Map<String, String> getAccessToken(WebResponse accessCodeResponse) {
	// Instead of following section 5.1 and "application/json" Facebook deceded to use "application/x-form-urlencoded"
	return ServletUtils.ParseQueryString(accessCodeResponse.getContentAsString());	
    }

    private static final String AUTOMATION_FAILURE_MESSAGE = "Unexpected interaction with Facebook authorization code endpoint. The administrator for the OAuth2 Password Grant Gateway should review server logs for more details";
}
