
package com.boost.watchcore.ui.faces.pizza.config;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boost.watchcore.ConfigurationBaseActivity;
import com.boost.watchcore.R;


public class PizzaConfigActivity extends ConfigurationBaseActivity {

    private View.OnClickListener mAboutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startAboutActivity(getString(R.string.activity_action_pizza));
            Toast.makeText(PizzaConfigActivity.this, getString(R.string.please_check_phone), Toast.LENGTH_SHORT).show();
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
        ((TextView) findViewById(R.id.title_TextView_ActivityConfigWearable)).setText(getString(R.string.pizza_name));
        findViewById(R.id.about_TextView_ActivityConfigWearable).setOnClickListener(mAboutListener);
    }
}
