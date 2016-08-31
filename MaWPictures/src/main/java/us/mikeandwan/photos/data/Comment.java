package us.mikeandwan.photos.data;

import java.util.Date;


@SuppressWarnings("ALL")
public class Comment {
    private Date _entryDate;

    public Date getEntryDate() {
        return _entryDate;
    }

    public void setEntryDate(Date entryDate) {
        _entryDate = entryDate;
    }

    private String _commentText;

    public String getCommentText() {
        return _commentText;
    }

    public void setCommentText(String commentText) {
        _commentText = commentText;
    }

    private String _username;

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }
}
