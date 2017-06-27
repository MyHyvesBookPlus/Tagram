package nl.myhyvesbookplus.tagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by felix on 23/06/2017.
 */

public class PicturePreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final int FILTER_NONE = 0;
    private static final int FILTER_SEPIA = 1;
    private static final int FILTER_BW = 2;

    private static int currentFilter = FILTER_NONE;

    Bitmap picture;
    Bitmap filterPicture;

    public PicturePreview(Context context, Bitmap bmp) {
        super(context);
        picture = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, false);
//        picture = Bitmap.createBitmap(bmp);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ColorMatrix cm = new ColorMatrix();
        Paint paint = new Paint();
        ColorMatrixColorFilter filter;

        switch (currentFilter) {
            case FILTER_NONE:
                canvas.drawBitmap(picture, 0, 0, null);
                canvas.rotate(90);
                filterPicture = picture;
                break;
            case FILTER_SEPIA:
                canvas.drawBitmap(toSepia(picture), 0, 0, null);
                canvas.rotate(90);
                filterPicture = toSepia(picture);
                break;
            case FILTER_BW:
                Canvas bw = new Canvas();
//                filterPicture = Bitmap.createBitmap(1920, 1440, null);
                filterPicture = Bitmap.createBitmap(picture.getWidth() / 2, picture.getHeight() / 2, Bitmap.Config.ARGB_8888);
                cm.setSaturation(0);
                filter = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(filter);
                bw.setBitmap(filterPicture);
                bw.drawBitmap(picture, 0, 0, paint);
                bw.rotate(90);
                canvas.drawBitmap(picture, 0, 0, paint);
                canvas.rotate(90);
                break;
        }
    }

    public static void filterPrev() {
        switch (currentFilter) {
            case FILTER_NONE:
                currentFilter = FILTER_BW;
                break;
            case FILTER_SEPIA:
                currentFilter = FILTER_NONE;
                break;
            case FILTER_BW:
                currentFilter = FILTER_SEPIA;
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
                currentFilter = FILTER_NONE;
                break;
        }
    }

    public Bitmap toSepia(Bitmap color) {
        int red, green, blue, pixel;
        int height = color.getHeight();
        int width = color.getWidth();
        int depth = 20;

        Bitmap sepia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[width * height];
        color.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++) {
            pixel = pixels[i];

            red = (pixel >> 16) & 0xFF;
            green = (pixel >> 8) & 0xFF;
            blue = pixel & 0xFF;

            red = green = blue = (red + green + blue) / 3;

            red += (depth * 2);
            green += depth;

            if (red > 255)
                red = 255;
            if (green > 255)
                green = 255;
            pixels[i] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
        }
        sepia.setPixels(pixels, 0, width, 0, 0, width, height);
        return sepia;
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
