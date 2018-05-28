package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nl.myhyvesbookplus.tagram.model.UriPost;

/**
 * Class which creates views for the home-page timeline. This is done with a ListView.
 */

public class TimeLineAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private static final String TAG = "TimeLineAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<UriPost> mData;
    private DatabaseReference mRef;
    private Animator mCurrentAnimator;

    /* TimeLineAdapter constructor */
    TimeLineAdapter(Context context, ArrayList<UriPost> data) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
        mRef = FirebaseDatabase.getInstance().getReference();
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

    /**
     *  Initiate a new view to be part of the ListView.
     * @param position The position at which the view should start.
     * @param convertView The viewconverter.
     * @param parent The parent of the view.
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.list_item_timeline, parent, false);

        TextView comment = rowView.findViewById(R.id.comment_timeline);
        final TextView nietSlechts = rowView.findViewById(R.id.niet_slecht_count);
        TextView dateTime = rowView.findViewById(R.id.timeline_date);
        final ImageView photo = rowView.findViewById(R.id.timeline_image);
        final ImageButton nietSlechtButton = rowView.findViewById(R.id.niet_slecht_button);

        final UriPost post = (UriPost) getItem(position);

        nietSlechts.setText(Integer.toString(post.getNietSlechts()));
        comment.setText(post.getComment());

        nietSlechtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + position);
                mRef.child("posts").child(post.getDatabaseEntryName())
                        .child("nietSlechts").setValue(post.getNietSlechts() + 1)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                nietSlechts.setText(Integer.toString(post.getNietSlechts() + 1));
                            }
                        });
            }
        });

        dateTime.setText(post.getDate().toString());

        final StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(post.getUri());
        GlideApp.with(mContext)
                .load(ref)
                .into(photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(photo, ref);
            }
        });

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

    /**
     * https://developer.android.com/training/animation/zoom.html
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area.
     *
     * @param thumbView  The thumbnail view to zoom in.
     * @param imageRef The high-resolution version of the image represented by the thumbnail.
     */
    private void zoomImageFromThumb(final View thumbView, StorageReference imageRef) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView hiddenView = ((MainActivity) mContext).findViewById(R.id.expanded_image);

        GlideApp.with(mContext)
                .load(imageRef)
                .into(hiddenView);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        ((MainActivity) mContext).findViewById(R.id.relative_layout_timeline).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        hiddenView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        hiddenView.setPivotX(0f);
        hiddenView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(hiddenView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(hiddenView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(hiddenView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(hiddenView, View.SCALE_Y, startScale, 1f));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        hiddenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(hiddenView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(hiddenView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(hiddenView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(hiddenView, View.SCALE_Y, startScaleFinal));
                set.setDuration(200);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        hiddenView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        hiddenView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
