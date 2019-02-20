package com.github.chess.techexercise.api.v1.jsonrecords;

public class TokenRecord {
	private String tokenID;
	private String token;
	
	public TokenRecord(String tokenID, String token) {
		setTokenID(tokenID);
		setToken(token);
	}

	public String getTokenID() {
		return tokenID;
	}

	public void setTokenID(String tokenID) {
		this.tokenID = tokenID;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
