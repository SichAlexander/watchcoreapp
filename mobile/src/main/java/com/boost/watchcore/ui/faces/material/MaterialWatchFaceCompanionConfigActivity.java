package com.boost.watchcore.ui.faces.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.faces.BaseCompanionActivity;
import com.boost.watchcore.utils.Constant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * The phone-side config activity for {@code DigitalWatchFaceService}. Like the watch-side config
 * activity ({@code DigitalWatchFaceWearableConfigActivity}), allows for setting the background
 * color. Additionally, enables setting the color for hour, minute and second digits.
 */
public class MaterialWatchFaceCompanionConfigActivity extends BaseCompanionActivity{

    //Listeners
    private ResultCallback<DataApi.DataItemResult> mResultCallback = new ResultCallback<DataApi.DataItemResult>() {
        @Override
        public void onResult(DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
                Log.d("cs_c","DataApi.DataItemResult: Cool!!! ");
                DataItem configDataItem = dataItemResult.getDataItem();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
                DataMap config = dataMapItem.getDataMap();
                setUI(config);
            } else {
                Log.d("cs_c","DataApi.DataItemResult: Empty result ");
                setUI(null);
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallback = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle connectionHint) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnected: " + connectionHint);
            }
            if (mPeerId == null){
                Toast.makeText(MaterialWatchFaceCompanionConfigActivity.this, "ERROR " , Toast.LENGTH_SHORT).show();
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        if (!nodes.getNodes().isEmpty()){
                            mPeerId = nodes.getNodes().get(0).getId();
                            String mConnName = nodes.getNodes().get(0).getDisplayName();
                            Log.d("cs_c","GetConnectedNodesResult: mPeerId - " + mPeerId + " mConnName - " + mConnName);
                            getCurrConfig();
                        }
                        else{
                            Log.d("cs_c","GetConnectedNodesResult: Empty result ");
                            displayNoConnectedDeviceDialog();
                        }
                    }
                });
            }
            else{
                getCurrConfig();
            }

        }

        @Override
        public void onConnectionSuspended(int cause) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mCheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean isChecked) {
            switch (view.getId()){
                case R.id.italic_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_BACKGROUND_STYLE_ITALIC, isChecked);
                    break;
                case R.id.date_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_DATE_SHOW, isChecked);
                    break;
                case R.id.shadow_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_SHADOW_SHOW, isChecked);
                    break;
                case R.id.hour_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_HOURS_SHOW, isChecked);
                    break;
                case R.id.roman_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_ROMAN_SHOW, isChecked);
                    break;
                case R.id.nightMode_CheckBox_MaterialConfigCompanion:
                    sendConfigUpdateMessage(KEY_NIGHT_MODE, isChecked);
                    break;
            }
        }
    };

    //Views
    private CheckBox chItalic;
    private CheckBox chDate;
    private CheckBox chHour;
    private CheckBox chShadow;
    private CheckBox chRoman;
    private CheckBox chNightMode;

    //Settings params
    private static final String KEY_BACKGROUND_STYLE_ITALIC = "BACKGROUND_STYLE";
    private static final String KEY_DATE_SHOW = "DATE_SHOW";
    private static final String KEY_HOURS_SHOW = "HOURS_SHOW";
    private static final String KEY_SHADOW_SHOW= "SHADOW_SHOW";
    private static final String KEY_ROMAN_SHOW = "ROMAN_SHOW";
    private static final String KEY_NIGHT_MODE = "NIGHT_MODE";

    //Local vars
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MaterialConfigActivity";
    private String mPeerId;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_watch_face_config);
        initUI();
        trackScreen(getString(R.string.analytics_material_settings));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGoogleApiClient();
        fetchData();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallback)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    private void fetchData(){
        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
//        Toast.makeText(this, "Success " + mPeerId, Toast.LENGTH_SHORT).show();
    }

    private void initUI(){
        chItalic = (CheckBox) findViewById(R.id.italic_CheckBox_MaterialConfigCompanion);
        chDate = (CheckBox) findViewById(R.id.date_CheckBox_MaterialConfigCompanion);
        chShadow = (CheckBox) findViewById(R.id.shadow_CheckBox_MaterialConfigCompanion);
        chHour = (CheckBox) findViewById(R.id.hour_CheckBox_MaterialConfigCompanion);
        chRoman = (CheckBox) findViewById(R.id.roman_CheckBox_MaterialConfigCompanion);
        chNightMode = (CheckBox) findViewById(R.id.nightMode_CheckBox_MaterialConfigCompanion);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_material_watch_face_config);
        chItalic.setOnCheckedChangeListener(mCheckBoxListener);
        chDate.setOnCheckedChangeListener(mCheckBoxListener);
        chShadow.setOnCheckedChangeListener(mCheckBoxListener);
        chHour.setOnCheckedChangeListener(mCheckBoxListener);
        chRoman.setOnCheckedChangeListener(mCheckBoxListener);
        chNightMode.setOnCheckedChangeListener(mCheckBoxListener);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.material_config_text));
    }

    private void setUI(DataMap config){
       if (config != null) {
           chItalic.setChecked(config.getBoolean(KEY_BACKGROUND_STYLE_ITALIC, false));
           chShadow.setChecked(config.getBoolean(KEY_SHADOW_SHOW, false));
           chDate.setChecked(config.getBoolean(KEY_DATE_SHOW, false));
           chHour.setChecked(config.getBoolean(KEY_HOURS_SHOW, false));
           chRoman.setChecked(config.getBoolean(KEY_ROMAN_SHOW, false));
           chNightMode.setChecked(config.getBoolean(KEY_NIGHT_MODE, false));
       }
   }

    private void displayNoConnectedDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = getResources().getString(R.string.title_no_device_connected);
        String okText = getResources().getString(R.string.ok_no_device_connected);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(okText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getCurrConfig(){
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("wear").path(Constant.PATH_WITH_FEATURE_MATERIAL).authority(mPeerId).build();
        Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(mResultCallback);
    }

    private void sendConfigUpdateMessage(String configKey, boolean isNeed) {
        if (mPeerId != null) {
            DataMap config = new DataMap();
            config.putBoolean(configKey, isNeed);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Constant.PATH_WITH_FEATURE_MATERIAL, rawData);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> "
                        + isNeed);
            }
        }
    }
}
