package com.travelbuddy.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    private Long groupId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;
    private String type; // CHAT | JOIN | LEAVE
    private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
    
}
