package com.tactile.tact.activities;

import android.content.Intent;
import android.os.Bundle;;
import android.view.View;

import com.tactile.tact.R;
import com.tactile.tact.controllers.TactSharedPrefController;
import com.tactile.tact.services.events.EventCheckInitialDBError;
import com.tactile.tact.services.events.EventCheckInitialDBSuccess;
import com.tactile.tact.services.events.EventStartOnboarding;
import com.tactile.tact.services.events.EventTempLoginError;
import com.tactile.tact.services.events.EventTempLoginSuccess;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.Utils;

import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class LandingActivity extends TactBaseFragmentActivity {

    private Button mGetMeStarted;
    private LinearLayout progresLayout;
    private ProgressBar progress_bar;

    //******************EVENTS*******************************************************************

    /**
     * Handle the event to start the onboarding flow after a correct login
     * @param eventStartOnboarding EventStartOnboarding
     */
    public void onEvent(EventStartOnboarding eventStartOnboarding) {
        EventBus.getDefault().removeStickyEvent(eventStartOnboarding);
        startActivity(new Intent(LandingActivity.this, OnBoardingActivity.class));
        finish();
    }

    /**
     * Handles the event after the verification of the database status as a check to the server call
     * @param eventCheckInitialDBSuccess - Event object
     */
    public void onEvent(EventCheckInitialDBSuccess eventCheckInitialDBSuccess) {
        EventBus.getDefault().removeStickyEvent(eventCheckInitialDBSuccess);
        EventBus.getDefault().postSticky(new EventStartOnboarding());
    }

    /**
     * Handles the event after a error checking db status as a server connection check
     * @param eventCheckInitialDBError - Event object
     */
    public void onEvent(EventCheckInitialDBError eventCheckInitialDBError) {
        EventBus.getDefault().removeStickyEvent(eventCheckInitialDBError);
        EventBus.getDefault().postSticky(new EventTempLoginError("Error trying to check db status with message: " + eventCheckInitialDBError.getError().getMessage()));
    }

    /**
     * Handles the event when the temporal login failed
     * @param eventTempLoginError - Event object
     */
    public void onEvent(EventTempLoginError eventTempLoginError) {
        EventBus.getDefault().removeStickyEvent(eventTempLoginError);
        hideProgress();
        Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the event when the temporal login succeed
     * @param eventTempLoginSuccess - Event object
     */
    public void onEvent(EventTempLoginSuccess eventTempLoginSuccess) {
        EventBus.getDefault().removeStickyEvent(eventTempLoginSuccess);
        TactNetworking.callCheckDatabaseStatus(true, this);
    }
//********************************0*****************************************************************

    /**
     * Show the swirl in the button of "Create Account" when login is executing
     */
    private void showProgress() {
        mGetMeStarted.setVisibility(View.GONE);
        mGetMeStarted.setClickable(false);
        progresLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the swirl in the button of "Create Account" when login is not executing
     */
    public void hideProgress() {
        mGetMeStarted.setVisibility(View.VISIBLE);
        progresLayout.setVisibility(View.GONE);
        mGetMeStarted.setClickable(true);
    }


    //******************* OVERRIDES *********************\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.landing);

        ImageView landingImg = (ImageView)findViewById(R.id.landing_img);
        landingImg.setImageBitmap(Utils.decodeSampledBitmapFromResource(this.getResources(), R.drawable.intro_device_background, Utils.getDisplaySize(this)[0], Utils.getDisplaySize(this)[0]));

        if (TactSharedPrefController.getOnboardingStep() != -1) { // If onboarding flow was started and not finished
            EventBus.getDefault().postSticky(new EventStartOnboarding());
        }
        else {

            //        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            //            @Override
            //            public int getCount() {
            //                return 6;
            //            }
            //
            //            @Override
            //            public Fragment getItem(int position) {
            //                Fragment fragment = new FragmentImage();
            //                Bundle args = new Bundle();
            //                args.putInt("identifier", position);
            //                fragment.setArguments(args);
            //                return fragment;
            //            }
            //        };
            //
            //        // wrap pager to provide infinite paging with wrap-around
            //        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);
            //
            //        // actually an InfiniteViewPager
            //        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            //        viewPager.setAdapter(wrappedAdapter);

            TextView btn_login = (TextView) findViewById(R.id.log_me_in);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if create account is processing, we must enable create account button
                    hideProgress();
                    //Log in
                    Intent loginIntent = new Intent(LandingActivity.this, TactLoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            });

            mGetMeStarted = (Button) findViewById(R.id.get_me_started);
            progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
            progresLayout = (LinearLayout) findViewById(R.id.progress_layout);
            mGetMeStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TactSharedPrefController.isTempLoggedIn()) { // If onboarding flow was started and not finished
                        EventBus.getDefault().postSticky(new EventStartOnboarding());
                    } else if (TactNetworking.checkInternetConnection(true)) {
                        showProgress();
                        TactNetworking.callTempLogin(LandingActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onBackPressed(){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TactApplication.unbindDrawables(findViewById(R.id.lyt_main));
        mGetMeStarted = null;
        progress_bar = null;
        System.gc();
    }

}
