package com.boost.watchcore.backend.dstore;

import com.boost.watchcore.backend.model.Watch;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by BruSD on 22.04.2015.
 */
public class DataStoreHelper {
    private static DatastoreService datastore;


    private static Date today;
    private static Date todayForUpdate;
    private static ArrayList<Watch> watches = new ArrayList<>();

    public static ArrayList<Watch> getWatchesFirstSync() {
        if (today == null) {
            today = new Date();
        }
        Date date = new Date();

        if (today.getDate() != date.getDate()) {
            today = new Date();
            watches = getWatches(today);
        } else {
            if (watches.size() != 0) {
                return watches;
            } else {
                today = new Date();
                watches = getWatches(today);
            }
        }

        return watches;
    }

    private static ArrayList<Watch> getWatches(Date today) {
        if (datastore == null) {
            datastore = DatastoreServiceFactory.getDatastoreService();
        }
        ArrayList<Watch> watchArrayList = new ArrayList<>();

        PreparedQuery pq = getWatchQuery(today);
        for (Entity result : pq.asIterable()) {
            Watch watch = generateWatchObject(result);
            watchArrayList.add(watch);
        }
        return watchArrayList;
    }


    private static PreparedQuery getWatchQuery(Date today) {
        Query q;
        Query.Filter dateUpdateFilter = new Query.FilterPredicate(Field.WATCH_TIME_STAMP,
                Query.FilterOperator.LESS_THAN_OR_EQUAL,
                today);

        q = new Query(Field.WATCH).setFilter(dateUpdateFilter);

        return datastore.prepare(q);
    }

    private static Watch generateWatchObject(Entity result) {

        Watch watch = new Watch();
        long id = result.getKey().getId();
        watch.setId(id);

        String name = (String) result.getProperty(Field.WATCH_NAME);
        watch.setName(name);

        String iconURl  = (String) result.getProperty(Field.WATCH_ICON);
        watch.setIconeURL(iconURl);

        String imageFolderURL = (String) result.getProperty(Field.WATCH_IMAGE_FOLDER_LINK);
        watch.setImageFolderURL(imageFolderURL);

        String discription = (String) result.getProperty(Field.WATCH_DESCRIPTION);
        watch.setDescription(discription);

        Date date = (Date) result.getProperty(Field.WATCH_TIME_STAMP);
        watch.setTimeStamp(date.getTime());

        String packageName = (String) result.getProperty(Field.WATCH_PACKAGE_NAME);
        watch.setPackageName(packageName);

        String actionName = (String) result.getProperty(Field.WATCH_ACTION_NAME);
        watch.setActionName(actionName);

        boolean isFree = (Boolean)result.getProperty(Field.WATCH_IS_FREE);
        watch.setIsFree(isFree);



        return watch;


    }


    public static ArrayList<Watch> getLastUpdatedWathces(long lastUdate) {
        todayForUpdate = new Date();
        Date date = new Date();
        date.setTime(lastUdate);
        ArrayList<Watch> lastUpdateWatch = new ArrayList<>();
        lastUpdateWatch = getWatchByTime(lastUdate);

        return lastUpdateWatch;
    }

    private static ArrayList<Watch> getWatchByTime(long timeStamp) {
        ArrayList<Watch> themeArrayList = new ArrayList<>();
        if (datastore == null) {
            datastore = DatastoreServiceFactory.getDatastoreService();
        }
        Query q;
        Query.Filter dateUpdateMaxFilter = new Query.FilterPredicate(Field.WATCH_TIME_STAMP,
                Query.FilterOperator.LESS_THAN_OR_EQUAL,
                todayForUpdate);

        Date lastUpate = new Date();
        lastUpate.setTime(timeStamp);
        Query.Filter dateUpdateMinFilter = new Query.FilterPredicate(Field.WATCH_TIME_STAMP,
                Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                lastUpate);
        Query.Filter dateRange =
                Query.CompositeFilterOperator.and(dateUpdateMinFilter, dateUpdateMaxFilter);

        q = new Query(Field.WATCH).setFilter(dateRange);

        PreparedQuery pq = datastore.prepare(q);
        for (Entity result : pq.asIterable()) {
            Watch watch = generateWatchObject(result);
            themeArrayList.add(watch);
        }
        return themeArrayList;
    }
}
