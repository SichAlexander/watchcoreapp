package com.boost.watchcore.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by BruSD on 28.04.2015.
 */
public class WatchContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.boost.watchcore";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WATCH = "watch";


    public static final class WatchEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WATCH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WATCH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WATCH;

        // Table name
        public static final String TABLE_NAME = "QUOTE";

        public static final String COLUMN_WATCH_ID = _ID;
        public static final String COLUMN_WATCH_NAME = "WATCH_NAME";
        public static final String COLUMN_WATCH_ICON = "WATCH_ICON";
        public static final String COLUMN_WATCH_IMAGE_FOLDER_LINK = "WATCH_IMAGE_FOLDER_LINK";
        public static final String COLUMN_WATCH_DESCRIPTION = "WATCH_DESCRIPTION";
        public static final String COLUMN_WATCH_TIME_STAMP = "WATCH_TIME_STAMP";
        public static final String COLUMN_WATCH_PACKAGE_NAME = "WATCH_PACKAGE_NAME";
        public static final String COLUMN_WATCH_ACTION_NAME = "COLUMN_WATCH_ACTION_NAME";
        public static final String COLUMN_WATCH_IS_FREE = "WATCH_IS_FREE";

        public static Uri buildQuoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
