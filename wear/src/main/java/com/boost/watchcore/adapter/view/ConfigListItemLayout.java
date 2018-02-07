package com.boost.watchcore.adapter.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boost.watchcore.R;

/**
 * Created by Alex_Jobs on 28.04.2015.
 */
public class ConfigListItemLayout extends RelativeLayout
        implements WearableListView.OnCenterProximityListener {

    private TextView tvTitle;
    private CircledImageView ciCircle;
    private ImageView ivLogo;

    private float mExpandCircleRadius;
    private float mShrinkCircleRadius;

    private ObjectAnimator mExpandCircleAnimator;
    private ObjectAnimator mExpandLabelAnimator;
    private AnimatorSet mExpandAnimator;

    private ObjectAnimator mShrinkCircleAnimator;
    private ObjectAnimator mShrinkLabelAnimator;
    private AnimatorSet mShrinkAnimator;


    private static final int ANIMATION_DURATION_MS = 150;
    /** The ratio for the size of a circle in shrink state. */
    private static final float SHRINK_CIRCLE_RATIO = 0.75f;

    private static final float SHRINK_LABEL_ALPHA = 0.5f;
    private static final float EXPAND_LABEL_ALPHA = 1f;

    public ConfigListItemLayout(Context context) {
        super(context);
        View.inflate(context, R.layout.list_item_material_config, this);
        tvTitle = (TextView) findViewById(R.id.configName_TextView_MaterialConfig);
        ciCircle = (CircledImageView) findViewById(R.id.circle_CircledImageView_MaterialConfig);
        ivLogo = (ImageView) findViewById(R.id.logo_ImageView_MaterialConfig);
        initAnimation();
    }


    private void initAnimation(){
        mExpandCircleRadius = ciCircle.getCircleRadius();
        mShrinkCircleRadius = mExpandCircleRadius * SHRINK_CIRCLE_RATIO;
        mShrinkCircleAnimator = ObjectAnimator.ofFloat(ciCircle, "circleRadius",
                mExpandCircleRadius, mShrinkCircleRadius);
        mShrinkLabelAnimator = ObjectAnimator.ofFloat(tvTitle, "alpha",
                EXPAND_LABEL_ALPHA, SHRINK_LABEL_ALPHA);
        mShrinkAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
        mShrinkAnimator.playTogether(mShrinkCircleAnimator, mShrinkLabelAnimator);

        mExpandCircleAnimator = ObjectAnimator.ofFloat(ciCircle, "circleRadius",
                mShrinkCircleRadius, mExpandCircleRadius);
        mExpandLabelAnimator = ObjectAnimator.ofFloat(tvTitle, "alpha",
                SHRINK_LABEL_ALPHA, EXPAND_LABEL_ALPHA);
        mExpandAnimator = new AnimatorSet().setDuration(ANIMATION_DURATION_MS);
        mExpandAnimator.playTogether(mExpandCircleAnimator, mExpandLabelAnimator);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        if (animate) {
            mShrinkAnimator.cancel();
            if (!mExpandAnimator.isRunning()) {
                mExpandCircleAnimator.setFloatValues(ciCircle.getCircleRadius(), mExpandCircleRadius);
                mExpandLabelAnimator.setFloatValues(tvTitle.getAlpha(), EXPAND_LABEL_ALPHA);
                mExpandAnimator.start();
            }
        } else {
            mExpandAnimator.cancel();
            ciCircle.setCircleRadius(mExpandCircleRadius);
            tvTitle.setAlpha(EXPAND_LABEL_ALPHA);
        }
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        if (animate) {
            mExpandAnimator.cancel();
            if (!mShrinkAnimator.isRunning()) {
                mShrinkCircleAnimator.setFloatValues(ciCircle.getCircleRadius(), mShrinkCircleRadius);
                mShrinkLabelAnimator.setFloatValues(tvTitle.getAlpha(), SHRINK_LABEL_ALPHA);
                mShrinkAnimator.start();
            }
        } else {
            mShrinkAnimator.cancel();
            ciCircle.setCircleRadius(mShrinkCircleRadius);
            tvTitle.setAlpha(SHRINK_LABEL_ALPHA);
        }
    }

    public void setText(String text){
        tvTitle.setText(text);
    }

    public void setImageResource(int resource){
        ivLogo.setImageResource(resource);
    }

    public void setItemColor(int color){
        ciCircle.setCircleColor(color);
    }
}