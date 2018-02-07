package com.boost.watchcore.about;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.boost.watchcore.Application;
import com.boost.watchcore.R;
import com.boost.watchcore.about.adapter.AboutImagePagerAdapter;
import com.boost.watchcore.utils.Pref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

/**
 * Created by cs on 03.08.15.
 */
public class BaseAboutActivity extends AppCompatActivity {
    private final String TAG = BaseAboutActivity.class.getSimpleName();
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ImageView ivZeroLayer;
    private Toolbar mToolbar;
    private List<Integer> mAboutImg;
    private List<String> mAboutDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findUI();
        initToolBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViewPager();
    }

    private void findUI() {
        ivZeroLayer = (ImageView)findViewById(R.id.main_image_iv_activity_about);
        mToolbar    = (Toolbar) findViewById(R.id.toolbar_activity_about);
    }

    protected void setImgResources(List<Integer> imgRes){
        mAboutImg = imgRes;
    }

    protected void setDescriptionResources(List<String> description) {
        mAboutDescription = description;
    }

    protected void setIvZeroLayer(int imgRes){
        ivZeroLayer.setImageResource(imgRes);
    }

    protected void setTitle(String watchName){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.about) + " " + watchName);
        }
    }

    private void initViewPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        if (mAboutImg == null || mAboutDescription == null){
            Log.d(TAG, "Error: \nmAboutImg == null || mAboutDeascription == null");
        }
        PagerAdapter pagerAdapter = new AboutImagePagerAdapter(this, mAboutImg, mAboutDescription);
        ViewPager pager = (ViewPager) findViewById(R.id.images_vp_activity_about);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setAdapter(pagerAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
    }

    protected void trackScreen(String screenName) {
        ((Application)getApplication()).trackScreen(screenName);
    }
}
