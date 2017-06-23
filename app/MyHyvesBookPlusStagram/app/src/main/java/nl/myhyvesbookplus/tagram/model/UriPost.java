package nl.myhyvesbookplus.tagram.model;

import android.net.Uri;

import java.util.Date;

public class UriPost extends Post {
    private Uri photo;

    public UriPost() {

    }

    public UriPost(Uri photo, String comment, Date date, int nietSlechts, String poster) {
        super(comment, date, nietSlechts, poster);
        this.photo = photo;
    }

    public UriPost(Uri photo, String comment) {
        super(comment);
        this.photo = photo;
    }

    public Uri getUri() {
        return photo;
    }
}