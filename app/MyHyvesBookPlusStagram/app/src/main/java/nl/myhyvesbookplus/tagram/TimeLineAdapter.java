package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

public class TimeLineAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
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
        TextView nietSlechts = (TextView) rowView.findViewById(R.id.niet_slecht_count);
        ImageView photo = (ImageView) rowView.findViewById(R.id.timeline_image);
        ImageButton nietSlechtButton = (ImageButton) rowView.findViewById(R.id.niet_slecht_button);

        nietSlechtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        UriPost post = (UriPost) getItem(position);

        nietSlechts.setText(Integer.toString(post.getNietSlechts()));
        comment.setText(post.getComment());

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(post.getUri());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(photo);


        return rowView;
    }


    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: rowNumber! "+ position);
    }

}
