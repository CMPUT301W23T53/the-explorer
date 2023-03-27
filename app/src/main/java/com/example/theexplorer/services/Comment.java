package com.example.theexplorer.services;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    private String commentId;
    private String userId;
    private String QRId;
    private String content;
    private Date createdAt;

    public Comment() {
        commentId = "temp";
        userId = "";
        content = "";
        createdAt = new Date();
    }

    public String getQRId() {
        return QRId;
    }

    public void setQRId(String QRId) {
        this.QRId = QRId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId='" + commentId + '\'' +
                ", userId='" + userId + '\'' +
                ", QRId='" + QRId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("content", content);
        result.put("QRId", QRId);
        result.put("createdAt", new Timestamp(createdAt));
        return result;
    }
}
