package com.boost.watchcore.backend.dstore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.util.Date;

/**
 * Created by BruSD on 22.04.2015.
 */


@Deprecated
public class DevTemp {

    private static DatastoreService datastore;

    public static void createEntry() {
        datastore = DatastoreServiceFactory.getDatastoreService();



            Entity watch = generateEntity(1);
            datastore.put(watch);



    }

    private static Entity generateEntity(int id) {

        Entity watch = new Entity(Field.WATCH);
        watch.setProperty(Field.WATCH_NAME, id + "Watch");
        watch.setProperty(Field.WATCH_ICON, "");
        watch.setProperty(Field.WATCH_IMAGE_FOLDER_LINK, "");
        watch.setProperty(Field.WATCH_DESCRIPTION, "");
        watch.setProperty(Field.WATCH_PACKAGE_NAME, "");
        watch.setProperty(Field.WATCH_ACTION_NAME, "");
        watch.setProperty(Field.WATCH_IS_FREE, true);

        Date date = new Date();
        watch.setProperty(Field.WATCH_TIME_STAMP, date);




        return watch;
    }
}
