package com.boost.watchcore.backend.model;

import com.boost.watchcore.backend.dstore.DataStoreHelper;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "watchApi",
        version = "v1",
        resource = "watch",
        namespace = @ApiNamespace(
                ownerDomain = "model.backend.watchcore.boost.com",
                ownerName = "model.backend.watchcore.boost.com",
                packagePath = ""
        )
)
public class WatchEndpoint {

    private static final Logger logger = Logger.getLogger(WatchEndpoint.class.getName());

    /**
     * This method gets the <code>Watch</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>Watch</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getWatch")
    public Watch getWatch(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getWatch method");
        return null;
    }

    /**
     * This inserts a new <code>Watch</code> object.
     *
     * @param watch The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertWatch")
    public Watch insertWatch(Watch watch) {
        // TODO: Implement this function
        logger.info("Calling insertWatch method");
        return watch;
    }

    @ApiMethod(name = "getWatchFirstTime")
    public SyncObject getWatchFirstTime() {
        SyncObject syncObject =  new SyncObject();
        syncObject.setWatches(DataStoreHelper.getWatchesFirstSync());

        return syncObject;
    }

    @ApiMethod (name = "tryUpdate")
    public SyncObject tryUpdate(@Named("lastUpdate")long lastUdate){
        SyncObject syncObject = new SyncObject();
        syncObject.setWatches(DataStoreHelper.getLastUpdatedWathces(lastUdate));

        return syncObject;

    }
}