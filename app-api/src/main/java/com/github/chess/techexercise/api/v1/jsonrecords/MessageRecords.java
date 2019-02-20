package com.github.chess.techexercise.api.v1.jsonrecords;

import java.util.List;

public class MessageRecords {
	private List<MessageRecord> messages;
	
	public MessageRecords(List<MessageRecord> messages) {
		setMessages(messages);
	}
	
	public List<MessageRecord> getMessages() {
		return messages;
	}
	
	public void setMessages(List<MessageRecord> messages) {
		this.messages = messages;
	}
}
