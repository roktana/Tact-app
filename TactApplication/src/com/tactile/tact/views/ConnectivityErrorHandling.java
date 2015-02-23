package com.tactile.tact.views;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.tactile.tact.utils.Log;
import android.widget.ImageView;

import com.tactile.tact.R;


public class ConnectivityErrorHandling extends Activity {
	private static final String TAG = "ConnectivityErrorHandling";
    private static final int AUTO_FINISH_TIME = 10000; // 10 seconds
    //private static final int AUTO_FINISH_TIME = 3000; // For debug
    private static final boolean DEBUG = true;

    public final static String CONNECTIVITY_ERROR_TYPE = "CONNECTIVITY_ERROR_TYPE";
    public final static int CONNECTIVITY_ERROR_NO_CONNECTION = 0;
    public final static int CONNECTIVITY_ERROR_SLOW_CONNECTION = CONNECTIVITY_ERROR_NO_CONNECTION + 1;

    private BroadcastReceiver mReceiver;
    private ConnectivityManager mConnectManager;
	private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ImageView mErrorImage;
    private int mErrorType = CONNECTIVITY_ERROR_NO_CONNECTION;

    private Runnable mAutoFinishRunnable = new Runnable() {
        public void run() {
            Log.d(TAG, "Time is up! Finish ConnectivityErrorHandling Activity...");

            finish();
        }
    };

	/**
     * Get the current context
     * @return the current context
     */
    public Context getContext() {
    	return mContext;
    }



    //*********************  OVERRIDES METHODS  *********************\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.connectivity_error);

        mContext        = this;
        mConnectManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        mErrorType      = getIntent().getIntExtra(CONNECTIVITY_ERROR_TYPE, CONNECTIVITY_ERROR_NO_CONNECTION);

        if (DEBUG)
            Log.d(TAG, "onCreate: savedInstanceState = " + savedInstanceState + ", mErrorType = " + mErrorType);

        mErrorImage = (ImageView)findViewById(R.id.error_image);

        if (mErrorType == CONNECTIVITY_ERROR_NO_CONNECTION) {
            mErrorImage.setImageResource(R.drawable.not_connected);
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final boolean noConnectivity = intent.getBooleanExtra(
                            ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                    Log.d(TAG, "onReceive: noConnectivity = " + noConnectivity);

                    if (!noConnectivity)
                        finish();
                }
            };

            registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (DEBUG)
            Log.d(TAG, "onStart");

        if (mErrorType == CONNECTIVITY_ERROR_NO_CONNECTION) {
            NetworkInfo info = mConnectManager.getActiveNetworkInfo();

            if (info != null && info.isConnectedOrConnecting())
                finish();
        } else {
            mHandler.postDelayed(mAutoFinishRunnable, AUTO_FINISH_TIME);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (DEBUG)
            Log.d(TAG, "onStop");

        if (mErrorType >= CONNECTIVITY_ERROR_SLOW_CONNECTION) {
            mHandler.removeCallbacks(mAutoFinishRunnable);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (DEBUG)
            Log.d(TAG, "onDestroy");

        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mErrorType >= CONNECTIVITY_ERROR_SLOW_CONNECTION) {
            super.onBackPressed();
        }

        if (DEBUG)
            Log.d(TAG, "onBackPressed");
    }
}
