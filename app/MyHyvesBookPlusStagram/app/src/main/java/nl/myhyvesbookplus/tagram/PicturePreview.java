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

    private BitmapFactory.Options options = new BitmapFactory.Options();
    private int imageHeight;
    private int imageWidth;

    Bitmap picture;
    Bitmap filterPicture;
    byte[] data;

    public PicturePreview(Context context, Bitmap bmp) {
//    public PicturePreview(Context context, byte[] data) {
        super(context);
        setWillNotDraw(false);

//        this.data = data;
//        options.inJustDecodeBounds = true;
        picture = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 4, bmp.getHeight() / 4, false);
//        picture = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        imageHeight = options.outHeight;
//        imageWidth = options.outWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        options.inSampleSize = 4; //calculateInSampleSize(options, canvas.getWidth(), canvas.getHeight());
//        options.inJustDecodeBounds = false;
//        picture = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        picture = Bitmap.createBitmap(bmp);
//        bmp.recycle();

        ColorMatrix cm = new ColorMatrix();
        Paint paint = new Paint();
        ColorMatrixColorFilter filter;
        Canvas saveCanvas = new Canvas();

        switch (currentFilter) {
            case FILTER_NONE:
//                canvas.rotate(90);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, null);
                filterPicture = rotate(picture, 90);
                break;
            case FILTER_SEPIA:
//                filterPicture = Bitmap.createBitmap(picture.getWidth() / 4, picture.getHeight() / 4, Bitmap.Config.ARGB_8888);
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Log.d(TAG, "onDraw: " + Integer.toString(canvas.getWidth()));

                float[] sepia = {0.393f,0.769f,0.189f,0f,0f,
                                 0.349f,0.686f,0.168f,0f,0f,
                                 0.272f,0.534f,0.131f,0f,0f,
                                 0f, 0f, 0f, 1f, 0f};
                cm.set(sepia);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
//                saveCanvas.rotate(90);
//                canvas.rotate(90);
                saveCanvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                break;
            case FILTER_BW:
//                filterPicture = Bitmap.createBitmap(picture.getWidth() / 4, picture.getHeight() / 4, Bitmap.Config.ARGB_8888);
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

//                filterPicture = Bitmap.createBitmap(1920, 1440, null);
                cm.setSaturation(0);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
//                saveCanvas.rotate(90);
//                canvas.rotate(90);
                saveCanvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, 90), 0, 0, paint);
                break;
            case FILTER_NEG:
//                filterPicture = Bitmap.createBitmap(picture.getWidth() / 4, picture.getHeight() / 4, Bitmap.Config.ARGB_8888);
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

                float[] neg = {-1f,0f,0f,0f,255f,
                                0f,-1f,0f,0f,255f,
                                0f,0f,-1f,0f,255f,
                                0f,0f,0f,1f,0f};
                cm.set(neg);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
//                saveCanvas.rotate(90);
//                canvas.rotate(90);
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

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap getPicture() {
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
    }
}
