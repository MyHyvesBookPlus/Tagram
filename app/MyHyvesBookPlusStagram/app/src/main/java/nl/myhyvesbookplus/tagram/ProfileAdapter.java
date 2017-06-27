package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Created by niels on 27-6-17.
 */

public class ProfileAdapter extends BaseAdapter {

    private static final String TAG = "ProfileAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<UriPost> mData;
    private TextView comment;
    private TextView nietSlechts;
    private ImageView photo;

    ProfileAdapter(Context context, ArrayList<UriPost> data) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
    }

    @Override
    public int getCount() {
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
        View rowView = mInflater.inflate(R.layout.list_item_timeline_profile, parent, false);
        View newRowView = findViews(rowView);
        UriPost post = (UriPost) getItem(position);
        comment.setText(post.getComment());
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(post.getUri());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(photo);

        return newRowView;
    }

    protected View findViews(View rowView) {
        comment = (TextView) rowView.findViewById(R.id.comment_timeline_profile);
        nietSlechts = (TextView) rowView.findViewById(R.id.niet_slecht_count_profile);
        photo = (ImageView) rowView.findViewById(R.id.timeline_image_profile);
        return rowView;
    }
}
