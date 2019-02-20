package com.github.chess.techexercise.api.v1.jsonrecords;

import java.util.Date;

public class MessageRecord {
	private int messageID;
	private int userID;
	private String userEmail;
	private String message;
	private Date createdOn;
	
	public MessageRecord(int messageID, int userID, String userEmail, String message, Date createdOn) {
		setMessageID(messageID);
		setUserID(userID);
		setUserEmail(userEmail);
		setMessage(message);
		setCreatedOn(createdOn);
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	

}
