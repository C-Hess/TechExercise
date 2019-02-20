package com.github.chess.techexercise.api.v1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.chess.techexercise.api.v1.errorhandler.ErrorHandler;
import com.github.chess.techexercise.api.v1.jsonrecords.TokenRecord;
import com.github.chess.techexercise.api.v1.utils.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Servlet implementation class Login
 */
@WebServlet("/auth/tokens")
public class AuthTokens extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("authtokens");

	private static final String SELECT_HASH_SALT = "select UserID, Salt, Hash from Users where Email=?";
	private static final String INSERT_TOKEN = "insert into Tokens(TokenID, UserID, Token) values (?,?,?)";

	/**
	 * Default constructor.
	 */
	public AuthTokens() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "Login token request received");
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
			try {
				JsonObject json = ApiUtils.getJSonParser(request);
				String userEmail = json.get("email").getAsString();
				String userPassword = json.get("password").getAsString();

				try (Connection connection = DriverManager.getConnection(ApiUtils.getDBEndpoint(),
						ApiUtils.getDBUsername(), ApiUtils.getDBPassword());
						PreparedStatement preparedStatement = connection.prepareStatement(SELECT_HASH_SALT)) {

					preparedStatement.setString(1, userEmail);
					ResultSet rs = preparedStatement.executeQuery();

					boolean authenticated = false;
					int userID = 0;
					if (rs.next()) {
						userID = rs.getInt("UserID");
						byte[] salt = rs.getBytes("Salt");
						byte[] dbHash = rs.getBytes("Hash");

						MessageDigest digest = MessageDigest.getInstance("SHA-256");
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						os.write(userPassword.getBytes(StandardCharsets.UTF_8));
						os.write(salt);
						byte[] userHash = digest.digest(os.toByteArray());
						authenticated = Arrays.equals(dbHash, userHash);
					}

					if (authenticated) {
						SecureRandom secureRandom = new SecureRandom();
						String newTokenID = UUID.randomUUID().toString();
						byte[] newRawTokenValue = new byte[256];
						secureRandom.nextBytes(newRawTokenValue);
						String newTokenValue = Base64.getEncoder().encodeToString(newRawTokenValue);
						try (PreparedStatement insertTokenStatement = connection.prepareStatement(INSERT_TOKEN)) {
							insertTokenStatement.setString(1, newTokenID);
							insertTokenStatement.setInt(2, userID);
							insertTokenStatement.setString(3, newTokenValue);
							int rowsChanged = insertTokenStatement.executeUpdate();
							if (rowsChanged == 1) {
								TokenRecord tokenRecord = new TokenRecord(newTokenID, newTokenValue);
								Gson gson = new Gson();
								String jsonResponse = gson.toJson(tokenRecord, TokenRecord.class);

								response.setStatus(HttpServletResponse.SC_OK);
								response.getWriter().append(jsonResponse);
							} else {
								logger.log(Level.SEVERE,
										"There was a problem inserting the new token into the databse");
								response.getWriter().write(ErrorHandler
										.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
								response.setStatus(500);
							}
						}
					} else {
						response.getWriter()
								.write(ErrorHandler.createErrorJSONString("Invalid username/password", 403));
						response.setStatus(403);
					}
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
