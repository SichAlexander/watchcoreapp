package com.boost.watchcore.utils;

import android.content.ContentValues;

import com.boost.watchcore.backend.model.watchApi.model.Watch;
import com.boost.watchcore.db.WatchContract.WatchEntry;

/**
 * Created by BruSD on 28.04.2015.
 */
public class ModelToDataConverter {
    public static ContentValues convertWatchToCV(Watch watch) {

        ContentValues watchValues = new ContentValues();
        watchValues.put(WatchEntry.COLUMN_WATCH_ID, watch.getId());
        watchValues.put(WatchEntry.COLUMN_WATCH_NAME, watch.getName());
        watchValues.put(WatchEntry.COLUMN_WATCH_ICON, watch.getIconeURL());
        watchValues.put(WatchEntry.COLUMN_WATCH_IMAGE_FOLDER_LINK, watch.getImageFolderURL());
        watchValues.put(WatchEntry.COLUMN_WATCH_DESCRIPTION, watch.getDescription());
        watchValues.put(WatchEntry.COLUMN_WATCH_TIME_STAMP, watch.getTimeStamp());
        watchValues.put(WatchEntry.COLUMN_WATCH_PACKAGE_NAME, watch.getPackageName());
        watchValues.put(WatchEntry.COLUMN_WATCH_ACTION_NAME, watch.getActionName());
        watchValues.put(WatchEntry.COLUMN_WATCH_IS_FREE, watch.getIsFree() ? 1 : 0);

        return watchValues;
    }
}
