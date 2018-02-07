package com.boost.watchcore.ui.faces.material;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.boost.watchcore.R;
import com.boost.watchcore.WatchConstants;
import com.boost.watchcore.WatchFaceUtil;
import com.boost.watchcore.ui.faces.BaseWatchFace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MaterialWatchFace extends BaseWatchFace {
    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends EngineBaseWatchFace {
        private static final String TAG = "MaterialWatchFace";
        private int mHourCurrent = -1;

        private Bitmap mBackgroundBorder;
        private BitmapDrawable mSecondScaledBitmap;
        private BitmapDrawable mMinuteScaledBitmap;
        private BitmapDrawable mHourScaledBitmap;
        private BitmapDrawable mSecondShadowScaledBitmap;
        private BitmapDrawable mMinuteShadowScaledBitmap;
        private BitmapDrawable mHourShadowScaledBitmap;
        private SimpleDateFormat mDateFormat;

        private static final float OFFSET                   = 20f;
        private static final float AMBIENT_HOUR_SIZE        = 120f;
        private static final float AMBIENT_MINUTE_SIZE      = 60f;
        private static final float AMBIENT_TEXT_STROKE_WIDTH= 1.5f;
        private static final float DATE_SIZE                = 20f;
        private static final float DATE_TEXT_STROKE_WIDTH   = 0;
        private static final float DATE_OFFSET              = 65f;
        private static final float SHADOW_OFFSET            = 5.0f;

        //local config vars
        private boolean mConfig_Italian         = false;
        private boolean mConfig_ShowHours       = false;
        private boolean mConfig_ShowShadow      = false;
        private boolean mConfig_ShowDate        = false;
        private boolean mConfig_RomanAmbient    = false;
        private boolean mConfig_NightMode       = false;
         //Listeners

        private DataApi.DataListener  mDataListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                try {
                    for (DataEvent dataEvent : dataEvents) {
                        if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                            continue;
                        }

                        DataItem dataItem = dataEvent.getDataItem();
                        if (!dataItem.getUri().getPath().equals(
                                WatchConstants.PATH_WITH_FEATURE_MATERIAL)) {
                            continue;
                        }

                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                        DataMap config = dataMapItem.getDataMap();
                        if (Log.isLoggable(TAG, Log.DEBUG)) {
                            Log.d(TAG, "Config DataItem updated:" + config);
                        }
                        updateUiForConfigDataMap(config);
                    }
                } finally {
                    dataEvents.close();
                }
            }
        };

        private GoogleApiClient.ConnectionCallbacks mConnectionCallback = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Wearable.DataApi.addListener(mGoogleApiClient, mDataListener);
                updateConfigDataItemAndUiOnStartup();
            }

            @Override
            public void onConnectionSuspended(int cause) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "onConnectionSuspended: " + cause);
                }
            }
        };

        private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "onConnectionFailed: " + result);
                }
            }
        };

        private  GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MaterialWatchFace.this)
                .addConnectionCallbacks(mConnectionCallback)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(Wearable.API)
                .build();

        @Override
        protected void initVars() {
            mGoogleApiClient.connect();
            mConfig_UpdateTime =  TimeUnit.SECONDS.toMillis(1);
            mDateFormat =  new SimpleDateFormat("E d");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                mGoogleApiClient.connect();
            } else if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(mGoogleApiClient, mDataListener);
                    mGoogleApiClient.disconnect();
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        protected void drawWatch(Canvas canvas) {
            super.drawWatch(canvas);
            drawDate(canvas);
            drawBorder(canvas);

        }
        @Override
        protected void drawBg(Canvas canvas) {
            super.drawBg(canvas);
            if (mConfig_NightMode){
                canvas.drawColor(mResources.getColor(R.color.material_night));
            }
            else {
                canvas.drawColor(mResources.getColor(R.color.material_day));
            }
            mMatrix.reset();
            mMatrix.postScale(mScaleValue, mScaleValue);
            canvas.drawBitmap(mBackgroundBitmap, mMatrix,
                    mBitmapPaint);
        }

        private void drawDate(Canvas canvas){
            if (mConfig_ShowDate) {
                drawText(canvas,
                        mDateFormat.format(mTime.getTime()),
                        mCenterY - DATE_OFFSET - (mConfig_IsRound ? 10.0f : -25.0f * mScaleValue),
                        DATE_SIZE,
                        DATE_TEXT_STROKE_WIDTH);
            }
        }

        private void drawBorder(Canvas canvas){
            if (mConfig_ShowShadow) {
                mMatrix.reset();
                mMatrix.postScale(mScaleValue, mScaleValue);
                canvas.drawBitmap(mBackgroundBorder, mMatrix,
                        mBitmapPaint);
            }
        }

        @Override
        protected void drawFace(Canvas canvas) {
            float minRot  = 6.0f * mTime.get(Calendar.MINUTE);
            float secRot  = 6.0f * mTime.get(Calendar.SECOND);
            float hourRot  = 30.0f * mTime.get(Calendar.HOUR) + 0.5f * mTime.get(Calendar.MINUTE);

            setHandsMaterial();
            if (mConfig_ShowHours)
                drawHourHand(canvas, hourRot);
            drawMinuteHand(canvas, minRot);
            drawSecondHand(canvas, secRot);
        }

        @Override
        protected void drawAmbient(Canvas canvas) {
            String hour;
            String minute;
            if (mConfig_RomanAmbient){
                hour = MaterialWatchFaceUtils.getRomanDigit(mTime.get(Calendar.HOUR));
                minute = MaterialWatchFaceUtils.getRomanDigit(mTime.get(Calendar.MINUTE));
            }
            else{
                hour =  new SimpleDateFormat("HH").format(mTime.getTime());
                minute = new SimpleDateFormat("mm").format(mTime.getTime());
            }
            drawText(canvas,
                    hour,
                    -OFFSET,
                    AMBIENT_HOUR_SIZE,
                    AMBIENT_TEXT_STROKE_WIDTH);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawLine(mCenterX / 3, mCenterY, mCenterX * 2 - mCenterX / 3, mCenterY, paint);

            drawText(canvas,
                    minute,
                    OFFSET,
                    AMBIENT_MINUTE_SIZE,
                    AMBIENT_TEXT_STROKE_WIDTH);
        }

        @Override
        protected void setBg() {
            if (mHourCurrent == mTime.get(Calendar.HOUR) && mBackgroundBitmap != null) {
                return;
            }
            mHourCurrent = mTime.get(Calendar.HOUR);
            mBackgroundBorder = BitmapFactory.decodeResource(getResources(), (mConfig_IsRound)?R.drawable.bg_circle:R.drawable.bg_square);
            mBackgroundBitmap = ((BitmapDrawable) ((mConfig_Italian)? getBackgroundItalic() : getBackground())).getBitmap();
            mScaleValue = (mCenterX * 2.0f) / mBackgroundBitmap.getWidth();
        }

        private void setHandsMaterial() {
            if (mSecondScaledBitmap == null) {
                mSecondScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_second_hand);
            }
            if (mMinuteScaledBitmap == null) {
                mMinuteScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_minute_hand);
            }

            if (mHourScaledBitmap == null) {
                mHourScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_hour_hand);
            }
            if (mSecondShadowScaledBitmap == null) {
                mSecondShadowScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_second_hand_shadow);
            }
            if (mMinuteShadowScaledBitmap == null) {
                mMinuteShadowScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_minute_hand_shadow);
            }

            if (mHourShadowScaledBitmap == null) {
                mHourShadowScaledBitmap = (BitmapDrawable) mResources.getDrawable(R.drawable.material_hour_hand_shadow);
            }
        }

        private Drawable getBackground() {
            if (isInAmbientMode()) {
                return mResources.getDrawable(R.drawable.bg);
            }
            switch (mHourCurrent) {
                case 0:
                    return mResources.getDrawable(R.drawable.bg_material_twelfth_hour);
                case 1:
                    return mResources.getDrawable(R.drawable.bg_material_first_hour);
                case 2:
                    return mResources.getDrawable(R.drawable.bg_material_second_hour);
                case 3:
                    return mResources.getDrawable(R.drawable.bg_material_third_hour);
                case 4:
                    return mResources.getDrawable(R.drawable.bg_material_fourth_hour);
                case 5:
                    return mResources.getDrawable(R.drawable.bg_material_fifth_hour);
                case 6:
                    return mResources.getDrawable(R.drawable.bg_material_sixth_hour);
                case 7:
                    return mResources.getDrawable(R.drawable.bg_material_seventh_hour);
                case 8:
                    return mResources.getDrawable(R.drawable.bg_material_eighth_hour);
                case 9:
                    return mResources.getDrawable(R.drawable.bg_material_ninth_hour);
                case 10:
                    return mResources.getDrawable(R.drawable.bg_material_tenth_hour);
                case 11:
                    return mResources.getDrawable(R.drawable.bg_material_eleventh_hour);

                default:
                    return mResources.getDrawable(R.drawable.bg);
            }
        }

        private Drawable getBackgroundItalic() {
            if (isInAmbientMode()) {
                return mResources.getDrawable(R.drawable.bg);
            }
            switch (mHourCurrent) {
                case 0:
                    return mResources.getDrawable(R.drawable.bg_material_italic_twelfth_hour);
                case 1:
                    return mResources.getDrawable(R.drawable.bg_material_italic_first_hour);
                case 2:
                    return mResources.getDrawable(R.drawable.bg_material_italic_second_hour);
                case 3:
                    return mResources.getDrawable(R.drawable.bg_material_italic_third_hour);
                case 4:
                    return mResources.getDrawable(R.drawable.bg_material_italic_fourth_hour);
                case 5:
                    return mResources.getDrawable(R.drawable.bg_material_italic_fifth_hour);
                case 6:
                    return mResources.getDrawable(R.drawable.bg_material_italic_sixth_hour);
                case 7:
                    return mResources.getDrawable(R.drawable.bg_material_italic_seventh_hour);
                case 8:
                    return mResources.getDrawable(R.drawable.bg_material_italic_eighth_hour);
                case 9:
                    return mResources.getDrawable(R.drawable.bg_material_italic_ninth_hour);
                case 10:
                    return mResources.getDrawable(R.drawable.bg_material_italic_tenth_hour);
                case 11:
                    return mResources.getDrawable(R.drawable.bg_material_italic_eleventh_hour);

                default:
                    return mResources.getDrawable(R.drawable.bg);
            }
        }

        private void drawText(Canvas canvas, String value, float offset, float textSize, float strokeWidth){
            Rect bounds = new Rect();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (strokeWidth != 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(strokeWidth * mScaleValue);
            }
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize * mScaleValue);

            paint.getTextBounds(value, 0, value.length(), bounds);
            float textWidth = paint.measureText(value);
            float textHeight = bounds.bottom - bounds.top;
            if (offset > 0){
                offset += textHeight;
            }
            canvas.drawText(value, mCenterX - textWidth / 2.0f, mCenterY + offset * mScaleValue, paint);
        }

        private void drawHourHand(Canvas canvas, float minRot) {
            if (mConfig_ShowShadow){
                drawHand(canvas, mHourShadowScaledBitmap, minRot, SHADOW_OFFSET);
            }
            drawHand(canvas, mHourScaledBitmap, minRot, 0);
        }

        private void drawMinuteHand(Canvas canvas, float minRot) {
            if (mConfig_ShowShadow){
                drawHand(canvas, mMinuteShadowScaledBitmap, minRot, SHADOW_OFFSET);
            }
            drawHand(canvas, mMinuteScaledBitmap, minRot, 0);
        }

        private void drawSecondHand(Canvas canvas, float secRot) {
            if (mConfig_ShowShadow){
                drawHand(canvas, mSecondShadowScaledBitmap, secRot, SHADOW_OFFSET);
            }
            drawHand(canvas, mSecondScaledBitmap, secRot, 0);
        }

        private void drawHand(Canvas canvas, BitmapDrawable source, float angle, float y_offset) {
            mMatrix.reset();
            float widthSecond = source.getBitmap().getWidth() * mScaleValue;
            float heightSecond = source.getBitmap().getHeight() * mScaleValue;
            mMatrix.postScale(mScaleValue, mScaleValue);
            mMatrix.postRotate(angle, widthSecond / 2.0f, heightSecond / 2.0f);
            mMatrix.postTranslate(mCenterX - widthSecond / 2.0f, + y_offset);

            canvas.drawBitmap(source.getBitmap(),
                    mMatrix,
                    new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        }

        //region Update Ui from Settings
        private void updateConfigDataItemAndUiOnStartup() {
            WatchFaceUtil.fetchConfigDataMap(mGoogleApiClient,
                    WatchConstants.PATH_WITH_FEATURE_MATERIAL,
                    new WatchFaceUtil.FetchConfigDataMapCallback() {
                        @Override
                        public void onConfigDataMapFetched(DataMap startupConfig) {
                            // If the DataItem hasn't been created yet or some keys are missing,
                            // use the default values.
                            MaterialWatchFaceUtils.setDefaultValuesForMissingConfigKeys(startupConfig);
                            WatchFaceUtil.putConfigDataItem(mGoogleApiClient,
                                    WatchConstants.PATH_WITH_FEATURE_MATERIAL,
                                    startupConfig,
                                    null);

                            updateUiForConfigDataMap(startupConfig);
                        }
                    }
            );
        }

        private void updateUiForConfigDataMap(final DataMap config) {
            boolean uiUpdated = false;
            for (String configKey : config.keySet()) {
                if (!config.containsKey(configKey)) {
                    continue;
                }
                boolean show= config.getBoolean(configKey);
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Found watch face config key: " + configKey + " -> "
                            + show);
                }
                if (updateUiForKey(configKey, show)) {
                    uiUpdated = true;
                }
            }
            if (uiUpdated) {
                invalidate();
            }
        }

        private boolean updateUiForKey(String configKey, boolean show) {
            switch (configKey){
                case WatchConstants.KEY_BACKGROUND_STYLE_ITALIC:
                    mHourCurrent = -1;
                    mConfig_Italian = show;
                    break;
                case WatchConstants.KEY_DATE_SHOW:
                    mConfig_ShowDate = show;
                    break;
                case WatchConstants.KEY_HOURS_SHOW:
                    mConfig_ShowHours = show;
                    break;
                case WatchConstants.KEY_SHADOW_SHOW:
                    mConfig_ShowShadow = show;
                    break;
                case WatchConstants.KEY_ROMAN_SHOW:
                    mConfig_RomanAmbient = show;
                    break;
                case WatchConstants.KEY_NIGHT_MODE:
                    mConfig_NightMode = show;
                    break;
                default:
                    return false;
            }
            return true;
        }

        //endregion
    }
}
