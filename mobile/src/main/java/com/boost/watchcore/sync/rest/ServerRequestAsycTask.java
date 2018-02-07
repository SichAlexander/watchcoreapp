package com.boost.watchcore.sync.rest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import com.boost.watchcore.backend.model.watchApi.WatchApi;
import com.boost.watchcore.backend.model.watchApi.model.SyncObject;
import com.boost.watchcore.backend.model.watchApi.model.Watch;
import com.boost.watchcore.db.WatchContract;
import com.boost.watchcore.utils.Constant;
import com.boost.watchcore.utils.ModelToDataConverter;
import com.boost.watchcore.utils.NotificationGenerator;
import com.boost.watchcore.utils.Pref;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by BruSD on 28.04.2015.
 */
public class ServerRequestAsycTask extends AsyncTask<Void,Void, Boolean> {


    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private static WatchApi watchApi = null;
    private String url = Constant.SERVER_URL + "_ah/api/";
    private NotificationGenerator notificationGenerator;
    private final String ORDER_BY_TIME_STAMP = WatchContract.WatchEntry.COLUMN_WATCH_TIME_STAMP + " DESC";

    public ServerRequestAsycTask(Context context){
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (watchApi == null) {  // Only do this once
            WatchApi.Builder builder = new WatchApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl(url)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });


            watchApi = builder.build();
        }
        try {
            SyncObject syncObject;
            Log.v("syncTime", DateFormat.format("MM/dd/yyyy hh:mm:ss", Pref.getEndTimeStamp(mContext)).toString());
            if (Pref.getEndTimeStamp(mContext) == 0) {
                syncObject = watchApi.getWatchFirstTime().execute();
                Log.v(TAG, syncObject.toString());
            } else {
                long lastUpdate = Pref.getEndTimeStamp(mContext) + 1;
                syncObject = watchApi.tryUpdate(lastUpdate).execute();
                Log.v(TAG, syncObject.toString());
            }


            if (syncObject.getWatches() != null) {
                addWatchToDB((ArrayList<Watch>) syncObject.getWatches());
            }




        } catch (IOException e) {
            return false;
        }
        return false;
    }



    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);


    }

    private void addWatchToDB(ArrayList<Watch> watches) {
        if (watches.size() != 0) {
            Vector<ContentValues> cVVector = new Vector<ContentValues>(watches.size());


            for (Watch watch : watches) {
                ContentValues themeValues = ModelToDataConverter.convertWatchToCV(watch);
                cVVector.add(themeValues);
            }

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int rowsInserted = mContext.getContentResolver()
                        .bulkInsert(WatchContract.WatchEntry.CONTENT_URI, cvArray);
                Log.v(TAG, "Theme inserted " + rowsInserted + " rows of weather data");

                // Use a DEBUG variable to gate whether or not you do this, so you can easily
                // turn it on and off, and so that it's easy to see what you can rip out if
                // you ever want to remove it.
                if (Constant.DEBUG) {
                    Cursor watchCursor = mContext.getContentResolver().query(
                            WatchContract.WatchEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            ORDER_BY_TIME_STAMP
                    );

                    if (watchCursor.moveToFirst()) {
                        Pref.setStartTimeStamp(mContext, Pref.getEndTimeStamp(mContext));
                        Pref.setEndTimeStamp(mContext, watchCursor.getLong(watchCursor.getColumnIndex(WatchContract.WatchEntry.COLUMN_WATCH_TIME_STAMP)));
                        while (watchCursor.moveToNext()) {
                            ContentValues resultValues = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(watchCursor, resultValues);
                            Log.v(TAG, "Query succeeded! **********");
                            for (String key : resultValues.keySet()) {
                                Log.v(TAG, key + ": " + resultValues.getAsString(key));
                            }
                        }
                        String selectionRule = WatchContract.WatchEntry.COLUMN_WATCH_TIME_STAMP + " > ?";
                        String[] args = new String[] { String.valueOf(Pref.getStartTimeStamp(mContext)+1)};
                        Cursor watchC = mContext.getContentResolver().query(
                                WatchContract.WatchEntry.CONTENT_URI,
                                null,
                                selectionRule,
                                args,
                                null );
                        int countNewWatch = watchC.getCount();
                        notificationGenerator = NotificationGenerator.newInstance();
                        notificationGenerator.showNotification(mContext, countNewWatch);
                    } else {
                        Log.v(TAG, "Query failed! :( **********");
                    }
                }
            }
        }
    }
}
