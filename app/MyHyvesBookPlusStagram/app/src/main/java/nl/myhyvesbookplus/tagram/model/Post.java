package nl.myhyvesbookplus.tagram.model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

/**
 * Created by marijnjansen on 22/06/2017.
 */

abstract class Post {

    private Date date;
    private String comment;
    private int nietSlechts;
    private String poster;

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

    public String getComment() {
        return comment;
    }

    public int getNietSlechts() {
        return nietSlechts;
    }

    public String getPoster() {
        return poster;
    }
}