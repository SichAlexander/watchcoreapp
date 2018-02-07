package com.boost.watchcore.ui.faces.pizza;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.faces.BaseWatchFace;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PizzaWatchFace extends BaseWatchFace {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends EngineBaseWatchFace {
        private static final String TAG = "PizzaWatchFace";
        private int mHourCurrent = -1;
        private Bitmap mHourBitmap;
        private Bitmap mMinuteBitmap;
        private Typeface mDateTypface;
        private SimpleDateFormat mDateFormat;
        private SimpleDateFormat mTimeFormat;

        private static final float AMBIENT_TIME_SIZE = 55f;
        private static final float AMBIENT_DATE_SIZE = 30f;
        private static final float AMBIENT_TIME_OFFSET_X = 140f;
        private static final float AMBIENT_TIME_OFFSET_Y = 145f;
        private static final float AMBIENT_DATE_OFFSET_X = 185f;
        private static final float AMBIENT_DATE_OFFSET_Y = 210f;

        @Override
        protected void initVars() {
            mConfig_UpdateTime = 1000;
            mDateFormat = new SimpleDateFormat("d MMM", Locale.US);
            mTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            mDateTypface =Typeface.createFromAsset(getAssets(), "BOYCOTT.TTF");
        }

        @Override
        protected void drawFace(Canvas canvas) {
            setMinuteHand();
            float minRot  = 6.0f * mTime.get(Calendar.MINUTE) + 0.1f * mTime.get(Calendar.SECOND);
            drawMinuteHand(canvas, minRot);
        }

        @Override
        protected void drawAmbient(Canvas canvas) {
            drawText(canvas, mTimeFormat.format(mTime.getTime()), AMBIENT_TIME_OFFSET_X, AMBIENT_TIME_OFFSET_Y, AMBIENT_TIME_SIZE);
            drawText(canvas, mDateFormat.format(mTime.getTime()), AMBIENT_DATE_OFFSET_X, AMBIENT_DATE_OFFSET_Y, AMBIENT_DATE_SIZE);
        }

        @Override
        protected void drawBg(Canvas canvas) {
            super.drawBg(canvas);
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            mMatrix.reset();
            mMatrix.postScale(mScaleValue, mScaleValue);
            canvas.drawBitmap(mBackgroundBitmap, mMatrix,
                    mBitmapPaint);

            if (!isInAmbientMode())
                canvas.drawBitmap(mHourBitmap, mMatrix,
                        mBitmapPaint);
        }

        private void drawText(Canvas canvas, String value, float offsetX, float offsetY, float textSize){
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTypeface(mDateTypface);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize * mScaleValue);
            canvas.drawText(value, offsetX * mScaleValue, offsetY * mScaleValue, paint);
        }

        private void drawMinuteHand(Canvas canvas, float minRot) {
            mMatrix.reset();
            float widthSecond = mMinuteBitmap.getWidth() * mScaleValue;
            float heightSecond = mMinuteBitmap.getHeight() * mScaleValue;
            mMatrix.postScale(mScaleValue, mScaleValue);
            mMatrix.postRotate(minRot, widthSecond / 2.0f, heightSecond / 2.0f);

            canvas.drawBitmap(mMinuteBitmap,
                    mMatrix,
                   mBitmapPaint);
        }

        private void setMinuteHand() {
            if (mMinuteBitmap == null) {
                mMinuteBitmap = BitmapFactory.decodeResource(mResources, R.drawable.pizza_hand_minute);
            }
        }

        @Override
        protected void setBg() {
            if (mHourCurrent == mTime.get(Calendar.HOUR) && mBackgroundBitmap != null) {
                return;
            }
            mHourCurrent = mTime.get(Calendar.HOUR);
            if (isInAmbientMode()){
                mBackgroundBitmap = BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_ambient);
            }
            else
                mBackgroundBitmap = BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_square);

            mHourBitmap = getHourBitmap();
            mScaleValue = (mCenterX * 2.0f) / mHourBitmap.getWidth();
        }

        private Bitmap getHourBitmap() {

            if (isInAmbientMode()) {
                return BitmapFactory.decodeResource(mResources, R.drawable.bg);
            }
            switch (mHourCurrent) {
                case 0:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_12);
                case 1:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_1);
                case 2:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_2);
                case 3:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_3);
                case 4:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_4);
                case 5:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_5);
                case 6:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_6);
                case 7:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_7);
                case 8:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_8);
                case 9:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_9);
                case 10:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_10);
                case 11:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg_pizza_11);

                default:
                    return BitmapFactory.decodeResource(mResources, R.drawable.bg);
            }
        }
    }
}