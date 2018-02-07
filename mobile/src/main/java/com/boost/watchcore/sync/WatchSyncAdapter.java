package com.boost.watchcore.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.boost.watchcore.sync.rest.ServerRequestAsycTask;

/**
 * Created by BruSD on 23.04.2015.
 */
public class WatchSyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private Context mContext;


    /**
     * Set up the sync adapter
     */
    public WatchSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */

        mContentResolver = mContext.getContentResolver();

    }



    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public WatchSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = mContext.getContentResolver();



    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
           Log.v("WatchFace", "TrySync");
        new ServerRequestAsycTask(mContext).execute();
//        TODO:Sync
    }
}
