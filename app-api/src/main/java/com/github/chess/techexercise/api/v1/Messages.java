package com.github.chess.techexercise.api.v1;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.chess.techexercise.api.v1.errorhandler.ErrorHandler;
import com.github.chess.techexercise.api.v1.jsonrecords.MessageRecord;
import com.github.chess.techexercise.api.v1.jsonrecords.MessageRecords;
import com.github.chess.techexercise.api.v1.utils.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Servlet implementation class Messages
 */
@WebServlet("/messages")
public class Messages extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("messages");

	private static final String SELECT_MESSAGES = "SELECT MessageID, Messages.UserID, Email, Message, CreatedOn FROM Messages LEFT JOIN Users ON Messages.UserID = Users.UserID ORDER BY CreatedOn";

	private static final String INSERT_MESSAGE = "insert into Messages(UserID, Message, CreatedOn) values (?,?,UTC_TIMESTAMP())";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Messages() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Message get request received");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setContentType("application/json");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error loading driver", e);
		}
		DriverManager.getDrivers();

		try (Connection connection = DriverManager.getConnection(ApiUtils.getDBEndpoint(), ApiUtils.getDBUsername(),
				ApiUtils.getDBPassword())) {
			if (ApiUtils.authenticate(request, connection)) {
				try (PreparedStatement messageStatement = connection.prepareStatement(SELECT_MESSAGES)) {
					ResultSet rs = messageStatement.executeQuery();
					List<MessageRecord> records = new ArrayList<MessageRecord>();
					MessageRecord newRec;
					while (rs.next()) {
						newRec = new MessageRecord(rs.getInt("MessageID"), rs.getInt("UserID"), rs.getString("Email"),
								rs.getString("Message"), rs.getTimestamp("CreatedOn"));
						records.add(newRec);
					}
					MessageRecords recordsPOJO = new MessageRecords(records);

					Gson gson = new Gson();
					response.getWriter().write(gson.toJson(recordsPOJO, MessageRecords.class));
					response.setStatus(200);
				}
			} else {
				response.getWriter()
						.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(401), 401));
				response.setStatus(401);
			}
		} catch (IllegalStateException | JsonParseException e) {
			String error = ErrorHandler.createErrorJSONString("Bad request - Malformed json", 400);
			response.getWriter().write(error);
			response.setStatus(400);
		} catch (NoSuchAlgorithmException e) {
			logger.log(Level.SEVERE, "Could not find SHA-256 algorithm with MessageDigest", e);
			response.getWriter()
					.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
			response.setStatus(500);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "There was a problem connecting to the database", e);
			response.getWriter()
					.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
			response.setStatus(500);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Message post request received");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setContentType("application/json");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Error loading driver", e);
		}
		DriverManager.getDrivers();

		if (!request.getContentType().equals("application/json")) {
			response.getWriter()
					.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(415), 415));
			response.setStatus(415);
		} else {
			try (Connection connection = DriverManager.getConnection(ApiUtils.getDBEndpoint(), ApiUtils.getDBUsername(),
					ApiUtils.getDBPassword())) {
				JsonObject json = ApiUtils.getJSonParser(request);
				String message = json.get("message").getAsString();

				if (ApiUtils.authenticate(request, connection)) {
					if (message.length() <= 1000) {
						int userID = ApiUtils.getUserID(ApiUtils.getAuthTokenID(request), connection);
						try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_MESSAGE)) {
							insertStatement.setInt(1, userID);
							insertStatement.setString(2, message);

							int rowsChanged = insertStatement.executeUpdate();
							if (rowsChanged == 1) {
								response.setStatus(HttpServletResponse.SC_OK);
							} else {
								logger.log(Level.SEVERE, "Unexpected 0 results inserted into the database");
								response.getWriter().write(ErrorHandler
										.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
								response.setStatus(500);
							}
						}
					} else {
						response.getWriter().write(
								ErrorHandler.createErrorJSONString("Bad request - Message to large (>1000)", 400));
						response.setStatus(400);
					}
				} else {
					response.getWriter().write(
							ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(401), 401));
					response.setStatus(401);
				}
			} catch (IllegalStateException | JsonParseException e) {
				String error = ErrorHandler.createErrorJSONString("Bad request - Malformed json", 400);
				response.getWriter().write(error);
				response.setStatus(400);
			} catch (NoSuchAlgorithmException e) {
				logger.log(Level.SEVERE, "Could not find SHA-256 algorithm with MessageDigest", e);
				response.getWriter()
						.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
				response.setStatus(500);
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "There was a problem connecting to the database", e);
				response.getWriter()
						.write(ErrorHandler.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
				response.setStatus(500);
			}
		}
	}

}
