package com.example.theexplorer.services;

import java.util.Date;

public class Comment {

    private String commentId;
    private String userId;
    private String content;
    private Date createdAt;

    public Comment() {
        commentId = "";
        userId = "";
        content = "";
        createdAt = new Date();
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
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
