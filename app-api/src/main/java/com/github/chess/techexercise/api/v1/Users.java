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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.github.chess.techexercise.api.v1.errorhandler.ErrorHandler;
import com.github.chess.techexercise.api.v1.utils.ApiUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Servlet implementation class Users
 */
@WebServlet("/users")
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger("users");

	private static final String SELECT_EXISTANCE_OF_USER = "select Email from Users where Email=?";
	private static final String INSERT_USER = "insert into Users(Email, Salt, Hash) values (?, ?, ?)";

	/**
	 * Default constructor.
	 */
	public Users() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.log(Level.INFO, "Sign-up request received");
		//response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
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
				String userEmail = json.get("email").getAsString();
				String userPassword = json.get("password").getAsString();

				try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EXISTANCE_OF_USER)) {

					preparedStatement.setString(1, userEmail);
					ResultSet rs = preparedStatement.executeQuery();
					boolean userAlreadyExists = rs.next();

					if (!userAlreadyExists) {
						SecureRandom secureRandom = new SecureRandom();
						byte[] salt = new byte[8];
						secureRandom.nextBytes(salt);
						MessageDigest digest = MessageDigest.getInstance("SHA-256");
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						os.write(userPassword.getBytes(StandardCharsets.UTF_8));
						os.write(salt);
						byte[] hash = digest.digest(os.toByteArray());

						try (PreparedStatement insertUser = connection.prepareStatement(INSERT_USER)) {
							insertUser.setString(1, userEmail);
							insertUser.setBytes(2, salt);
							insertUser.setBytes(3, hash);

							if (insertUser.executeUpdate() == 1) {
								response.setStatus(HttpServletResponse.SC_OK);
							} else {
								logger.log(Level.SEVERE, "There was a problem inserting a new user into the database");
								response.getWriter().write(ErrorHandler
										.createErrorJSONString(ErrorHandler.getErrorMessageForStatusCode(500), 500));
								response.setStatus(500);
							}
						}
					} else {
						response.getWriter()
								.write(ErrorHandler.createErrorJSONString("Email address already taken", 400));
						response.setStatus(400);
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
