package com.tactile.tact.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tactile.tact.services.events.EventNoInternet;
import com.tactile.tact.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 10/27/14.
 * Base Activity
 */
public class TactBaseFragmentActivity extends FragmentActivity {


    public void onEvent(Object e) {

    }

    public void onEventMainThread(EventNoInternet eventNoInternet) {
        EventBus.getDefault().removeStickyEvent(eventNoInternet);
        if (eventNoInternet.isCritical()) {
            Utils.setConnectionErrorMessage(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
