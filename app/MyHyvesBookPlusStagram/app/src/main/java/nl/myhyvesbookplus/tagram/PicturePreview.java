package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by felix on 23/06/2017.
 */

public class PicturePreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "PicturePreveiew";
    private static final int FILTER_NONE = 0;
    private static final int FILTER_SEPIA = 1;
    private static final int FILTER_BW = 2;
    private static final int FILTER_NEG = 3;

    private static int currentFilter = FILTER_NONE;

    Bitmap picture;
    Bitmap filterPicture;

    public PicturePreview(Context context, Bitmap bmp) {
        super(context);
        setWillNotDraw(false);

        picture = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, false);
        Log.d(TAG, "PicturePreview: " + bmp.getWidth() + " " + bmp.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ColorMatrix cm = new ColorMatrix();
        Paint paint = new Paint();
        ColorMatrixColorFilter filter;
        Canvas saveCanvas = new Canvas();

        switch (currentFilter) {
            case FILTER_NONE:
                canvas.drawBitmap(rotate(picture, 90), 0, 0, null);
                filterPicture = rotate(picture, 90);
                break;
            case FILTER_SEPIA:
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Log.d(TAG, "onDraw: " + Integer.toString(canvas.getWidth()));

                float[] sepia = {0.393f,0.769f,0.189f,0f,0f,
                                 0.349f,0.686f,0.168f,0f,0f,
                                 0.272f,0.534f,0.131f,0f,0f,
                                 0f, 0f, 0f, 1f, 0f};
                cm.set(sepia);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                break;
            case FILTER_BW:
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

                cm.setSaturation(0);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
                saveCanvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                break;
            case FILTER_NEG:
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

                float[] neg = {-1f,0f,0f,0f,255f,
                                0f,-1f,0f,0f,255f,
                                0f,0f,-1f,0f,255f,
                                0f,0f,0f,1f,0f};
                cm.set(neg);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
                saveCanvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                break;

        }
    }

    public static void filterPrev() {
        switch (currentFilter) {
            case FILTER_NONE:
                currentFilter = FILTER_NEG;
                break;
            case FILTER_SEPIA:
                currentFilter = FILTER_NONE;
                break;
            case FILTER_BW:
                currentFilter = FILTER_SEPIA;
                break;
            case FILTER_NEG:
                currentFilter = FILTER_BW;
                break;
        }
    }

    public static void filterNext() {
        switch (currentFilter) {
            case FILTER_NONE:
                currentFilter = FILTER_SEPIA;
                break;
            case FILTER_SEPIA:
                currentFilter = FILTER_BW;
                break;
            case FILTER_BW:
                currentFilter = FILTER_NEG;
                break;
            case FILTER_NEG:
                currentFilter = FILTER_NONE;
                break;
        }
    }

    public static Bitmap rotate(Bitmap bmp, int degree) {
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mtx, true);
    }

    public Bitmap getPicture() {
        picture.recycle();
        return filterPicture;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: PICTURE DESTROYED");
        picture.recycle();
        filterPicture.recycle();
    }
}
