package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Created by marijnjansen on 26/06/2017.
 */

public class TimeLineAdapter extends BaseAdapter {
    private static final String TAG = "TimeLineAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<UriPost> mData;

    TimeLineAdapter(Context context, ArrayList<UriPost> data) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + mData.size());
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_timeline, parent, false);

        TextView comment = (TextView) rowView.findViewById(R.id.comment_timeline);
        TextView nietslechts = (TextView) rowView.findViewById(R.id.niet_slecht_count);
        ImageView photo = (ImageView) rowView.findViewById(R.id.timeline_image);

        UriPost post = (UriPost) getItem(position);

        nietslechts.setText(Integer.toString(post.getNietSlechts()));
        comment.setText(post.getComment());

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(post.getUri());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(photo);


        return rowView;
    }
}
