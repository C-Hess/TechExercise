package com.github.chess.techexercise.api.v1.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class ApiUtils {
	private static final String SELECT_TOKEN = "select Token from Tokens where TokenID=?";
	private static final String SELECT_USERID = "select UserID from Tokens where TokenID=?";
	private static final String SELECT_EMAIL = "select Email from Users where UserID=?";

	private static String dbEndpoint = "jdbc:mysql://localhost:3306/techexDB";
	private static String dbUsername = "techapi";
	private static String dbPassword = "&&91=coat=BEHIND=twelve=OBJECT=base=15&&";


	
	public static JsonObject getJSonParser(HttpServletRequest request) throws IOException, JsonParseException {
		StringBuilder data = new StringBuilder("");
		String nextLine;
		try (BufferedReader reader = request.getReader()) {
			while ((nextLine = reader.readLine()) != null) {
				data.append(nextLine);
			}
		}

		JsonParser jsonParser = new JsonParser();
		return jsonParser.parse(data.toString()).getAsJsonObject();
	}

	public static String getAuthTokenID(HttpServletRequest request) {
		String auth = request.getHeader("Authorization");
		if (auth != null) {
			auth = auth.trim();
			if (auth.toUpperCase().startsWith("DIGEST ")) {
				String[] spaceSplit = auth.split(" ");
				if (spaceSplit.length == 2) {
					String[] authSplit = spaceSplit[1].split(":");
					if (authSplit.length == 2) {
						return authSplit[0];
					}
				}
			}
		}
		return null;
	}

	public static Integer getUserID(String tokenID, Connection connection) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement(SELECT_USERID)) {
			ps.setString(1, tokenID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("UserID");
			}
		}

		return null;
	}

	public static Integer getEmail(int userID, Connection connection) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement(SELECT_EMAIL)) {
			ps.setInt(1, userID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("Email");
			}
		}

		return null;
	}

	public static boolean authenticate(HttpServletRequest request, Connection connection)
			throws SQLException, NoSuchAlgorithmException {
		String auth = request.getHeader("Authorization");
		if (auth != null) {
			auth = auth.trim();
			if (auth.toUpperCase().startsWith("DIGEST ")) {
				String[] spaceSplit = auth.split(" ");
				if (spaceSplit.length == 2) {
					String[] authSplit = spaceSplit[1].split(":");
					if (authSplit.length == 2) {
						String tokenID = authSplit[0];
						String hash64 = authSplit[1];

						String method = request.getMethod();
						String uriQuery = request.getRequestURI();
						
						if(request.getQueryString() != null) {
							uriQuery += "?" + request.getQueryString();
						}

						try (PreparedStatement tokenStatement = connection.prepareStatement(SELECT_TOKEN)) {
							tokenStatement.setString(1, tokenID);

							ResultSet rs = tokenStatement.executeQuery();

							if (rs.next()) {
								String token = rs.getString("Token");

								try {
									MessageDigest digest = MessageDigest.getInstance("SHA-256");
									String digestStr = (token + "+" + method.toUpperCase() + "+" + uriQuery);
									byte[] serverHash = digest.digest(digestStr.getBytes(StandardCharsets.UTF_8));
									byte[] clientHash = Base64.getDecoder().decode(hash64);

									if (Arrays.equals(serverHash, clientHash)) {
										return true;
									}
								} catch (IllegalArgumentException e) {
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static String getDBEndpoint() {
		return dbEndpoint;
	}

	public static String getDBUsername() {
		return dbUsername;
	}

	public static String getDBPassword() {
		return dbPassword;
	}
}
