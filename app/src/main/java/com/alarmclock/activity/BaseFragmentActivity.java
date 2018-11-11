package com.alarmclock.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.alarmclock.R;

import java.util.List;

public class BaseFragmentActivity extends AppCompatActivity {

    protected Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Fragment startFragment(Fragment f, boolean addToBackStack) {
        return startFragment(f, addToBackStack, false);
    }

    public Fragment startFragment(Fragment f, boolean addToBackStack, boolean popBackStack) {
        currentFragment = startFragment(R.id.container, f, addToBackStack, popBackStack);
        return currentFragment;
    }

    protected Fragment startFragment(int viewId, Fragment f, boolean addToBackStack, boolean popBackStack) {
        String tag = f.getClass().getSimpleName();

        if (currentFragment != null && currentFragment.getClass().getSimpleName().equals(tag)) {
            return currentFragment;
        }
        FragmentManager manager = getSupportFragmentManager();

        if (popBackStack) {
            List<Fragment> fragments = manager.getFragments();
            int index = -1;
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fr = fragments.get(i);
                if (fr != null && fr.getClass().getSimpleName().equals(tag)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                int popCount = fragments.size() - index - 1;
                for (int i = 0; i < popCount; i++) {
                    manager.popBackStackImmediate();
                }
                return fragments.get(index);
            }
        }

        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(viewId, f, tag);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        ft.commit();
        return f;
    }
}