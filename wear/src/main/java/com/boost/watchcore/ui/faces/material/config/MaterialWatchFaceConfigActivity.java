
package com.boost.watchcore.ui.faces.material.config;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.watchcore.ConfigurationBaseActivity;
import com.boost.watchcore.R;
import com.boost.watchcore.WatchConstants;
import com.boost.watchcore.WatchFaceUtil;
import com.boost.watchcore.adapter.ConfigMaterialAdapter;
import com.boost.watchcore.model.Property;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;

/**
 * The watch-side config activity for {@link com.boost.watchcore.ui.faces.material.MaterialWatchFace}, which allows for setting the
 * background color.
 */
public class MaterialWatchFaceConfigActivity extends ConfigurationBaseActivity {
    private static final String TAG = "MaterialConfigActivity";
   private WatchFaceUtil.FetchConfigDataMapCallback mFetchCurrConfig = new WatchFaceUtil.FetchConfigDataMapCallback() {
       @Override
       public void onConfigDataMapFetched(DataMap config) {
           fetchData(config);
       }
   } ;
    private  WearableListView.ClickListener mListSettingsClickListener = new WearableListView.ClickListener() {
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        int selectedPosition = viewHolder.getPosition();
        if (mProperties.size()!= 0 && selectedPosition < mProperties.size() && selectedPosition >=0) {
            Log.d("cs_c", " selected Key : " + mProperties.get(selectedPosition).getPropertyKey());
            String key = mProperties.get(selectedPosition).getPropertyKey();

            if (key.equals(WatchConstants.KEY_START_ABOUT)){
                startAboutActivity(getString(R.string.activity_action_material));
                Toast.makeText(MaterialWatchFaceConfigActivity.this, getString(R.string.please_check_phone), Toast.LENGTH_SHORT).show();
                return;
            }
            updateConfigDataItem(key,
                    !mProperties.get(selectedPosition).isShow(),
                    selectedPosition);
        }
    }

    @Override
    public void onTopEmptyRegionClick() {}
};

    private  WearableListView.OnScrollListener  mListSettingsOnScrollListener = new WearableListView.OnScrollListener() {
        @Override
        public void onScroll(int scroll) {}

        @Override
        public void onAbsoluteScrollChange(int scroll) {
            float newTranslation = Math.min(-scroll, 0);
          mHeader.setTranslationY(newTranslation);
        }

        @Override
        public void onScrollStateChanged(int scrollState) {}

        @Override
        public void onCentralPositionChanged(int centralPosition) {}
    };

    private TextView mHeader;
    private List<Property> mProperties;
    private  WearableListView configList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_config);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WatchFaceUtil.fetchConfigDataMap(mGoogleApiClient,
                WatchConstants.PATH_WITH_FEATURE_MATERIAL,
                mFetchCurrConfig);
    }


    private void initUI() {
        mHeader = (TextView) findViewById(R.id.headerMaterial_TextView_ActivityConfigWearable);
        configList = (WearableListView) findViewById(R.id.materialConfig_WearableListView_MaterialConfig);
        configList.setHasFixedSize(true);
        configList.setClickListener(mListSettingsClickListener);
        configList.addOnScrollListener(mListSettingsOnScrollListener);
    }

    private void fetchData(DataMap config) {
        mProperties = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.material_config_list_titles);
        TypedArray imagesOn = getResources().obtainTypedArray(R.array.material_config_logo_list_on);
        TypedArray imagesOff = getResources().obtainTypedArray(R.array.material_config_logo_list_off);
        for (int i = 0; i < titles.length; i++){
            try {
                Property property = new Property();
                property.setTitle(titles[i]);
                property.setPropertyKey(WatchConstants.MATERIAL_PROPERTIES_KEYS.get(i));
                property.setLogoOnId(imagesOn.getResourceId(i, -1));
                property.setLogoOffId(imagesOff.getResourceId(i, -1));
                if (config != null && i< WatchConstants.MATERIAL_PROPERTIES_KEYS.size())
                    property.setShow(config.getBoolean(WatchConstants.MATERIAL_PROPERTIES_KEYS.get(i), false));
                else{
                    property.setShow(false);
                }
                mProperties.add(property);
            }
            catch (Exception e){
                Log.d("cs_c", "Error fetchData Property: " + e.getMessage());
            }
         }
        imagesOn.recycle();
        imagesOff.recycle();
        configList.setAdapter(new ConfigMaterialAdapter(this, mProperties));
    }


    private void updateConfigDataItem(final String key, final boolean show, final int position) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(key,
                show);
        WatchFaceUtil.overwriteKeysInConfigDataMap(mGoogleApiClient,
                WatchConstants.PATH_WITH_FEATURE_MATERIAL,
                configKeysToOverwrite,
                  new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (dataItemResult.getStatus().isSuccess()){
                            mProperties.get(position).setShow(!mProperties.get(position).isShow());
                            configList.getAdapter().notifyDataSetChanged();
                        }
                    }
                });
    }
}
