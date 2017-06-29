package nl.myhyvesbookplus.tagram.model;

import android.net.Uri;

import java.util.Date;

/**
 * UriPost is a Class for a Post with a Uri as an image.
 */
public class UriPost extends Post {
    private String uri;
    private String databaseEntryName;

    public UriPost() {
        // Default constructor required for calls to DataSnapshot.getValue(UriPost.class)

    }

    public UriPost(Uri uri, String comment, Date date, int nietSlechts, String poster) {
        super(comment, date, nietSlechts, poster);
        this.uri = uri.toString();
    }

    public UriPost(String uri, String comment) {
        super(comment);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }


    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDatabaseEntryName() {
        return databaseEntryName;
    }

    public void setDatabaseEntryName(String databaseEntryName) {
        this.databaseEntryName = databaseEntryName;
    }
}