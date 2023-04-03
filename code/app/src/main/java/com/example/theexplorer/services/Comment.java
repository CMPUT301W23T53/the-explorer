/**
 * The Comment class represents a comment of a QR Code. It contains
 * the comment's ID, user's id that commented this,
 * qr id of the qr code it contained, and createdAt for the date
 */

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
    /**
     * Creates a new Comment object with default values.
     */
    public Comment() {
        commentId = "temp";
        userId = "";
        content = "";
        createdAt = new Date();
    }
    /**
     * Returns the ID of the QR code associated with the comment.
     *
     * @return the QR code ID
     */
    public String getQRId() {
        return QRId;
    }
    /**
     * Sets the ID of the QR code associated with the comment.
     *
     * @param QRId the QR code ID to set
     */
    public void setQRId(String QRId) {
        this.QRId = QRId;
    }
    /**
     * Returns the ID of the comment.
     *
     * @return the comment ID
     */
    public String getCommentId() {
        return commentId;
    }
    /**
     * Sets the ID of the comment.
     *
     * @param commentId the comment ID to set
     */
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    /**
     * Returns the ID of the user who made the comment.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
    /**
     * Sets the ID of the user who made the comment.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * Returns the content of the comment.
     *
     * @return the comment content
     */
    public String getContent() {
        return content;
    }
    /**
     * Sets the content of the comment.
     *
     * @param content the comment content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * Returns the date the comment was created.
     *
     * @return the comment creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    /**
     * Sets the date the comment was created.
     *
     * @param createdAt the comment creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    /**
     * Returns a String representation of the Comment object.
     *
     * @return a String representation of the Comment object
     */
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
    /**
     * Converts the Comment object to a Map that can be stored in a database.
     *
     * @return a Map representation of the Comment object
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("content", content);
        result.put("QRId", QRId);
        result.put("createdAt", new Timestamp(createdAt));
        return result;
    }
}
