package com.boost.watchcore.ui.faces.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.faces.BaseWatchFace;

import java.text.SimpleDateFormat;

public class SimpleWatchFace extends BaseWatchFace {

    private static final String TAG = "SimpleWatchFace";

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine  extends BaseWatchFace.EngineBaseWatchFace {
        private Bitmap mWhiteLine;
        private Typeface mDateTypeface;
        private SimpleDateFormat mDateFormat;
        private SimpleDateFormat mTimeFormat;
        private SimpleDateFormat mTimeFormatWithoutDots;
        private boolean mNeedDot = true;

        private static final float DATE_TEXT_ANGLE      = -5f;
        private static final float AMBIENT_TIME_SIZE    = 100f;
        private static final float AMBIENT_DATE_SIZE    = 40f;
        private static final float AMBIENT_TIME_OFFSET  = 0f;
        private static final float AMBIENT_DATE_OFFSET  = 47f;
        @Override
        protected void initVars() {
            mConfig_UpdateTime = 1000;
            mDateFormat =  new SimpleDateFormat("d MMM . yyyy");
            mTimeFormat = new SimpleDateFormat("HH : mm");
            mTimeFormatWithoutDots = new SimpleDateFormat("HH   mm");
            mDateTypeface =  Typeface.createFromAsset(getAssets(), "gomarice_hyouzi_display.ttf");
        }


        @Override
        protected void drawWatch(Canvas canvas) {
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            mNeedDot = !mNeedDot;
            if (isInAmbientMode()) {
                mNeedDot = true;
            }
            drawAmbientText(canvas,
                    mNeedDot? mTimeFormat.format(mTime.getTime()) : mTimeFormatWithoutDots.format(mTime.getTime()),
                    AMBIENT_TIME_OFFSET,
                    AMBIENT_TIME_SIZE);
            drawWhiteLine(canvas);

            drawAmbientText(canvas,
                    mDateFormat.format(mTime.getTime()),
                    AMBIENT_DATE_OFFSET,
                    AMBIENT_DATE_SIZE);

        }


        private void drawAmbientText(Canvas canvas, String value, float offset, float textSize){
            Rect bounds = new Rect();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(mDateTypeface);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize * mScaleValue);

            paint.getTextBounds(value, 0, value.length(), bounds);
            float textWidth = paint.measureText(value);
            float textHeight = bounds.bottom - bounds.top;
            canvas.save();
            canvas.rotate(DATE_TEXT_ANGLE, mCenterX, mCenterY);
            canvas.drawText(value,
                    mCenterX - textWidth / 2f,
                    mCenterY + offset - textHeight / 2,
                    paint);
            canvas.restore();
        }

        private void drawWhiteLine(Canvas canvas){
            if (!isInAmbientMode()) {
                mWhiteLine = BitmapFactory.decodeResource(getResources(), R.drawable.vinyl_white_line);
                mMatrix.reset();
                float widthLine = mWhiteLine.getWidth() * mScaleValue;
                float heightLine = mWhiteLine.getHeight() * mScaleValue;
                mMatrix.postScale(mScaleValue, mScaleValue);
                mMatrix.postTranslate(mCenterX - widthLine / 2, mCenterY - heightLine);
                canvas.drawBitmap(mWhiteLine, mMatrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            }
        }
    }
}
