package com.tactile.tact.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.tactile.tact.R;

public class ProgressSpinner {
    static final int PROGRESS_NOTIFICATION_ID = 10001;
    static NotificationManager notificationManager = null;
	static AtomicInteger requestCount = new AtomicInteger();
	static AtomicBoolean runningThread = new AtomicBoolean();
    
    public static void start(Context context) {
    	if (requestCount.getAndIncrement() == 0 && !runningThread.get()) {
    		runningThread.set(true);
	    	notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    	final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
	    	builder.setContentTitle("Tact").setContentText("Fetching Data").setProgress(0, 0, true);
	    	builder.setSmallIcon(R.drawable.progress_spinner, 0);
	    	new Thread(
	    	    new Runnable() {
	    	        @Override
	    	        public void run() {
	    	        	int progressCounter = 0;
	    	        	while (true) {
	    	        		try {
	    	        			Thread.sleep(100);
	    	        		}
	    	        		catch (InterruptedException e) {
	    	        			// ignore
	    	        		}
	    	        		builder.setSmallIcon(R.drawable.progress_spinner, ++progressCounter % 9);
	    	        		int currentRequestCount = requestCount.get();
	    	        		builder.setContentText("Fetching Data! (" + currentRequestCount + " active requests.");
	    	        		if (currentRequestCount > 0) {
	    	        			notificationManager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
	    	        		}
	    	        		else {
	    	        	    	notificationManager.cancel(PROGRESS_NOTIFICATION_ID);
	    	        			runningThread.set(false);
	    	        			break;
	    	        		}
	    	        	}
	    	        }
	    	    }
	    	).start();
    	}
    }
    
    public static void stop() {
    	requestCount.decrementAndGet();
    }
}
