package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse.OAuth2Error;

public class GoogleAuthcode2ResownerServlet extends AbstractAuthcode2ResownerServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
	    .getLogger(GoogleAuthcode2ResownerServlet.class.getSimpleName());

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
	    // Hit the oauth2 Url
	    HtmlPage page = webClient.getPage(authCodeUrl);
	    log.info("Fetched: " + authCodeUrl);

	    // Bad request?
	    int statusCode = page.getWebResponse().getStatusCode();
	    if (statusCode != HttpServletResponse.SC_OK) {
		if (statusCode == HttpServletResponse.SC_BAD_REQUEST) {
		    HtmlParagraph errorDescription = (HtmlParagraph) page
			    .getElementById("errorDescription");
		    OAuth2ErrorResponse error = new OAuth2ErrorResponse(
			    OAuth2Error.invalid_request,
			    errorDescription.asText());
		    log.info(error.toString());
		    throw new OAuth2Exception(error);
		}
		log.severe("Unexpected HTTP Status Code: " + statusCode);
		throw new OAuth2Exception(
			new OAuth2ErrorResponse(OAuth2Error.invalid_request,
				AUTOMATION_FAILURE_MESSAGE));
	    }

	    // Submit the login form
	    HtmlForm form = page.getHtmlElementById("gaia_loginform");
	    form.getInputByName("Email").setValueAttribute(username);
	    form.getInputByName("Passwd").setValueAttribute(password);
	    HtmlCheckBoxInput persist = form.getInputByName("PersistentCookie");
	    persist.setChecked(false);
	    page = form.getInputByName("signIn").click();
	    log.info("Submitted gaia_loginform for: " + username);

	    // Bad password?
	    if (!page.getByXPath("//*[@id=\"errormsg_0_Passwd\"]/text()")
		    .isEmpty()) {
		log.info("Invalid username or password for: " + username);
		throw new OAuth2Exception(new OAuth2ErrorResponse(
			OAuth2Error.access_denied,
			"Invalid username or password"));
	    }

	    // Need verification code?
	    if (!page.getByXPath("//*[@id=\"verify-form\"]").isEmpty()) {
		log.info("Verification code was required for: " + username);

		if (otp == null) {
		    log.info("Verification code was required but none was provided");
		    throw new OAuth2Exception(
			    new OAuth2ErrorResponse(
				    OAuth2Error.invalid_request,
				    "Google SMS or OTP verification code is required"));
		}

		// Submit verification code.
		form = page.getHtmlElementById("verify-form");
		form.getInputByName("smsUserPin").setValueAttribute(otp);
		persist = form.getInputByName("PersistentCookie");
		persist.setChecked(false);
		page = form.getInputByName("smsVerifyPin").click();
		log.info("Submitted Verification code: " + otp);

		// Verification code valid?
		if (!page.getByXPath("//*[@id=\"error\"]").isEmpty()) {
		    log.info("Verification code was incorrect: " + otp);
		    throw new OAuth2Exception(new OAuth2ErrorResponse(
			    OAuth2Error.access_denied,
			    "Verification code is invalid"));
		}
		log.info("Verification code valid: " + otp);
	    }

	    if (!page.getByXPath("//*[@id=\"nojssubmit\"]").isEmpty()) {
		HtmlSubmitInput nojssubmit = (HtmlSubmitInput) page
			.getElementById("nojssubmit");
		page = nojssubmit.click();
		log.info("Clicked nojssubmit button");
	    }

	    if (page.getTitleText().equals("Request for Permission")) {
		log.info("Request for permission required");
		HtmlButton smsVerifyPinButton = (HtmlButton) page
			.getElementById("submit_approve_access");
		page = smsVerifyPinButton.click();
		log.info("Clicked submit_approve_access button");
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
	} catch (SocketTimeoutException e) {
	    log.severe(e.getMessage());
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.temporarily_unavailable,
		    "Request to Google authentication service timed out"));
	} catch (IOException e) {
	    log.severe(e.getMessage());
	    throw new OAuth2Exception(new OAuth2ErrorResponse(
		    OAuth2Error.server_error, e.getMessage()));
	} finally {
	    webClient.closeAllWindows();
	}
    }

    @Override
    protected String getAuthCodeBaseUrl() {
	return "https://accounts.google.com/o/oauth2/auth";
    }

    @Override
    protected String getAccessTokenUrl() {
	return "https://accounts.google.com/o/oauth2/token";
    }
    
    @Override
    protected Map<String, String> getAccessToken(WebResponse accessCodeResponse) {
	// Thank you Google for following specification for access token response	
	Gson gson = new GsonBuilder().create();	
	Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType(); 
	return gson.fromJson(accessCodeResponse.getContentAsString(), typeOfHashMap);	
    } 

    private static final String AUTOMATION_FAILURE_MESSAGE = "Unexpected interaction with Google authorization code endpoint. The administrator for the OAuth2 Password Grant Gateway should review server logs for more details";

       
}