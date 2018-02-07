package com.boost.watchcore.ui.fragment;

import android.app.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.boost.watchcore.Application;
import com.boost.watchcore.R;
import com.boost.watchcore.db.WatchContract.WatchEntry;
import com.boost.watchcore.ui.adapters.WatchAdapter;
import com.boost.watchcore.utils.Constant;
import com.boost.watchcore.utils.Pref;
import com.boost.watchcore.utils.RecyclerItemClickListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by BruSD on 29.04.2015.
 */
public class WatchListFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private Loader<Cursor> mLoader;
    private static final String PARAM_IS_FREE          = "param_is_free";
    private Activity mParentActivity;
    private int WATCH_LOADER = 1;
    private View rootView;
    private RecyclerView mRecyclerView;
    private WatchAdapter mAdapter;
    private static final String POSITION_KEY = "POSITION_KEY";
    private int mSelectedPosition = -1;
    private String mPeerId;
    private WatchChangeListener mWatchChangeListener = new WatchChangeListener();

    private static final String[] WATCH_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WatchEntry.COLUMN_WATCH_ID,
            WatchEntry.COLUMN_WATCH_NAME,
            WatchEntry.COLUMN_WATCH_ICON,
            WatchEntry.COLUMN_WATCH_IMAGE_FOLDER_LINK,
            WatchEntry.COLUMN_WATCH_DESCRIPTION,
            WatchEntry.COLUMN_WATCH_TIME_STAMP,
            WatchEntry.COLUMN_WATCH_PACKAGE_NAME,
            WatchEntry.COLUMN_WATCH_ACTION_NAME,
            WatchEntry.COLUMN_WATCH_IS_FREE};

    private String sortOrder = WatchEntry.COLUMN_WATCH_TIME_STAMP + " DESC";

    public static WatchListFragment newInstance(final int isFree, final String perId) {
        WatchListFragment fragment = new WatchListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_IS_FREE, isFree);
        bundle.putString(Constant.BUNDLE_WATCH_ID, perId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentActivity = getActivity();
        rootView = inflater.inflate(R.layout.fragment_watch_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.watch_RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        if (getArguments()!=null && getArguments().containsKey(Constant.BUNDLE_WATCH_ID))
            mPeerId = getArguments().getString(Constant.BUNDLE_WATCH_ID);

        // use a linear layout manager
        int columns = 2;
        if (!getResources().getBoolean(R.bool.is_phone))
            columns = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(mParentActivity, columns));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mParentActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Cursor cursor = mAdapter.getCursor();
                        cursor.moveToPosition(position);
                        showWatchDialog(cursor);
                        mSelectedPosition = position;
                        trackEventItemClick();
                    }
                })
        );
        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mSelectedPosition = savedInstanceState.getInt(POSITION_KEY);
        }
        trackScreen();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader = getLoaderManager().initLoader(WATCH_LOADER, null, this);
        mLoader.startLoading();
        registerWatchReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWatchChangeListener != null){
            mParentActivity.unregisterReceiver(mWatchChangeListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedPosition != -1)
            outState.putInt(POSITION_KEY, mSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    private void showWatchDialog(Cursor cursor) {
        final String settingsAction = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_ACTION_NAME));
        final String packageName = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_PACKAGE_NAME));
        final String watchName = cursor.getString(cursor.getColumnIndex(WatchEntry.COLUMN_WATCH_NAME));
        final String aboutClassName = packageName + ".about." + watchName.replace(" ","");

        PackageManager packageManager = mParentActivity.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            Log.d("cs_cs", packageName + " cs_cs " + packageInfo.activities.length);
        } catch (Exception e) {
            openInGooglePlay(packageName);
            return;
        }
        final Dialog dialogAction = new Dialog(getActivity());
        dialogAction.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_watch_action, null);
        dialogAction.setContentView(dialogView);
        View settingsBtn = dialogView.findViewById(R.id.settings_Button_dialog_watch_action);
        if (!checkExistSettings(packageManager,settingsAction)){
            settingsBtn.setVisibility(View.GONE);
        }
        else{
            settingsBtn.setVisibility(View.VISIBLE);
            settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSettings(settingsAction);
                    dialogAction.dismiss();
                }
            });
        }

        View aboutBtn = dialogView.findViewById(R.id.about_Button_dialog_watch_action);

        if (!checkExistAbout(packageManager, aboutClassName)){
            aboutBtn.setVisibility(View.GONE);
        }
        else{
            if (settingsBtn.getVisibility()!=View.VISIBLE){
                openAbout(aboutClassName);
                return;
            }

            aboutBtn.setVisibility(View.VISIBLE);
            aboutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAbout(aboutClassName);
                    dialogAction.dismiss();
                }
            });
        }
        if (aboutBtn.getVisibility() == View.VISIBLE || settingsBtn.getVisibility() == View.VISIBLE )
            dialogAction.show();
    }

    private void openInGooglePlay(String packageName){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_url) + packageName));
        startActivity(browserIntent);
    }

    private boolean checkExistSettings(PackageManager packageManager, String settingsAction){
        return (settingsAction != null)
                && (packageManager.queryIntentActivities(new Intent(settingsAction), PackageManager.MATCH_DEFAULT_ONLY).size() > 0);
    }

    private void openSettings(String settingsAction){
        if (mPeerId == null || mPeerId.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.title_no_device_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(settingsAction);
        intent.putExtra(WatchFaceCompanion.EXTRA_PEER_ID, mPeerId);
        startActivity(intent);
    }

    //Start about activity via action filter
    private boolean checkExistAbout(PackageManager packageManager, String className){
        return (className != null)
                && (packageManager.queryIntentActivities(new Intent(className), PackageManager.MATCH_DEFAULT_ONLY).size() > 0);
    }

    private void openAbout(String className){
        Intent intent = new Intent(className);
        startActivity(intent);
    }

    private void registerWatchReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_CHANGED_WATCHES);
        mParentActivity.registerReceiver(mWatchChangeListener, filter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String getFreeQuery = null;
        if (getArguments() != null && getArguments().containsKey(PARAM_IS_FREE)){
            getFreeQuery = WatchEntry.COLUMN_WATCH_IS_FREE + " = " + getArguments().getInt(PARAM_IS_FREE);
        }
        Log.d("DoQuery", "" + getFreeQuery);

        return new CursorLoader(
                mParentActivity,
                WatchEntry.CONTENT_URI,
                WATCH_COLUMNS,
                getFreeQuery,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            rootView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            mAdapter = new WatchAdapter(mParentActivity, data);
            mRecyclerView.setAdapter(mAdapter);
            if (mSelectedPosition != -1 && data.getCount() != 0 && mSelectedPosition <= data.getCount()){
                mRecyclerView.scrollToPosition(mSelectedPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class WatchChangeListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constant.BUNDLE_WATCH_ID))
                mPeerId = intent.getStringExtra(Constant.BUNDLE_WATCH_ID);
        }
    }

    private void trackScreen() {
        ((Application)getActivity().getApplication()).trackScreen(getString(R.string.analytics_fragment_list));
    }

    private void trackEventItemClick(){
       ((Application)getActivity().getApplication()).trackEvent("Watch list", "event", "click", "Watch selected");
    }
}
