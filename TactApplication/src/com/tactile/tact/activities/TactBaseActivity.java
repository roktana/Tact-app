package com.tactile.tact.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;
import com.tactile.tact.services.TactCUDLSyncService;
import com.tactile.tact.services.events.EventConfirmLogout;
import com.tactile.tact.services.events.EventGoHome;
import com.tactile.tact.services.events.EventGoToSync;
import com.tactile.tact.services.events.EventLogout;
import com.tactile.tact.services.events.EventNoInternet;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;

import java.util.concurrent.ExecutionException;

import de.greenrobot.event.EventBus;

/**
 * Created by leyan on 10/27/14.
 */
public class TactBaseActivity extends Activity {

    public void onEvent(Object e) {

    }

    public void onEventMainThread(EventNoInternet eventNoInternet) {
        EventBus.getDefault().removeStickyEvent(eventNoInternet);
        if (eventNoInternet.isCritical()) {
            Utils.setConnectionErrorMessage(this);
        }
    }

    public void onEventMainThread(EventGoToSync eventGoToSync) {
        Intent intentResult = new Intent(this, LoginSyncActivity.class);
        startActivity(intentResult);
    }

    public void onEventMainThread(EventGoHome eventGoHome) {
        Intent intentResult = new Intent(this, HomeActivity.class);
        startActivity(intentResult);
    }

    public void onEventMainThread(EventConfirmLogout eventConfirmLogout)
    {
        TactDialogHandler dialog = new TactDialogHandler(this);
        dialog.showConfirmation(eventConfirmLogout.getMessage(), getResources().getString(R.string.are_you_sure),
                getResources().getString(R.string.message_button_ok), getResources().getString(R.string.message_button_no),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(new EventLogout());
                    }
                });
    }

    public void onEventMainThread(EventLogout eventLogout)
    {
        Utils.logout(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
        TactApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        TactApplication.activityPaused();
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
        EventBus.getDefault().registerSticky(this);
    }

    protected void tryRemoveKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
