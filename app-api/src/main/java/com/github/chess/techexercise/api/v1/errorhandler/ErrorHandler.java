package com.github.chess.techexercise.api.v1.errorhandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class ErrorHandler
 */
@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Gson gson = new Gson();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ErrorHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processError(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processError(request, response);
	}

	private void processError(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		response.setContentType("application/json");
		response.getWriter().append(createErrorJSONString(getErrorMessageForStatusCode(statusCode), statusCode));
		response.setStatus(statusCode);
	}

	public static String getErrorMessageForStatusCode(int code) {
		switch (code) {
		case 400:
			return "Error 400 - Bad Request";
		case 401:
			return "Error 401 - Unauthorized";
		case 403:
			return "Error 403 - Forbidden";
		case 404:
			return "Error 404 - Not Found";
		case 405:
			return "Error 405 - Method Not Allowed";
		case 409:
			return "Error 409 - Conflict";
		case 415:
			return "Error 415 - Unsupported Media Type";
		case 500:
			return "Error 500 - Internal server error";
		case 504:
			return "Error 504 - Gateway Timeout";
		default:
			return "Unknown error";
		}

	}

	public static String createErrorJSONString(String message, int code) {
		ErrorRecord errorPayload = new ErrorRecord(message, code);
		return gson.toJson(errorPayload, ErrorRecord.class);
	}
}
