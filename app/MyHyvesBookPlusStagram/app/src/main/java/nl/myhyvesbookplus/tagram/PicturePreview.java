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
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Draws the picture taken and applies filters, which can be switched.
 */
public class PicturePreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "PicturePreview";
    private static final int FILTER_NONE = 0;
    private static final int FILTER_SEPIA = 1;
    private static final int FILTER_BW = 2;
    private static final int FILTER_NEG = 3;

    private static int currentFilter = FILTER_NONE;

    private int facing;
    private int rotate;
    private Bitmap picture;
    private Bitmap filterPicture;

    /**
     * Constructor: changes image based on current direction the camera is facing.
     * @param context
     * @param bmp Image to be previewed.
     * @param facing Direction camera is facing.
     */
    public PicturePreview(Context context, Bitmap bmp, int facing) {
        super(context);
        setWillNotDraw(false);

        this.facing = facing;

        if (((Integer)facing).equals(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            picture = Bitmap.createBitmap(bmp);
            rotate = 270;
        } else {
            picture = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, false);
            rotate = 90;
        }
    }

    /**
     * Checks the current filter and draws and saves the image with altered colours.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ColorMatrix cm = new ColorMatrix();
        Paint paint = new Paint();
        ColorMatrixColorFilter filter;
        Canvas saveCanvas = new Canvas();

        switch (currentFilter) {
            case FILTER_NONE:
                canvas.drawBitmap(rotate(picture, rotate), 0, 0, null);
                filterPicture = rotate(picture, rotate);
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
                saveCanvas.setBitmap(filterPicture);
                saveCanvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
                break;
            case FILTER_BW:
                filterPicture = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);

                cm.setSaturation(0);

                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                saveCanvas.setBitmap(filterPicture);
                saveCanvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
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
                saveCanvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
                canvas.drawBitmap(rotate(picture, rotate), 0, 0, paint);
                break;

        }
    }

    /**
     * Switches filter to the left.
     */
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

    /**
     * Switches filter to the right.
     */
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

    /**
     * Rotates an image by a specified amount of degrees by matrix.
     * @param bmp Image to be rotated.
     * @param degree Amount of degrees to rotate
     * @return Rotated image.
     */
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

    /**
     * Recycles pictures to free memory.
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        picture.recycle();
        filterPicture.recycle();
    }
}
