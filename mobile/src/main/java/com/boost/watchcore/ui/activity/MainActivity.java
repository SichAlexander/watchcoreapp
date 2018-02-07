package com.boost.watchcore.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.adapters.FragmentListAdapter;
import com.boost.watchcore.ui.fragment.WatchListFragment;
import com.boost.watchcore.utils.Constant;
import com.boost.watchcore.utils.Network;
import com.boost.watchcore.utils.Pref;
import com.boost.watchcore.voting.Voting;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.boost.watchcore";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "watchface.com";
    // The account name
    public static final String ACCOUNT = "WatchFaceAccount";
    private final String CAPABILITY_KEY         = "boost_watch_faces";
    // Instance fields
    Account mAccount;
    public static final long SYNC_INTERVAL = 259200; //= 3 day
    private final int UPDATE_TIME = 5000;
    private String mPeerId;
    private final int MSG_UPDATE_TIME = 0;
    private boolean mIsConnectedPlayService = false;
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mWifiReceiver;
    private Spinner spWatches;
    private Dialog dNoDevices;

    private GoogleApiClient.ConnectionCallbacks mConnectionCallback = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle connectionHint) {
            //Get capability list
            mIsConnectedPlayService = true;

            getCapabilityList();
            registerWatchListenerTimer();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d("cs", "onConnectionSuspended " + cause);
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.d("cs", "onConnectionFailed " + result.describeContents());
            mGoogleApiClient.connect();

        }
    };

    private CapabilityApi.CapabilityListener mCapabilityListener = new CapabilityApi.CapabilityListener() {
        @Override
        public void onCapabilityChanged(final CapabilityInfo capabilityInfo) {
            Log.d("cs_c", "CapabilityListener " + capabilityInfo.getNodes().size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setWatchList(capabilityInfo.getNodes());
                }
            });
        }
    };

    private final Handler mUpdateTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_UPDATE_TIME:
                    Log.d("cs_c" , "mUpdateTimeHandler " );
                    getCapabilityList();
                    if (shouldTimerBeRunning()) {
                        long timeMs = System.currentTimeMillis();
                        long delayMs = UPDATE_TIME
                                - (timeMs % UPDATE_TIME);
                        mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initSyncAdapter();
        initListTabFragments();
        Voting voting = new Voting(this);
        voting.tryVoting();
        initGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_layout, menu);
        View view  = menu.findItem(R.id.watch_list_menu).getActionView();
        spWatches = (Spinner)view.findViewById(R.id.watches_Spinner_menuItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterWiFiReceiver();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            mPeerId = "";
            try {
                Wearable.CapabilityApi.removeCapabilityListener(mGoogleApiClient,
                        mCapabilityListener, CAPABILITY_KEY);
            }
            catch (Exception e){
                Log.d("cs_c", e.getMessage());
            }
        }
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
    }

    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(mConnectionCallback)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .addApi(Wearable.API)
                .build();

    }

    private void initListTabFragments() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragmentList_vp_activity_main);
        if (viewPager != null) {
            setupViewPager(viewPager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_tl_activity_main);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_main);
        setSupportActionBar(toolbar);
    }
    private boolean shouldTimerBeRunning() {
        return mIsConnectedPlayService && (mPeerId == null || mPeerId.isEmpty());
    }

    private void registerWatchListenerTimer() {
        //
        Wearable.CapabilityApi.addCapabilityListener(mGoogleApiClient,
                mCapabilityListener, CAPABILITY_KEY);
        //

        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
        if (shouldTimerBeRunning()) {
            mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        }
    }

    private void setWatchList(Set<Node> watchesNodes){
        if (watchesNodes == null || watchesNodes.size() == 0){
            watchesNodes = new HashSet<>();
            displayNoConnectedDeviceDialog();
        }

        final List<Node> watches = new ArrayList<>(watchesNodes);
        List<String> names = new ArrayList<>();
        for (Node node: watches){
            names.add(node.getDisplayName());
        }

        if (names.isEmpty()){
            names.add(getString(R.string.sp_no_devices));
        }
        names.add(getString(R.string.sp_settings));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spiner_item,
                R.id.watch_name_Spinner_spinnerItem,
                names);
        mPeerId = watchesNodes.size()>0 ? watches.get(0).getId() : "";
        sendWatchId();
        if (spWatches != null) {
            spWatches.setAdapter(adapter);
            spWatches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (view == null)
                        return;
                    String itemText = ((TextView)view.findViewById(R.id.watch_name_Spinner_spinnerItem)).getText().toString();
                    if (itemText == null || itemText.isEmpty() || itemText.equals(getString(R.string.sp_no_devices))){
                        return;
                    }
                    if (itemText.equals(getString(R.string.sp_settings))){
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        return;
                    }
                    if (watches.size() > 0) {
                        mPeerId = watches.get(position).getId();
                        sendWatchId();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void sendWatchId(){
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_CHANGED_WATCHES);
        intent.putExtra(Constant.BUNDLE_WATCH_ID, mPeerId);
        sendBroadcast(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentListAdapter adapter = new FragmentListAdapter(getSupportFragmentManager());
        adapter.addFragment(new  WatchListFragment(), getString(R.string.tab_all));
        adapter.addFragment(WatchListFragment.newInstance(0, mPeerId), getString(R.string.tab_paid));
        adapter.addFragment(WatchListFragment.newInstance(1, mPeerId), getString(R.string.tab_free));
        viewPager.setAdapter(adapter);

    }

    private void getCapabilityList(){
        Wearable.CapabilityApi.getCapability(
                mGoogleApiClient, CAPABILITY_KEY,
                CapabilityApi.FILTER_REACHABLE).setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
            @Override
            public void onResult(CapabilityApi.GetCapabilityResult getCapabilityResult) {
                Log.d("cs_c", "getCapabilityList " + getCapabilityResult.getCapability().getNodes().size());
                setWatchList(getCapabilityResult.getCapability().getNodes());
            }
        });
    }

    /**
     * Show Dialog when we have no internet connection.
     *
     * Ok       - register wifi receiver;
     *          - turn on wifi.
     *
     * Cancel   - close activity.
     */
    private void createInternetDialog(){
        final Dialog noInternetDialog = new Dialog(this);
        noInternetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet, null);
        noInternetDialog.setContentView(dialogView);
        dialogView.findViewById(R.id.turnWIFI_Button_NoInternetDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Network.changeWifiState(MainActivity.this, true);
                initWifiReceiver();
                registerWiFiReceiver();
                noInternetDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.cancel_Button_NoInternetDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetDialog.dismiss();
            }
        });
        noInternetDialog.show();
    }

    private void displayNoConnectedDeviceDialog() {
        if (dNoDevices != null)
            return;
        dNoDevices = new Dialog(this);
        dNoDevices.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_no_watches, null);
        dNoDevices.setContentView(dialogView);
        dialogView.findViewById(R.id.ok_Button_NoDevicesDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dNoDevices.dismiss();
            }
        });
        dNoDevices.show();
    }


    //region SyncAdapter
    private void initSyncAdapter(){
        // Create the dummy account
        mAccount = CreateSyncAccount(this);

        if (Pref.getEndTimeStamp(this) == 0) {
            // Pass the settings flags by inserting them in a bundle
            if (Network.isInternetConnectionAvailable(this)) {
                Bundle settingsBundle = new Bundle();
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_MANUAL, true);
                settingsBundle.putBoolean(
                        ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
                ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);


                ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
                ContentResolver.addPeriodicSync(
                        mAccount,
                        AUTHORITY,
                        Bundle.EMPTY,
                        SYNC_INTERVAL);  //SYNC_INTERVAL
            } else {
                createInternetDialog();
            }
        }
    }
    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    /**
     * Register receiver with intent filters for monitoring wifi connection.
     *
     */
    private void registerWiFiReceiver(){
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mWifiReceiver, filter);
    }

    /**
     * If WiFiReceiver registered -> unregister Receiver.
     */
    private void unRegisterWiFiReceiver() {
        if (mWifiReceiver != null)
            unregisterReceiver(mWifiReceiver);
            mWifiReceiver = null;
    }

    /**
     *   Init Wifi receiver
     *   - if wifi connected -> initSyncAdapter
     */
    private void initWifiReceiver(){
        this.mWifiReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    initSyncAdapter();
                    Log.d("WifiReceiver", "Have Wifi Connection");
                }
                else
                    Log.d("WifiReceiver", "Don't have Wifi Connection");
            }
        };
    }
//    endregion

}
