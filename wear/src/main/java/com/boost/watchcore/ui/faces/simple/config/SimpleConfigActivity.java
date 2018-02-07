
package com.boost.watchcore.ui.faces.simple.config;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.watchcore.ConfigurationBaseActivity;
import com.boost.watchcore.R;


public class SimpleConfigActivity extends ConfigurationBaseActivity {

    private View.OnClickListener mAboutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startAboutActivity(getString(R.string.activity_action_simple));
            Toast.makeText(SimpleConfigActivity.this, getString(R.string.please_check_phone), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
        initUI();
    }

    private void initUI() {
        ((TextView) findViewById(R.id.title_TextView_ActivityConfigWearable)).setText(getString(R.string.simple_name));
        findViewById(R.id.about_TextView_ActivityConfigWearable).setOnClickListener(mAboutListener);
    }
}
