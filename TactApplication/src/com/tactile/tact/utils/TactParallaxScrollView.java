package com.tactile.tact.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.tactile.tact.utils.interfaces.ScrollViewListener;

/**
 * Created by ismael on 12/6/14.
 */
    public class TactParallaxScrollView extends ParallaxScrollView{

    private ScrollViewListener scrollViewListener = null;

    public TactParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TactParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TactParallaxScrollView(Context context) {
        super(context);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onParallaxScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
