package com.tactile.tact.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

public class RoundImageView extends NetworkImageView {

    /**
     * Builder for RoundImageView
     * @param context the current context
     */
    public RoundImageView(Context context) {
        super(context);
    }

    /**
     * Builder for RoundImageView
     * @param context the current context
     * @param attrs the set of attributes of the round image view
     */
    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Builder for RoundImageView
     * @param context the current context
     * @param attrs the set of attributes of the round image view
     * @param defStyle int
     */
    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (b == null) {
            return;
        }
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);

    }



    //*********************  UTILITIES *********************\\

    /**
     * Get the cropped bitmap
     * @param bmp the original bitmap
     * @param radius the radius to use for the cropped
     * @return the cropped bitmap
     */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap scaledBitmap;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            scaledBitmap = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            scaledBitmap = bmp;
        Bitmap output = Bitmap.createBitmap(scaledBitmap.getWidth(),
                scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(scaledBitmap.getWidth() / 2 + 0.7f, scaledBitmap.getHeight() / 2 + 0.7f,
                scaledBitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledBitmap, rect, rect, paint);

        return output;
    }

}