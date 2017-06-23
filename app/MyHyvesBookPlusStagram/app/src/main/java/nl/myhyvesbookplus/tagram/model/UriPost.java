package nl.myhyvesbookplus.tagram.model;

import android.net.Uri;

import java.util.Date;

/**
 * UriPost is a Class for a Post with a Uri as an image.
 */
public class UriPost extends Post {
    private String photo;

    public UriPost() {
        // Default constructor required for calls to DataSnapshot.getValue(UriPost.class)

    }

    public UriPost(Uri photo, String comment, Date date, int nietSlechts, String poster) {
        super(comment, date, nietSlechts, poster);
        this.photo = photo.toString();
    }

    public UriPost(String photo, String comment) {
        super(comment);
        this.photo = photo;
    }

    public String getUri() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}