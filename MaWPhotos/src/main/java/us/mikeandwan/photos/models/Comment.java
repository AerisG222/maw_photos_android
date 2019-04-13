package us.mikeandwan.photos.models;

import java.util.Date;


public class Comment {
    private Date _entryDate;
    private String _commentText;
    private String _username;

    public Date getEntryDate() { return _entryDate; }
    public void setEntryDate(Date entryDate) { _entryDate = entryDate; }

    public String getCommentText() { return _commentText; }
    public void setCommentText(String commentText) { _commentText = commentText; }

    public String getUsername() { return _username; }
    public void setUsername(String username) { _username = username; }
}
