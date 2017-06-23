package nl.myhyvesbookplus.tagram.model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

/**
 * Post is a Class for a Post with a Bitmap as an image.
 */
abstract class Post {

    private Date date;
    private String comment;
    private int nietSlechts;
    private String poster;

    Post() {
        // Default constructor required for calls to Post.getValue(User.class)
    }

    Post(String comment, Date date, int nietSlechts, String poster) {
        this.date = date;
        this.comment = comment;
        this.nietSlechts = nietSlechts;
        this.poster = poster;
    }

    Post(String comment) {
        this(comment, new Date(), 0, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNietSlechts() {
        return nietSlechts;
    }

    public void setNietSlechts(int nietSlechts) {
        this.nietSlechts = nietSlechts;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}