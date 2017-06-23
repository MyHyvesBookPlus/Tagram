package nl.myhyvesbookplus.tagram.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

public class BitmapPost extends Post {
    private Bitmap photo;

    public BitmapPost(Bitmap photo, String comment, Date date, int nietSlechts, String poster) {
        super(comment, date, nietSlechts, poster);
        this.photo = photo;
    }

    public BitmapPost(Bitmap photo, String comment) {
        super(comment);
        this.photo = photo;
    }

    public Bitmap getBitmap() {
        return photo;
    }

    public UriPost getUriPost(Uri url) {
        return new UriPost(url, getComment(), getDate(), getNietSlechts(), getPoster());
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}