package com.tactile.tact.utils.interfaces;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tactile.tact.utils.ObservableScrollView;

/**
 * Created by ismael on 11/25/14.
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy);
    void onParallaxScrollChanged(ParallaxScrollView observableScrollView, int x, int y, int oldx, int oldy);
}
