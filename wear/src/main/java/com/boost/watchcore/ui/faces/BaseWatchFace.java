package com.boost.watchcore.ui.faces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.boost.watchcore.WatchConstants;

import java.util.Calendar;

/**
 * Created by Alex_Jobs on 27.05.2015.
 */
public abstract class BaseWatchFace extends CanvasWatchFaceService {
    private static final String TAG = "BaseWatchFace";


    protected String getBatteryInfoWatch()
    {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus =  registerReceiver(null, iFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return String.valueOf(Math.round(level));
    }

    public class EngineBaseWatchFace extends CanvasWatchFaceService.Engine {

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime = Calendar.getInstance();
                invalidate();
            }
        };

        protected  final int MSG_UPDATE_TIME = 0;
        protected float mCenterX;
        protected float mCenterY;
        protected Matrix mMatrix;
        protected float mScaleValue = 1.0f;
        protected Calendar mTime;
        protected boolean mRegisteredTimeZoneReceiver = false;
        protected Resources mResources;
        protected Paint mBitmapPaint;
        protected Bitmap mBackgroundBitmap;
        protected long mConfig_UpdateTime = 100;
        protected boolean mConfig_IsRound = false;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(BaseWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            initResources();
            initVars();
        }

        private void initResources(){
            mResources = getApplicationContext().getResources();
            mMatrix = new Matrix();
            mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
        protected void initVars(){
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        /**
         * Need to call super.onAmbientModeChanged(inAmbientMode)
         *  Need to call super.onAmbientModeChanged(inAmbientMode) to set Background
         *  and draw new WatchFace tick
         * @param inAmbientMode
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mBackgroundBitmap = null;
            invalidate();
            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mConfig_IsRound = insets.isRound();

        }

        final private Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (mConfig_UpdateTime == 0) {
                    mConfig_UpdateTime = WatchConstants.UPDATE_MAX;
                }
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = mConfig_UpdateTime
                                    - (timeMs % mConfig_UpdateTime);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver(getApplicationContext());
            } else {
                unregisterReceiver(getApplicationContext());
            }
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mTime = Calendar.getInstance();
            mCenterX = bounds.width() / 2.0f;
            mCenterY = bounds.height() / 2.0f;
            drawWatch(canvas);
        }


        protected void drawWatch(Canvas canvas) {
            drawBg(canvas);
            if (isInAmbientMode()) {
                drawAmbient(canvas);
            } else {
                drawFace(canvas);
            }
        }

        protected void drawFace(Canvas canvas) {
        }

        protected void drawAmbient(Canvas canvas) {
        }

        /**
         * Need to call super.drawBg(canvas) to set Background
         * @param canvas
         */
        protected void drawBg(Canvas canvas) {
            setBg();
        }

        protected void setBg() {
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void registerReceiver(Context context) {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            context.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver(Context context) {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            context.unregisterReceiver(mTimeZoneReceiver);
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }
    }
}
