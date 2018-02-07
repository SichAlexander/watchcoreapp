package com.boost.watchcore.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by njaka on 7/7/2015.
 */
public class AnimateLinearLayout extends LinearLayout {
    public AnimateLinearLayout(Context context) {
        super(context);
    }

    public AnimateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
