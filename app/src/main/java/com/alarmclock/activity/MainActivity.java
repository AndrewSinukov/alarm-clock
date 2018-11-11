package com.alarmclock.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alarmclock.Constants;
import com.alarmclock.R;
import com.alarmclock.fragment.MainFragment;
import com.alarmclock.fragment.SettingApplicationFragment;
import com.alarmclock.helper.PreferenceHelper;

public class MainActivity extends BaseFragmentActivity {

    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragmen_container);

        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString(Constants.SHARED_PREF.APP_LANGUAGE, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        preferenceHelper.setLocale(language);
        if (savedInstanceState == null) {
            if (currentFragment == null) {
                startFragment(new MainFragment(), false);
            } else {
                startFragment(currentFragment, true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem splashItem = menu.findItem(R.id.actionSplash);
        splashItem.setChecked(preferenceHelper.getBoolean(Constants.SHARED_PREF.SPLASH_IS_INVISIBLE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSplash:
                item.setChecked(!item.isChecked());
                preferenceHelper.putBoolean(Constants.SHARED_PREF.SPLASH_IS_INVISIBLE, item.isChecked());
                break;
            case R.id.settingApplication:
                startFragment(new SettingApplicationFragment(), true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}