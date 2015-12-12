package ru.eadm.nobird.design;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import ru.eadm.nobird.R;

public final class RoundImageView extends ImageView {
    private final float mCornerRadius;

    public RoundImageView(final Context context) {
        super(context);
        mCornerRadius = 0;
    }

    public RoundImageView(final Context context, final AttributeSet attributes) {
        super(context, attributes);
        final TypedArray array = context.obtainStyledAttributes(attributes, R.styleable.RoundImageView);
        if (array != null) {
            mCornerRadius = array.getDimension(R.styleable.RoundImageView_corner_radius, 0);
            array.recycle();
        } else {
            mCornerRadius = 0;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final long time = System.currentTimeMillis();
        final Drawable maiDrawable = getDrawable();
        if (maiDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
            final Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
            final int color = 0xff000000;
            final Rect bitmapBounds = maiDrawable.getBounds();
            final RectF rectF = new RectF(bitmapBounds);
            // Create an off-screen bitmap to the PorterDuff alpha blending to work right
            final int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.MATRIX_SAVE_FLAG |
                    Canvas.CLIP_SAVE_FLAG |
                    Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                    Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                    Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            // Resize the rounded rect we'll clip by this view's current bounds
            // (super.onDraw() will do something similar with the drawable to draw)
            getImageMatrix().mapRect(rectF);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

            final Xfermode oldMode = paint.getXfermode();
            // This is the paint already associated with the BitmapDrawable that super draws
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);

//            Log.d("roundImage", System.currentTimeMillis() - time + "ms | " + bitmapBounds.width() + "x" + bitmapBounds.height());
        } else {
            super.onDraw(canvas);
        }
    }
}
