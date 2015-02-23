package com.tactile.tact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.tactile.tact.R;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.events.EventGoHome;
import com.tactile.tact.services.events.EventGoToSync;
import com.tactile.tact.utils.Utils;

import de.greenrobot.event.EventBus;


public class SplashActivity extends TactBaseActivity {
    private static final int SPLASH_TIMEOUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView splashImg = (ImageView)findViewById(R.id.splash_image);
        splashImg.setImageBitmap(Utils.decodeSampledBitmapFromResource(this.getResources(), R.drawable.splash, Utils.getDisplaySize(this)[0], Utils.getDisplaySize(this)[0]));
    }

    @Override
    public void onResume() {
        super.onResume();
        SplashActivity.this.setVisible(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (TactSharedPrefController.isLoggedIn()) {
                    if (TactSharedPrefController.isInitialSynced()) {
                        EventBus.getDefault().post(new EventGoHome());
                    } else {
                        EventBus.getDefault().post(new EventGoToSync());
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LandingActivity.class));
                }
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        TactApplication.unbindDrawables(findViewById(R.id.splash_layout));
    }
}
