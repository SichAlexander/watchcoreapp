package com.boost.watchcore.ui.faces.heartbeat;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.faces.BaseWatchFace;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HeartWatchFace extends BaseWatchFace {
    private static final String TAG = "HeartWatchFace";

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends EngineBaseWatchFace {
        private Typeface mDateTypeface;
        private TypedArray mBgTypedArray;
        private SimpleDateFormat mTimeFormatWithoutDots;
        private SimpleDateFormat mDateFormat;
        private static final float DATE_TEXT_ANGLE      = -5f;
        private static final float AMBIENT_DATE_SIZE    = 40f;
        private static final float AMBIENT_TIME_OFFSET  = 0f;
        private static final float AMBIENT_DATE_OFFSET  = 47f;
        private static final float AMBIENT_TIME_SIZE    = 80f;
        private final float OFFSET_X = 10f;

        @Override
        protected void initVars() {
            mConfig_UpdateTime = 40;
            mBgTypedArray = mResources.obtainTypedArray(R.array.drugs_heart_set);
            mDateTypeface =  Typeface.createFromAsset(getAssets(), "gomarice_hyouzi_display.ttf");
            mTimeFormatWithoutDots = new SimpleDateFormat("HH   mm");
            mDateFormat =  new SimpleDateFormat("d MMM . yyyy");
        }

        @Override
        protected void drawFace(Canvas canvas) {
            drawText(canvas, String.valueOf(mTime.get(Calendar.HOUR_OF_DAY)), 0, mCenterY, AMBIENT_TIME_SIZE);
            drawText(canvas, String.valueOf(mTime.get(Calendar.MINUTE)), -1, mCenterY, AMBIENT_TIME_SIZE);
        }

        @Override
        protected void drawAmbient(Canvas canvas) {
            drawAmbientText(canvas,
                    mTimeFormatWithoutDots.format(mTime.getTime()),
                    AMBIENT_TIME_OFFSET,
                    AMBIENT_TIME_SIZE);
            drawAmbientText(canvas,
                    mDateFormat.format(mTime.getTime()),
                    AMBIENT_DATE_OFFSET,
                    AMBIENT_DATE_SIZE);
        }

        private void drawText(Canvas canvas, String value, float offsetX, float offsetY, float textSize){
            Rect bounds = new Rect();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(mDateTypeface);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize * mScaleValue);

            paint.getTextBounds(value, 0, value.length(), bounds);
            float posX = offsetX <0? mCenterX * 2 -  paint.measureText(value) - OFFSET_X : offsetX + OFFSET_X;
            float posY = bounds.bottom - bounds.top;
            canvas.drawText(value,
                    posX,
                    offsetY + posY / 2,
                    paint);
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

        private void drawBitmap(Canvas canvas, Bitmap source) {
            mMatrix.reset();
            mMatrix.postScale(mScaleValue, mScaleValue);
            canvas.drawBitmap(source,
                    mMatrix,
                    new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        }

        private Bitmap getBackground() {
            if (isInAmbientMode()){
                return BitmapFactory.decodeResource(getResources(), R.drawable.bg);
            }
            else

                return BitmapFactory.decodeResource(getResources(),
                        mBgTypedArray.getResourceId((int)(mTime.get(Calendar.MILLISECOND)/mConfig_UpdateTime), -1));
        }

        @Override
        protected void drawBg(Canvas canvas) {
            super.drawBg(canvas);
            drawBitmap(canvas, mBackgroundBitmap);
        }

        @Override
        protected void setBg() {
            mBackgroundBitmap = getBackground();
            mScaleValue = (mCenterX * 2.0f) / mBackgroundBitmap.getWidth();
        }
    }
}
