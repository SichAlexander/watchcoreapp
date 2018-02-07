
package com.boost.watchcore;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;


public final class WatchFaceUtil {
    private static final String TAG = "WatchFaceUtil";

    public static void overwriteKeysInConfigDataMap(final GoogleApiClient googleApiClient,
                                                    final String pathWithFeature,
                                                    final DataMap configKeysToOverwrite,
                                                    final ResultCallback<DataApi.DataItemResult> resultCallback) {

        fetchConfigDataMap(googleApiClient,
                pathWithFeature,
                new FetchConfigDataMapCallback() {
                    @Override
                    public void onConfigDataMapFetched(DataMap currentConfig) {
                        DataMap overwrittenConfig = new DataMap();
                        overwrittenConfig.putAll(currentConfig);
                        overwrittenConfig.putAll(configKeysToOverwrite);
                        putConfigDataItem(googleApiClient,
                                pathWithFeature,
                                overwrittenConfig,
                                resultCallback);
                    }
                }
        );
    }

    public static void fetchConfigDataMap(final GoogleApiClient client,
                                           final String pathWithFeature,
                                          final FetchConfigDataMapCallback callback) {
        Wearable.NodeApi.getLocalNode(client).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        String localNode = getLocalNodeResult.getNode().getId();
                        Uri uri = new Uri.Builder()
                                .scheme("wear")
                                .path(pathWithFeature)
                                .authority(localNode)
                                .build();
                        Wearable.DataApi.getDataItem(client, uri)
                                .setResultCallback(new DataItemResultCallback(callback));
                    }
                }
        );
    }

    public static void putConfigDataItem(GoogleApiClient googleApiClient,
                                          String pathWithFeature,
                                          DataMap newConfig,
                                          final ResultCallback<DataApi.DataItemResult> resultCallback) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(pathWithFeature);
        DataMap configToPut = putDataMapRequest.getDataMap();
        configToPut.putAll(newConfig);
        if (resultCallback != null){
            Wearable.DataApi.putDataItem(googleApiClient, putDataMapRequest.asPutDataRequest())
                    .setResultCallback(resultCallback);
        }
        else{
            Wearable.DataApi.putDataItem(googleApiClient, putDataMapRequest.asPutDataRequest());
        }
    }

    public static void sendStartAboutMessage(final GoogleApiClient googleApiClient,
                                             final String aboutAction) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if(getConnectedNodesResult.getNodes() == null)
                    return;
                for(Node node : getConnectedNodesResult.getNodes()){
                    Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
                                WatchConstants.START_ACTIVITY_PATH, aboutAction.getBytes());
                }
            }
        });
    }

    private static class DataItemResultCallback implements ResultCallback<DataApi.DataItemResult> {

        private final FetchConfigDataMapCallback mCallback;

        public DataItemResultCallback(FetchConfigDataMapCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onResult(DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess()) {
                if (dataItemResult.getDataItem() != null) {
                    DataItem configDataItem = dataItemResult.getDataItem();
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
                    DataMap config = dataMapItem.getDataMap();
                    mCallback.onConfigDataMapFetched(config);
                } else {
                    mCallback.onConfigDataMapFetched(new DataMap());
                }
            }
        }
    }

    public interface FetchConfigDataMapCallback {
        void onConfigDataMapFetched(DataMap config);
    }
}
