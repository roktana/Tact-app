package com.tactile.tact.services;

import android.app.Activity;
import android.app.ActivityManager;

public class MemoryService {

	Activity context = null;
	
	public MemoryService(Activity context) {
		this.context = context;
	}
	
	/**
	 * Get The Maximum head size(bytes) this app can use before an error is thrown
	 * 
	 * @return Maximum head size(bytes)
	 */		   
	public long getMaxHeapSize() {
		Runtime rt = Runtime.getRuntime();
		Long maxMemory = rt.maxMemory();
		return maxMemory != null ? maxMemory.longValue() : 0L;
	}
	
	/**
	 * The recommended Max heap size(bytes) we should use depending on the
	 * device and the installed version of the OS
	 * 
	 * @return recommended Max Heap Size(bytes) 
	 */
	public long getRecommendedMaxHeapSize() {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass(); // recommended max heap usage in Mbytes
		return memoryClass*1048576L;
	}
	
	/**
	 * Get the memory currently available that we can use
	 * 
	 * @return memory available(bytes)
	 */
	public long getMemoryAvailable() {
		return getRecommendedMaxHeapSize() - getMemoryAllocated();
	}
	
	public long getMemoryAllocated() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

}
