package com.boost.watchcore.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.boost.watchcore.R;
import com.boost.watchcore.db.WatchContract;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by BruSD on 29.04.2015.
 */
public class WatchAdapter extends RecyclerView.Adapter<WatchAdapter.ViewHolder>  {
    CursorAdapter mCursorAdapter;
    Context mContext;


    private ImageLoader mImageLoader;

    private boolean islockedAnimations                  = false;
    private static int mLastAnimatedItem                = -1;

    private static final Interpolator INTERPOLATOR      = new DecelerateInterpolator();
    private static final int MAX_PHOTO_ANIMATION_DELAY  = 500;
    private long profileHeaderAnimationStartTime        = 0;
    private static final String FREE_ITEM               = "1";

    public WatchAdapter(Context context, final Cursor c) {
        mContext = context;
        initImageLoader(context);
        mLastAnimatedItem = c.getCount() - 1;
        mCursorAdapter = new CursorAdapter(mContext, c, 0) {
            private ImageLoadingListener imageLoadingListener;

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.watch_list_item, parent, false);
                return v;
            }



            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ItemHolder holder = null;
                if (view.getTag() == null) {
                    holder = new ItemHolder(view);
                    view.setTag(holder);
                } else {
                    holder = (ItemHolder) view.getTag();
                }
                holder.name.setText(cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_NAME)));
                mImageLoader.displayImage(cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_ICON)), holder.image);
                mImageLoader.setDefaultLoadingListener(imageLoadingListener);
                holder.ico.setImageResource(getIcon(cursor));
                holder.imagePlay.setImageResource(getImagePlay(cursor));
            }
        };
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(R.mipmap.ic_placeholder)
                .showImageOnFail(R.mipmap.ic_placeholder)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .build();
            mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(config);
        }
    }


    public Cursor getCursor(){
        return mCursorAdapter.getCursor();
    }
    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position);
        animatePhoto(holder);
        mCursorAdapter.bindView(holder.llRoot, mContext, mCursorAdapter.getCursor());
        if (mLastAnimatedItem < position + 1) mLastAnimatedItem = position;


    }

    /**
     * Do animation for every item just once.
     * @param holder
     */
    private void animatePhoto(ViewHolder holder) {
        if (!islockedAnimations()) {
            if (mLastAnimatedItem == holder.getPosition()) {
                setIslockedAnimations(true);
            }
            long animationDelay = profileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (profileHeaderAnimationStartTime == 0) {
                animationDelay = holder.getPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = holder.getPosition() * 30;
            } else {
                animationDelay += holder.getPosition() * 30;
            }
            holder.llRoot.setScaleY(0);
            holder.llRoot.setScaleX(0);
            holder.llRoot.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }

    }

    public int getIcon(Cursor cursor) {
        String packageAction = cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_ACTION_NAME));
        PackageManager packageManager = mContext.getPackageManager();
        if (packageAction == null || packageAction.isEmpty())
            return R.drawable.bg;

        Intent intent = new Intent();
        intent.setAction(packageAction);
        if (packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0)
            return R.drawable.bg;
        return R.drawable.ic_settings;
    }

    private int getImagePlay(Cursor cursor){
        String packageName = cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_PACKAGE_NAME));
        String packageAction = cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_ACTION_NAME));
        String isFree = cursor.getString(cursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_IS_FREE));
        PackageManager packageManager = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            Log.d("cs_cs", packageName + " cs_cs " + packageInfo.activities.length);
        } catch (Exception e) {
            if (isFree.equals(FREE_ITEM)){
                return R.mipmap.ic_google_play;
            }else {
                return R.mipmap.ic_buy_in_google_play;
            }
        }
        if (packageAction == null || packageAction.isEmpty())
            return R.drawable.bg;

        Intent intent = new Intent();
        intent.setAction(packageAction);
        if (packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0)
            return R.drawable.bg;
        return R.drawable.bg;

    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView themeTextView;
        public RelativeLayout llRoot;

        public ViewHolder(View v) {
            super(v);
            themeTextView = (TextView) v.findViewById(R.id.name_TextView_WatchListItem);
            llRoot = (RelativeLayout) v.findViewById(R.id.root_ll_WatchListItem);
        }
    }


    public boolean islockedAnimations() {
        return islockedAnimations;
    }

    public void setIslockedAnimations(boolean islockedAnimations) {
        this.islockedAnimations = islockedAnimations;
        Log.d("Animation locked: ", islockedAnimations + " ");
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemHolder {
        // each data item is just a string in this case
        public TextView name;
        public ImageView image;
        public ImageView ico;
        public ImageView imagePlay;

        public ItemHolder(View v) {
            name = (TextView) v.findViewById(R.id.name_TextView_WatchListItem);
            image = (ImageView) v.findViewById(R.id.watch_image_view);
            ico = (ImageView) v.findViewById(R.id.icon_ImageView_WatchListItem);
            imagePlay = (ImageView) v.findViewById(R.id.imagePlay_rl_WatchListItem);
        }
    }
}
