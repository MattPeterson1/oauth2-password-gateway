package com.quest.oauth2.passwdgateway.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OAuth2CallbackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger
	    .getLogger(OAuth2CallbackServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {

	// TODO: Check cache for state return 404 if we don't have it.
	forward(request, response, "oauth2callback.jsp");
    }

    protected void forward(HttpServletRequest request,
	    HttpServletResponse response, String path) {
	try {
	    RequestDispatcher rd = request.getRequestDispatcher(path);
	    rd.forward(request, response);
	} catch (Throwable tr) {
	    log.severe("Caught Exception: " + tr.getMessage());

	}
    }
}
