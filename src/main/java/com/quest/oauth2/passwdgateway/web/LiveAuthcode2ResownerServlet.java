package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.lang.reflect.Type;

import java.util.Map;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebResponse;

import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse.OAuth2Error;

public class LiveAuthcode2ResownerServlet extends
	AbstractAuthcode2ResownerServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
	    .getLogger(LiveAuthcode2ResownerServlet.class.getSimpleName());

    @Override
    protected String getAuthCode(String authCodeUrl, String username,
	    String password, String otp) throws OAuth2Exception {

	final WebClient webClient = new WebClient(
		BrowserVersion.INTERNET_EXPLORER_8);
	webClient.setThrowExceptionOnFailingStatusCode(false);
	webClient.setThrowExceptionOnScriptError(false);
	webClient.setCssEnabled(false);
	webClient.setJavaScriptEnabled(true);
	webClient.getCookieManager().setCookiesEnabled(true);

	try {

	    // GET the Oauth2 "code" Url
	    HtmlPage page = webClient.getPage(authCodeUrl);
	    log.info("Fetched: " + authCodeUrl);

	    // A request using an bad client_id or redirect_uri will result
	    // result in a redirect to a default
	    // Microsoft error page. Error response URL looks like this:
	    if (page.getUrl().getFile().startsWith("/err.srf")) {		
		throw new OAuth2Exception(
			new OAuth2ErrorResponse(
				OAuth2Error.server_error,
				"Administrator should check init-param values for client_id and redirect_uri for servlet: "
					+ this.getClass().getCanonicalName()));
	    }

	    // All other errors will actually be redirects to our redirectURL
	    // Thank you Microsoft for following the specification!
	    if (isRedirectUrl(page.getUrl())) {
		throw new OAuth2Exception(parseRedirectUrlError(page.getUrl()));
	    }

	    // Submit the login form
	    HtmlForm form = page.getElementByName("f1");
	    form.getInputByName("login").setValueAttribute(username);
	    form.getInputByName("passwd").setValueAttribute(password);
	    HtmlCheckBoxInput persist = form.getInputByName("KMSI");
	    persist.setChecked(false);
	    page = form.getInputByName("SI").click();
	    log.info("Submitted login_form for: " + username);

	    // Login fail?
	    // *[@id="idTd_Tile_ErrorMsg_Login"]
	    if (!page.getByXPath("//*[@id=\"idTd_Tile_ErrorMsg_Login\"]")
		    .isEmpty()) {
		log.info("Invalid username or password for: " + username);
		throw new OAuth2Exception(new OAuth2ErrorResponse(
			OAuth2Error.access_denied,
			"Invalid username or password"));
	    }

	    // Need authorization?
	    // *[@id="idBtn_Accept"]
	    if (!page.getByXPath(" //*[@id=\"idBtn_Accept\"]").isEmpty()) {
		log.info("Authorization required");
		page = page.getElementByName("ucaccept").click();
		log.info("Clicked to authorize");
	    }

	    // Read the authorization code from the title of the callback
	    if (!isRedirectUrl(page.getUrl())) {
		log.severe("Redirected to unexpected Url: " + page.getUrl());
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
	return "https://login.live.com/oauth20_authorize.srf";
    }

    @Override
    protected String getAccessTokenUrl() {
	return "https://login.live.com/oauth20_token.srf";
    }
    
    @Override
    protected Map<String, String> getAccessToken(WebResponse accessCodeResponse) {
	// Thank you Microsoft for following specification for access token response	
	Gson gson = new GsonBuilder().create();	
	Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType(); 
	return gson.fromJson(accessCodeResponse.getContentAsString(), typeOfHashMap);	
    } 

    private static final String AUTOMATION_FAILURE_MESSAGE = "Unexpected interaction with Facebook authorization code endpoint. The administrator for the OAuth2 Password Grant Gateway should review server logs for more details";
}
