package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.quest.oauth2.passwdgateway.model.OAuth2ErrorResponse;

public class ServletUtils {
    static public boolean AlwaysReturnOKOnError = false;

    static public void PopulateJsonResponse(HttpServletResponse resp,
	    int statusCode, Object bean) throws IOException {
	Gson gson = new Gson();
	resp.setStatus(statusCode);
	resp.setContentType("application/json");
	resp.getWriter().write(gson.toJson(bean));
    }
    
    static public void PopulateJsonResponse(HttpServletResponse resp,
	    OAuth2ErrorResponse error) throws IOException {
	int httpResponseCode = HttpServletResponse.SC_OK;

	if (AlwaysReturnOKOnError == false) {
	    switch (error.getError()) {
	    case invalid_request:
		httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
		break;
	    case unauthorized_client:
	    case access_denied:
		httpResponseCode = HttpServletResponse.SC_FORBIDDEN;
		break;
	    case unsupported_response_type:
	    case invalid_scope:
		httpResponseCode = HttpServletResponse.SC_BAD_REQUEST;
		break;
	    case server_error:
	    case temporarily_unavailable:
		httpResponseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		break;
	    }
	}

	PopulateJsonResponse(resp, httpResponseCode, error);
    }

    static public Map<String, String> ParseQueryString(String query) {
	// Code below is from BalusC's simple URL parser snippet on
	// StackOverflow
	Map<String, String> params = new HashMap<String, String>();
	try {	   
	    for (String param : query.split("[&]")) {
		String pair[] = param.split("=");
		String key = URLDecoder.decode(pair[0], "UTF-8");
		String value = "";
		if (pair.length > 1) {
		    value = URLDecoder.decode(pair[1], "UTF-8");
		}
		params.put(key, value);
	    }
	} catch (UnsupportedEncodingException e) {
	    // Return empty params if we run into an encoding error
	}

	return params;
    }
    
}
